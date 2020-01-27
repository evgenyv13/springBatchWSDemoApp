package com.evgenyv13.demoapp.rest.controller;

import com.evgenyv13.demoapp.batch.model.ResponseDataDto;
import com.evgenyv13.demoapp.rest.exception.FileStorageException;
import com.evgenyv13.demoapp.rest.exception.ReportGenerationExeption;
import com.evgenyv13.demoapp.rest.service.BatchService;
import com.evgenyv13.demoapp.rest.service.FileService;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private BatchService batchService;



    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseDataDto submit(@RequestParam("file") MultipartFile file) throws Exception {
        String filePath = fileService.saveFile(file);
        ResponseDataDto responseDataDto = batchService.runStatsJob(filePath);
        fileService.deleteFile(filePath);
        return responseDataDto;
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity handleException(FileStorageException exception) {
        return ResponseEntity.status(400).body(exception.getMsg());
    }

    @ExceptionHandler(ReportGenerationExeption.class)
    public ResponseEntity handleException(ReportGenerationExeption exception) {
        return ResponseEntity.status(400).body(exception.getMsg());
    }
}
