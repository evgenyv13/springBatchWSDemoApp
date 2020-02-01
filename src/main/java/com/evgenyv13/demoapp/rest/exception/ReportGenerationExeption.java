package com.evgenyv13.demoapp.rest.exception;

public class ReportGenerationExeption extends RuntimeException {

    private String msg;

    public ReportGenerationExeption(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
