package com.example.rednote.controller;

import com.example.rednote.model.dto.PostDTO;
import com.example.rednote.model.vo.PostResult;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rednote.common.response.Result;
import com.example.rednote.model.po.PostPO;
import com.example.rednote.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/posts")
@Tag(name = "帖子接口", description = "帖子相关接口")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    @Operation(summary = "获取全部帖子", description = "返回所有贴子，支持分页")
    public Result<Page<PostPO>> list(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "-1") int size) {
        return Result.success(postService.page(new Page<>(page, size)));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "获取帖子详情", description = "根据帖子ID返回详细信息")
    public Result<PostPO> getPost(@PathVariable Integer postId) {
        return Result.success(postService.getById(postId));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "删除帖子", description = "根据帖子ID删除指定帖子")
    public Result<?> delete(@PathVariable Integer postId) {
        postService.delete(postId);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "获取全部帖子", description = "适用于主页的返回所有贴子,带用户头像、用户名")
    public Result<List<PostResult>> listWithUserInfo(
            @RequestParam(defaultValue = "0") int topicId) {
        List<PostResult> postResults = postService.listWithUserInfo(topicId);
        return Result.success(postResults);
    }

    @GetMapping("/list/{bloggerId}")
    @Operation(summary = "获取博主帖子", description = "适用于主页的返回所有贴子,带用户头像、用户名")
    public Result<List<PostResult>> listWithUserInfoByBloggerId(
            @PathVariable Integer bloggerId) {
        List<PostResult> postResults = postService.listWithUserInfoByBloggerId(bloggerId);
        return Result.success(postResults);
    }

    @Operation(summary = "添加帖子")
    @PostMapping("/insert")
    public Result addPost(@RequestBody PostDTO postDTO){
        postService.addPost(postDTO);
        return Result.success();
    }
}
