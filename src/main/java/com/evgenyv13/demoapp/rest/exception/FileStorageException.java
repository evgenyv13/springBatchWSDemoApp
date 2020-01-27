package com.evgenyv13.demoapp.rest.exception;

public class FileStorageException extends RuntimeException {

    private String msg;

    public FileStorageException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
