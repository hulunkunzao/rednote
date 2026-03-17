package com.example.rednote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rednote.model.po.PostDetailsPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostDetailsMapper extends BaseMapper<PostDetailsPO> {
}
