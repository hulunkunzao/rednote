package com.example.rednote.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@AllArgsConstructor
@Tag(name = "点赞管理",description = "点赞管理接口")
public class LikeController {

}
