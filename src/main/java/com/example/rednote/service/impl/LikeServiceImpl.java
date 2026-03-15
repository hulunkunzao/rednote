package com.example.rednote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rednote.common.utils.ThreadLocalUtils;
import com.example.rednote.mapper.LikeMapper;
import com.example.rednote.mapper.PostDetailsMapper;
import com.example.rednote.model.po.LikePO;
import com.example.rednote.model.po.PostDetailsPO;
import com.example.rednote.service.LikeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class LikeServiceImpl extends ServiceImpl<LikeMapper, LikePO> implements LikeService {

    private final LikeMapper likeMapper;
    private final PostDetailsMapper postDetailsMapper;

    @Override
    @Transactional
    public String isLiked(Long postId) {
        String userIdstr = ThreadLocalUtils.get("userId");
        Long userId = Long.parseLong(userIdstr);
        LikePO likePO = new LikePO();
        likePO.setUserId(userId);
        likePO.setPostId(postId);
        LambdaQueryWrapper<LikePO> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(LikePO::getPostId,postId)
                .eq(LikePO::getUserId,userId);
        boolean exists = likeMapper.exists(lambdaQuery);
        if(!exists){   //未点赞，执行点赞操作
            int insert_cnt = likeMapper.insert(likePO);
            UpdateWrapper<PostDetailsPO> wrapper = new UpdateWrapper<PostDetailsPO>();
            wrapper.setSql("like_count = like_count+1")
                    .eq("post_id",postId);
            postDetailsMapper.update(wrapper);
            return "已点赞！";
        }
        // 已点赞，执行取消点赞操作
        int delete_cnt = likeMapper.delete(lambdaQuery);
        UpdateWrapper<PostDetailsPO> wrapper = new UpdateWrapper<PostDetailsPO>();
        wrapper.setSql("like_count = like_count-1")
                .eq("post_id",postId);
        postDetailsMapper.update(wrapper);
        return "已取消点赞";
    }
}
