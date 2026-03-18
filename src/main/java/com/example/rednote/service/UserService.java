package com.example.rednote.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rednote.model.dto.LoginDTO;
import com.example.rednote.model.dto.PasswordUpdateDTO;
import com.example.rednote.model.dto.UserDTO;
import com.example.rednote.model.dto.UserUpdateDTO;
import com.example.rednote.model.vo.PostVO;
import com.example.rednote.model.vo.UserDetailsVO;
import com.example.rednote.model.vo.UserVO;

import java.util.List;

public interface UserService {

    UserVO getById(Integer userId);

    Integer register(UserDTO userDTO);

    String login(LoginDTO loginDTO);

    UserDetailsVO getCurrentUserDetails();


    Boolean updateUserInfo(UserUpdateDTO updateDTO);


    Boolean updatePassword(PasswordUpdateDTO passwordDTO);

    Boolean followUser(Integer followUserId);

    Page<PostVO> listUserPosts(Integer userId, Integer page, Integer size);

    Boolean isFollow(Integer followUserId);

    List<UserVO> listBloggers();

    UserVO getCurrentUser();
}
