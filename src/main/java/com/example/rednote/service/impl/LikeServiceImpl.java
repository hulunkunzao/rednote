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

}
