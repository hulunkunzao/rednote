package com.example.rednote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rednote.model.po.PostTopicPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostTopicMapper extends BaseMapper<PostTopicPO> {
}
