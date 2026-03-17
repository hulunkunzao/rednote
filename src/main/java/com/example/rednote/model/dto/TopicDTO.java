package com.example.rednote.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TopicDTO {

    private String topicName;
    private String topicDescription;
    private String topicIcon;
    private String cover;
    private Integer postCount;    //帖子数目
    private Integer followCount;  //关注数目
    private Boolean isHot;        //是否为热门话题
    private LocalDateTime createTime;
}
