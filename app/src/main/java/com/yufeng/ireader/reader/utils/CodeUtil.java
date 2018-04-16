package com.yufeng.ireader.reader.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by yufeng on 2018/4/15.
 *
 */

public class CodeUtil {
    private static final String TAG = CodeUtil.class.getSimpleName();
    private static final int UNKNOWN = 0;
    private static final int ANSI = 1;
    private static final int UTF16_LE = 2;
    private static final int UTF8 = 3;
    private static final int UTF16_BE = 4;
    private static final int BIG5 = 5;

    @SuppressWarnings("InjectedReferences")
    public static final String[] ENCODINGS = new String[]{"", "gb2312", "UTF16-LE", "utf-8", "UTF16-BE", "Big5"};
    /**
     转半角的函数(DBC case)<br/><br/>
     全角空格为12288，半角空格为32
     其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     * @param input 任意字符串
     * @return 半角字符串
     *
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                //全角空格为12288，半角空格为32
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) {
                //其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
                c[i] = (char) (c[i] - 65248);
            }
        }

        return new String(c);
    }


    public static int regCode(String filename) {
        if (filename == null) {
            return UNKNOWN;
        }

        File file = new File(filename);
        if (!file.exists()) {
            Log.e("CodeUtil","regCode(String filename) file not exits");
            return UNKNOWN;
        }

        RandomAccessFileInputStream bInputStream = null;
        InputStream is = null;
        int code = ANSI;

        try {
            bInputStream = new RandomAccessFileInputStream(filename);
            bInputStream.seek(0);

            int p = (bInputStream.read() << 8) + bInputStream.read();
            switch (p) {
                case 0xefbb:
                    code = UTF8;
                    break;
                case 0xfffe:
                    code = UTF16_LE;
                    break;
                case 0xfeff:
                    code = UTF16_BE;
                    break;
            }

            if (code == ANSI) {
                String chset = new TextCharsetDetector().guestFileEncoding(file);
                if (chset.equals("GB18030")) {
                    code = ANSI;
                } else if (chset.equals("GB2312")) {
                    code = ANSI;
                } else if (chset.equals("GBK")) {
                    code = ANSI;
                } else if (chset.equals("UTF-8")) {
                    code = UTF8;
                } else if (chset.equals("Big5")) {
                    code = BIG5;
                } else if (chset.equals("UTF-16LE")) {
                    code = UTF16_LE;
                } else if (chset.equals("windows-1252")) {
                    is = new FileInputStream(filename);
                    byte[] bcode = new byte[2048];
                    int nNewLength = is.read(bcode);
                    code = guessEncoding(bcode, nNewLength);
                    Log.d(TAG,"windows-1252" + code);
                } else if (chset.equals("UTF-16BE")) {
                    code = UTF16_BE;
                } else {
                    code = ANSI;
                }
                Log.d(TAG,"chset" + chset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bInputStream != null){
                try {
                    bInputStream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            if (is != null){
                try {
                    is.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        }
        return code;
    }

    private static int guessEncoding(byte[] bcode, int nNewLength) {
        int code ;

        int iUnicode = 0;
        int iGbkSymbol = 0;
        int iUtf8Symbol = 0;
        int iUniCodeBig = 0;

        for (int i = 0; i < nNewLength - 2; i++) {
            int ch1 = (int) bcode[i];
            int ch2 = (int) bcode[i + 1];
            int ch3 = (int) bcode[i + 2];
            if (ch1 < 0)
                ch1 += 256;
            if (ch2 < 0)
                ch2 += 256;
            if (ch3 < 0)
                ch3 += 256;
            if (ch1 > 0 && ch1 < 0x7F) {
                if (ch2 == 0) {
                    iUnicode++;
                    i++;
                } else {
                    iGbkSymbol++;
                    iUtf8Symbol++;
                }
            } else if (ch1 == 0 && ch2 < 0x7F) {
                iUniCodeBig++;
                i++;
            } else if ((ch1 == 0xA1 && ch2 == 0xA3) || (ch1 == 0xA3 && ch2 == 0xAC) || (ch1 == 0xA3 && ch2 == 0xBF)
                    || (ch1 == 0xA3 && ch2 == 0xA1)) {
                i++;
                iGbkSymbol++;
            } else if ((ch1 == 0xE3 && ch2 == 0x80 && ch3 == 0x82) || (ch1 == 0xEF && ch2 == 0xBC && ch3 == 0x8C)
                    || (ch1 == 0xEF && ch2 == 0xBC && ch3 == 0x9F) || (ch1 == 0xEF && ch2 == 0xBC && ch3 == 0x81)) {
                iUtf8Symbol++;
                i += 2;
            } else if ((ch1 == 0xff && ch2 == 0x0c) || (ch1 == 0x30 && ch2 == 0x02) || (ch1 == 0xff && ch2 == 0x1f)
                    || (ch1 == 0xff && ch2 == 0x01)) {
                i++;
                iUniCodeBig++;
            }
        }
        if (iGbkSymbol >= iUtf8Symbol && iGbkSymbol > iUniCodeBig && iGbkSymbol > iUnicode || iUtf8Symbol > iGbkSymbol
                && iUtf8Symbol > iUniCodeBig && iUtf8Symbol > iUnicode || iGbkSymbol == 0 && iUtf8Symbol == 0 && iUnicode == 0
                && iUniCodeBig == 0) {
            // int regCount=(MIN_CHINESE_COUNT +
            // 1>nNewLength)?nNewLength:MIN_CHINESE_COUNT;
            // byte[] szChinese = new byte[regCount];
            // int[] temp = new int[bcode.length];
            // int n = 0;
            // for (int i = 0; i < temp.length; i++) {
            // if (bcode[i] < 0) {
            // temp[i] = bcode[i] + 256;
            // } else {
            // temp[i] = bcode[i];
            // }
            // }
            // while (temp[n] <= 0x80) {
            // n++;
            // if (n >= temp.length)
            // break;
            // }
            // for (int ii = 0; ii < regCount; ii++) {
            // if (n >= temp.length)
            // break;
            // szChinese[ii] = (byte) temp[n];
            // n++;
            // }
            // if (isUTF8(szChinese))// utf8 with no bom.
            // {
            // // m_bNoBom = true;
            // // m_bHasJugdeCode = TRUE;
            // code = UTF8;
            // } else {
            // code = 1;
            // }
            if (iUnicode == 0) {
                code = ANSI;
            } else {
                code = UTF16_LE;
            }
        } else if (iUniCodeBig > iGbkSymbol && iUniCodeBig > iUtf8Symbol && iUniCodeBig > iUnicode) {
            // m_bHasJugdeCode = TRUE;
            code = UTF16_BE;
        } else {
            // m_bHasJugdeCode = TRUE;
            code = ANSI;// GEnCodeTextLittle;
        }
        return code;
    }
}
