package com.example.rednote.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post/images")
@Tag(name = "帖子图片接口", description = "帖子图片相关接口")
@AllArgsConstructor
public class PostImageController {
}