package org.example.model;

import java.io.Serializable;
import java.util.Arrays;

public class Request implements Serializable {

    private int method;
    private String filename;
    private long fileId;
    private byte[] content;


    public Request(int method, String filename, long fileId, byte[] content) {
        this.method = method;
        this.filename = filename;
        this.fileId = fileId;
        this.content = content;
    }
    public Request(int method, String filename, byte[] content) {
        this(method, filename, -1, content);
    }
    public Request(int method, String filename, long fileId) {
        this(method, filename, fileId, null);
    }
    public Request(int method) {
        this(method, null, -1, null);
    }


    public int getMethod() {
        return method;
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String name) {
        filename = name;
    }

    public long getFileId() {
        return fileId;
    }

    public byte[] getContent() {
        return content;
    }


    @Override
    public String toString() {
        return "Request{status=" + method + ", filename='" + filename + ", fileId='" + fileId + "', content='" + Arrays.toString(content) + "'}";
    }
}
