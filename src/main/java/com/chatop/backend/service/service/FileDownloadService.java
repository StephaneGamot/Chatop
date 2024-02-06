package com.chatop.backend.service.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileDownloadService {
    String uploadFile(MultipartFile multipartFile) throws IOException;
}
