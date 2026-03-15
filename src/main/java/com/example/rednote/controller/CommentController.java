package com.example.rednote.controller;

import com.example.rednote.common.response.Result;
import com.example.rednote.model.dto.CommentDTO;
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
}
