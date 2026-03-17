package com.example.rednote.model.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("post_details")
public class PostDetailsPO {
    @TableId(type = IdType.AUTO)
    private Integer postDetailsId;
    private Integer postId;
    private Integer type;
    private Integer topicIds;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
}
