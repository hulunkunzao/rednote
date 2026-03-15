package com.example.rednote.controller;

import com.example.rednote.common.response.Result;
import com.example.rednote.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("files")
@Tag(name = "文件接口", description = "文件相关接口")
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "帖子图片视频上传")
    @PostMapping("/update")
    public Result fileUpdate(@RequestParam("file") MultipartFile file) throws Exception {
        String fileUrl = fileService.updateFile(file);
        return Result.success(fileUrl);
    }
}
