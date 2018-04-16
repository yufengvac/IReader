package com.yufeng.ireader.reader.utils;

import android.text.TextUtils;
import android.util.Log;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

import java.io.File;
import java.io.IOException;


class TextCharsetDetector {
    private static final String TAG = TextCharsetDetector.class.getSimpleName();

    private boolean found = false;
    private String encoding = null;

    private static String[][] fileCodeBuffer = null;


    /* IsUTF8
     *
     * UTF-8 is the encoding of Unicode based on Internet Society RFC2279
     * ( See http://www.cis.ohio-state.edu/htbin/rfc/rfc2279.html )
     *
     * Basicly:
     * 0000 0000-0000 007F - 0xxxxxxx  (ascii converts to 1 octet!)m
     * 0000 0080-0000 07FF - 110xxxxx 10xxxxxx    ( 2 octet format)
     * 0000 0800-0000 FFFF - 1110xxxx 10xxxxxx 10xxxxxx (3 octet format)
     * 					   - 1111xxxx 10xxxxxx 10xxxxxx 10xxxxxx(4 octet format)
     *
     */
    private static boolean IsUTF8(byte[] str, int length) {
        int i;
        byte cOctets; // octets to go in this UTF-8 encoded character
        byte chr;

        cOctets = 0;
        boolean bAllAscii = true;
        int checkCount = 0;
        for (i = 0; i < length; i++) {
            chr = str[i];
            if (i > 0) {
                int data1 = str[i - 1] & 0xC0;
                int data2 = str[i] & 0xC0;
                // 10xx xxxx 不能是UTF8的第一个字符
                if (i == 1 && data1 == 0x80) {
                    return false;
                }
                // 10xx xxxx 前 必须是 11xx xxxx 或者 10xx xxxx
                if (data1 != 0xc0 && data1 != 0x80 && data2 == 0x80) {
                    return false;
                }
                // 11xx xxxx 后必须是 10xx xxxx
                if (data1 == 0xc0 && data2 != 0x80) {
                    return false;
                }

                if (i + 2 < length) {
                    data1 = str[i - 1] & 0xe0;
                    int data3 = str[i + 1] & 0xe0;
                    int data4 = str[i + 2] & 0xf0;
                    // 110x xxxx  10xx xxxx  10xx xxxx  是不允许的
                    if (data1 == 0xc0 && data3 == 0x80) {
                        return false;
                    }
                    // 1110 xxxx  10xx xxxx  10xx xxxx  10xx xxxx 是不允许的
                    if (data1 == 0xe0 && data4 == 0x80) {
                        return false;
                    }
                }
            }
            if ((chr & 0x80) != 0) {
                checkCount += 1;
                bAllAscii = false;
            }

            if (checkCount > 32 && !bAllAscii && i >= 36 && chr < 'z' && chr >= 0)
                break;
            if (cOctets == 0) {
                if (chr >= 0x80) {
                    do {
                        chr <<= 1;
                        cOctets++;
                    } while ((chr & 0x80) != 0);

                    cOctets--;
                    if (cOctets == 0) {
                        return false;
                    }
                }
            } else {
                if ((chr & 0xC0) != 0x80) {
                    return false;
                }

                cOctets--;
            }
        }

        if (cOctets > 0) {
            return false;
        }

        if (bAllAscii) {
            return false;
        }

        return true;
    }

    private String getFileCodeFromBuffer(String keyString) {
        if (fileCodeBuffer == null) {
            fileCodeBuffer = new String[64][2];
            for (int i = 0; i < fileCodeBuffer.length; i++) {
                fileCodeBuffer[i] = new String[]{null, null};
            }
            return null;
        } else {
            for (int i = 0; i < fileCodeBuffer.length; i++) {
                if (fileCodeBuffer[i][0] != null && fileCodeBuffer[i][0].equals(keyString)) {
                    return fileCodeBuffer[i][1];
                }
            }
        }

        return null;
    }

    private void addFileCode(String keyString, String code) {
        if (fileCodeBuffer != null) {
            for (int i = 0; i < fileCodeBuffer.length; i++) {
                if (fileCodeBuffer[i][0] == null) {
                    fileCodeBuffer[i][0] = keyString;
                    fileCodeBuffer[i][1] = code;
                    fileCodeBuffer[(i + 1) % fileCodeBuffer.length][0] = null;
                    break;
                }
            }
        }
    }

