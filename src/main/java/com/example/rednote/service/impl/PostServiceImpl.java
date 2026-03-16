package com.example.rednote.service.impl;

import com.example.rednote.mapper.UserMapper;
import com.example.rednote.model.po.UserPO;
import com.example.rednote.model.vo.PostResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rednote.mapper.PostMapper;
import com.example.rednote.model.po.PostPO;
import com.example.rednote.service.PostService;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class PostServiceImpl extends ServiceImpl<PostMapper, PostPO> implements PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public List<PostResult> listWithUserInfo() {
        List<PostPO> postPOS = list();
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
}
