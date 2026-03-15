package com.example.rednote.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rednote.model.po.LikePO;

public interface LikeService extends IService<LikePO> {
    String isLiked(Long postId);
}
