package com.yufeng.ireader.reader.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by yufeng on 2018/4/16-0016.
 *
 */

public class ReadRandomAccessFile extends RandomAccessFile{

    private static final int BOM_SIZE = 4;
    private static final int BOM_SIZE_UTF8 = 3;
    private boolean isUTF = false;

    private String realPath;
    private long curPosition;

    /**字符编码*/
    private int code;

    public ReadRandomAccessFile(String name, String mode) throws IOException{
        super(name, mode);
        realPath = name;
        init(name);
    }

    private void init(String name) throws IOException{
        InputStream bin = null;
        try {
            bin = new FileInputStream(name);
            byte[] bom = new byte[BOM_SIZE];
            bin.read(bom);
            if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) &&
                    (bom[2] == (byte) 0xBF)) {
                isUTF = true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bin != null){
                try {
                    bin.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        curPosition = getAbsoluteFilePointer();

    }

    private long getAbsoluteFilePointer() throws IOException {
        if (isUTF) {
            return super.getFilePointer() + BOM_SIZE_UTF8;
        } else {
            return super.getFilePointer();
        }
    }

    /**
     * 得到当前指针的位置
     * @return
     * @throws IOException
     */
    public long getLocation() throws IOException{
        return getAbsoluteFilePointer();
    }

    /**
     * 记录一下当前的pointer位置
     * @param position 读完之后pointer的位置
     */
    public void setCurPosition(long position){
        this.curPosition = position;
    }

    public long getCurPosition(){
        return this.curPosition;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
