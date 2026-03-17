package com.example.rednote.service;


import com.example.rednote.model.vo.PostImagesVO;

import java.util.List;

public interface PostImageService {
    List<String> list(Integer postId);
}
