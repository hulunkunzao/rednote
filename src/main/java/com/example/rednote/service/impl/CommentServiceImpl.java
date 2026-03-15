package com.example.rednote.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rednote.common.utils.ThreadLocalUtils;
import com.example.rednote.mapper.CommentMapper;
import com.example.rednote.mapper.PostMapper;
import com.example.rednote.model.dto.CommentDTO;
import com.example.rednote.model.po.CommentPO;
import com.example.rednote.model.vo.CommentVO;
import com.example.rednote.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class CommentServiceImpl  implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
}
