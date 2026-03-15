package com.example.rednote.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentPO> implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    @Override
    public List<CommentVO> listByPostId(Integer postId) {
        List<CommentPO> commentPOS = commentMapper.selectList(
                Wrappers.<CommentPO>lambdaQuery().eq(CommentPO::getPostId, postId));
        if (commentPOS.isEmpty()) {
            return List.of();
        }
        List<CommentVO> commentVOS = BeanUtil.copyToList(commentPOS, CommentVO.class);
        return commentVOS;
    }

    /**
     * 添加评论
     * @param commentDTO
     */
    @Override
    public void insert(CommentDTO commentDTO) {
        // @TODO 1. 从ThreadLocal中获取当前登录用户的ID
        Integer userId = 1;
        // 2. 校验帖子是否存在
        if (postMapper.selectById(commentDTO.getPostId()) == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
        CommentPO commentPO = BeanUtil.copyProperties(commentDTO, CommentPO.class);
        commentPO.setUserId(userId);
        commentMapper.insert(commentPO);
    }
}
