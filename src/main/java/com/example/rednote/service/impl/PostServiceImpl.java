package com.example.rednote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rednote.common.utils.MinioUtils;
import com.example.rednote.common.utils.ThreadLocalUtils;
import com.example.rednote.mapper.*;
import com.example.rednote.model.dto.PostDTO;
import com.example.rednote.model.po.*;
import com.example.rednote.model.vo.PostResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rednote.service.PostService;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class PostServiceImpl extends ServiceImpl<PostMapper, PostPO> implements PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final PostTopicMapper postTopicMapper;
    private final MinioUtils minioUtils;
    private final PostDetailsMapper postDetailsMapper;
    private final PostImageMapper postImageMapper;


    @Override
    @Transactional
    public List<PostResult> listWithUserInfo(int topicId) {
        LambdaQueryWrapper<PostPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PostPO::getCreateTime);
        if(topicId != 0){
            List<PostTopicPO> postTopicPOS = postTopicMapper
                    .selectList(new LambdaQueryWrapper<PostTopicPO>().eq(PostTopicPO::getTopicId, topicId));
            List<Integer> postIds = postTopicPOS.stream().map(PostTopicPO::getPostId).toList();
            wrapper.in(PostPO::getPostId, postIds);
        }
        List<PostPO> postPOS = postMapper.selectList(wrapper);
        List<PostResult> postResults = postPOS.stream().map(postPO -> {
            UserPO userPO = userMapper.selectById(postPO.getUserId());
            PostResult postResult = new PostResult();
            BeanUtils.copyProperties(postPO, postResult);
            postResult.setUserName(userPO.getUsername());
            postResult.setUserAvatar(userPO.getAvatar());
            return postResult;
        }).toList();
        return postResults;
    }

    @Override
    public List<PostResult> listWithUserInfoByBloggerId(Integer bloggerId) {
        LambdaQueryWrapper<PostPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PostPO::getCreateTime);
        wrapper.eq(PostPO::getUserId, bloggerId);
        List<PostPO> postPOS = postMapper.selectList(wrapper);
        List<PostResult> postResults = postPOS.stream().map(postPO -> {
            UserPO userPO = userMapper.selectById(postPO.getUserId());
            PostResult postResult = new PostResult();
            //postPO.setCoverImage(minioUtils.getPublicUrl(postPO.getCoverImage()));
            BeanUtils.copyProperties(postPO, postResult);
            postResult.setUserName(userPO.getUsername());
            postResult.setUserAvatar(userPO.getAvatar());
            return postResult;
        }).toList();
        return postResults;
    }
    @Override
    public void addPost(PostDTO postDTO){
        PostPO postPO = new PostPO();
        postDTO.setUserId(Integer.parseInt(ThreadLocalUtils.get("userId")));
        BeanUtils.copyProperties(postDTO, postPO);
        postMapper.insert(postPO);
        Integer postId = postPO.getPostId();
        PostDetailsPO postDetailsPO = new PostDetailsPO();
        postDetailsPO.setPostId(postId);
        postDetailsPO.setTopicIds(postDTO.getTopicIds().toString());
        postDetailsMapper.insert(postDetailsPO);

        for(String fileUrl : postDTO.getFileUrls()){
            PostImagePO postImagePO = new PostImagePO();
            postImagePO.setPostId(postId);
            postImagePO.setUrl(fileUrl);
            postImageMapper.insert(postImagePO);
        }
        for (String topicId : postDTO.getTopicIds()){
            PostTopicPO postTopicPO = new PostTopicPO();
            postTopicPO.setPostId(postId);
            postTopicPO.setTopicId(Integer.parseInt(topicId));
            postTopicMapper.insert(postTopicPO);
        }
    }
}
