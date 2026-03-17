package com.example.rednote.controller;

import com.example.rednote.common.response.Result;
import com.example.rednote.model.dto.CommentDTO;
import com.example.rednote.model.vo.CommentResultVO;
import com.example.rednote.model.vo.CommentVO;
import com.example.rednote.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
@Tag(name = "评论接口")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "根据帖子ID查询评论列表")
    @GetMapping("/list/{postId}")
    public Result<List<CommentResultVO>> listByPostId(@PathVariable Integer postId) {
        return Result.success(commentService.listByPostId(postId));
    }

    @Operation(summary = "添加评论")
    @PostMapping("/insert")
    public Result addComment(@RequestBody CommentDTO commentDTO){
        commentService.insert(commentDTO);
        return Result.success();
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{commentId}")
    public Result delete(@PathVariable Integer commentId) {
        commentService.removeById(commentId);
        return Result.success();
    }
}
