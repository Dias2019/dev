package org.example.model;

import java.io.Serializable;
import java.util.Arrays;

public class Response implements Serializable {

    private int status;
    private long fileId;
    private byte[] content;


    public Response(int status, long id, byte[] content) {
        this.status = status;
        this.fileId = id;
        this.content = content;
    }
    public Response(int status, long id) {
        this(status, id, null);
    }
    public Response(int status, byte[] content) {
        this(status, -1, content);
    }
    public Response(int status) {
        this(status, -1, null);
    }


    public int getStatus() {
        return status;
    }
    public long getFileId() {
        return fileId;
    }
    public byte[] getContent() {
        return content;
    }


    @Override
    public String toString() {
        return "Response{status=" + status + ", fileId=" + fileId + ", content='" + Arrays.toString(content) + "'}";
    }
}
