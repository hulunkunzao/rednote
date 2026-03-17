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
    public Boolean isLiked(Integer postId) {
        String userIdStr = ThreadLocalUtils.get("userId");
        Integer userId = Integer.parseInt(userIdStr);
        LikePO likePO = new LikePO();
        likePO.setUserId(userId);
        likePO.setPostId(postId);
        LambdaQueryWrapper<LikePO> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(LikePO::getPostId,postId)
                .eq(LikePO::getUserId,userId);
        boolean exists = likeMapper.exists(lambdaQuery);
        if(!exists){
            int insert_cnt = likeMapper.insert(likePO);
            UpdateWrapper<PostDetailsPO> wrapper = new UpdateWrapper<PostDetailsPO>();
            wrapper.setSql("like_count = like_count+1")
                    .eq("post_id",postId);
            postDetailsMapper.update(wrapper);
            return true;
        }
        int delete_cnt = likeMapper.delete(lambdaQuery);
        UpdateWrapper<PostDetailsPO> wrapper = new UpdateWrapper<PostDetailsPO>();
        wrapper.setSql("like_count = like_count-1")
                .eq("post_id",postId);
        postDetailsMapper.update(wrapper);
        return false;
    }
}
