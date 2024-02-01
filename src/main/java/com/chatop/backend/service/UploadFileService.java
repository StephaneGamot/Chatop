package com.chatop.backend.service;

import java.io.IOException;
public interface UploadFileService {
    String uploadFile(String multipartFile) throws IOException;
}
