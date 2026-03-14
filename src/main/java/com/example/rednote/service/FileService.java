package com.example.rednote.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String updateFile(MultipartFile file) throws Exception;
}
