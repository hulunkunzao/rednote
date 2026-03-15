package com.example.rednote.controller;

import com.example.rednote.common.response.Result;
import com.example.rednote.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
@AllArgsConstructor
@Tag(name = "点赞管理",description = "点赞管理接口")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "点赞/取消点赞",description = "根据postId点赞/取消点赞")
    @PostMapping("/isliked/{postId}")
    public Result<String> isLiked(@PathVariable Long postId){
        return Result.success(likeService.isLiked(postId));
    }
}
