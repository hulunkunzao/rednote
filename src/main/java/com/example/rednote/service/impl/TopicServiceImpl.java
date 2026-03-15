package com.example.rednote.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rednote.mapper.TopicMapper;
import com.example.rednote.model.po.TopicPO;
import com.example.rednote.service.TopicService;
import org.springframework.stereotype.Service;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, TopicPO> implements TopicService {
}
