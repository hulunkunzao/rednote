package com.example.rednote.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rednote.common.utils.ThreadLocalUtils;
import com.example.rednote.config.interceptor.JwtInterceptor;
import com.example.rednote.mapper.CommentMapper;
import com.example.rednote.mapper.PostMapper;
import com.example.rednote.mapper.UserMapper;
import com.example.rednote.model.dto.CommentDTO;
import com.example.rednote.model.po.CommentPO;
import com.example.rednote.model.po.UserPO;
import com.example.rednote.model.vo.CommentResultVO;
import com.example.rednote.model.vo.CommentVO;
import com.example.rednote.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentPO> implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final UserMapper userMapper;

    @Override
    public List<CommentResultVO> listByPostId(Integer postId) {
        List<CommentPO> commentPOS = commentMapper.selectList(
                Wrappers.<CommentPO>lambdaQuery().eq(CommentPO::getPostId, postId));
        if (commentPOS.isEmpty()) {
            return List.of();
        }
        List<CommentResultVO> commentResultVOS = BeanUtil.copyToList(commentPOS, CommentResultVO.class);
        commentResultVOS.forEach(commentResultVO -> {
            UserPO userPO = userMapper.selectById(commentResultVO.getUserId());
            commentResultVO.setUserName(userPO.getUsername());
            commentResultVO.setUserAvatar(userPO.getAvatar());
        });
        return commentResultVOS;
    }


    @Override
    public void insert(CommentDTO commentDTO) {
        // 1. 从ThreadLocal中获取当前登录用户的ID
        String userIdStr = ThreadLocalUtils.get("userId");
        Integer userId = Integer.parseInt(userIdStr);
        log.info("userId: {}", userId);
        log.info("commentDTO: {}", commentDTO);
        // 2. 校验帖子是否存在
        if (postMapper.selectById(commentDTO.getPostId()) == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
        CommentPO commentPO = BeanUtil.copyProperties(commentDTO, CommentPO.class);
        commentPO.setUserId(userId);
        commentMapper.insert(commentPO);
    }
}
