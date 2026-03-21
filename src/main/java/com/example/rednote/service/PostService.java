package com.example.rednote.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rednote.model.dto.PostDTO;
import com.example.rednote.model.po.PostPO;
import com.example.rednote.model.vo.PostResult;

import java.util.List;

public interface PostService extends IService<PostPO> {
    List<PostResult> listWithUserInfo(int topicId);

    List<PostResult> listWithUserInfoByBloggerId(Integer bloggerId);

    void addPost(PostDTO postDTO);

    void deleteById(Integer postId);
}
