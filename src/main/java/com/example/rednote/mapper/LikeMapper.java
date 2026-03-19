package com.example.rednote.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rednote.model.po.LikePO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper extends BaseMapper<LikePO> {
}

