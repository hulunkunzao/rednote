package com.example.rednote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rednote.model.po.CommentPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<CommentPO> {
}
