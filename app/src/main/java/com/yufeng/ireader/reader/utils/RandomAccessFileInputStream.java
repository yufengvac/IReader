package com.yufeng.ireader.reader.utils;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

public class RandomAccessFileInputStream extends InputStream implements DataInput {
    private ReadRandomAccessFile file;

    public RandomAccessFileInputStream(ReadRandomAccessFile file) {
        this.file = file;
    }

    public RandomAccessFileInputStream(String filePath) throws IOException {
        this.file = new ReadRandomAccessFile(filePath, "r");
    }

    @Override
    public int read() throws IOException {
        return file.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return file.read(b, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return file.read(b);
    }

    /**
     * @return
     * @throws IOException
     */
    public long length() throws IOException {
        return file.length();
    }

    /**
     * @param pos
     * @throws IOException
     */
    public void seek(long pos) throws IOException {
        file.seek(pos);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return file.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return file.readByte();
    }

    @Override
    public char readChar() throws IOException {
        return file.readChar();
    }

    @Override
    public double readDouble() throws IOException {
        return file.readDouble();
    }

    @Override
    public float readFloat() throws IOException {
        return file.readFloat();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        file.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        file.readFully(b, off, len);
    }

    @Override
    public int readInt() throws IOException {
        return file.readInt();
    }

    @Override
    public String readLine() throws IOException {
        return file.readLine();
    }

    @Override
    public long readLong() throws IOException {
        return file.readLong();
    }

    @Override
    public short readShort() throws IOException {
        return file.readShort();
    }

    @Override
    public String readUTF() throws IOException {
        return file.readUTF();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return file.readUnsignedByte();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return file.readUnsignedShort();
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return file.skipBytes(n);
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (file != null) {
            file.close();
        }
    }
}