    /**
     * 传入一个文件(File)对象，检查文件编码
     *
     * @param file File对象实例
     * @return 文件编码，若无，则返回null
     */
    String guestFileEncoding(File file) throws IOException {

        String keyString = file.length() + file.lastModified() + file.getAbsolutePath();
        encoding = getFileCodeFromBuffer(keyString);
        if (!TextUtils.isEmpty(encoding)) {
            return encoding;
        }

        nsDetector det = new nsDetector();
        encoding = null;
        det.Init(new nsICharsetDetectionObserver() {
            public void Notify(String charset) {
                found = true;
                encoding = charset;
            }
        });

        RandomAccessFileInputStream imp = null;
        try {
            imp = new RandomAccessFileInputStream(file.getAbsolutePath());
            byte[] buf = new byte[1024 * 2];
            int len;
            encoding = "";
            imp.seek(0);
            if ((len = imp.read(buf, 0, buf.length)) != -1) {
                if (IsUTF8(buf, len)) {
                    encoding = "UTF-8";
                    Log.i(TAG, "guess utf 8");
                } else if (det.isAscii(buf, len)) {
                    encoding = "ASCII";
                } else if (!det.DoIt(buf, len, false)) {
                    long currentPos = len;
                    while (!isGoodBig5GbkJudgeBuf(buf) && file.length() - currentPos > buf.length) {
                        imp.seek(currentPos);
                        len = imp.read(buf, 0, buf.length);
                        currentPos += len;
                    }
                    int ret = judgeGBKorBig5(buf);
                    if (ret == 1) {
                        encoding = "GBK";
                    } else if (ret == 2) {
                        encoding = "Big5";
                    }
                }
            }
        } finally {
            if (imp != null) {
                try {
                    imp.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (encoding.length() == 0) {
            det.DataEnd();    //最后要调用此方法，此时，Notify被调用。
        } else {
            found = true;
        }

        Log.i(TAG, encoding);

        if (!found) {
            String prob[] = det.getProbableCharsets();
            if (prob.length > 0) {
                encoding = prob[0].toUpperCase();
            } else {
                addFileCode(keyString, "GBK");
                return "GBK";
            }
        }
        Log.d(TAG, encoding);
        addFileCode(keyString, encoding);
        return encoding;
    }

    private boolean isGoodBig5GbkJudgeBuf(byte[] buf) {
        int size = buf.length;
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (buf[i] < 0) {
                count++;
            }
        }
        return (count > 100);
    }

    /**
     * 判断是GBK 还是 BIG5编码
     *
     * @return -1 无法判断，1 GBK , 2 BIG5
     */
    private int judgeGBKorBig5(byte[] buf) {
        if (buf == null || buf.length < 2) {
            return -1;
        }

        int testLength = buf.length;
        if (testLength > 2048) {
            testLength = 2048;
        }
        // 特殊字符辅助判断
        int a1a2Count = 0;    // gbk 中的  "、"  big5中的 "〔"
        int a1a3Count = 0;  // gbk 中的  "。"  big5中的 "〕"

        int a1Count = 0;    // 符号区
        int sum = 0;
        int firstByte;
        int secondByte;
        int count = 0;
        for (int i = 0; i < testLength - 2; i += 2) {
            firstByte = buf[i] & 0xff;
            secondByte = buf[i + 1] & 0xff;

            if (firstByte == 0x0d || firstByte == 0x0a) {
                i -= 1;
                continue;
            }
            if (firstByte <= 0x80) {
                i -= 1;
                continue;
            }
            if (firstByte < 0xa0 || firstByte > 0xfe
                    || secondByte < 0x40 || (secondByte > 0x7e && secondByte < 0xa1)) {
                return 1;
            }

            if (firstByte == 0xa1) {
                a1Count += 1;
                if (secondByte == 0xa2) {
                    a1a2Count += 1;
                } else if (secondByte == 0xa3) {
                    a1a3Count += 1;
                }
            }
            sum += firstByte;
            count += 1;
        }

        sum = sum / count;

        if (count < 200 && (a1Count * 100 / count) > 30) {
            // 在采样中符号占有较多时进行判断，（不是对称的括号，并且有一定数量的"、"或者" 。" 认为是GBK编码）
            if (a1a2Count != a1a3Count && (a1a2Count > 3 || a1a3Count > 3)) {
                Log.i(TAG, "check gbk by 0xa1xx");
                return 1;
            }
        }

        if (sum > 184) {
            return 1;
        } else {
            return 2;
        }
    }

}
