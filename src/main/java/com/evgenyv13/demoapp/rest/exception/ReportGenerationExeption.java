package com.evgenyv13.demoapp.rest.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReportGenerationExeption extends RuntimeException {

    @Getter
    private final String msg;
}
