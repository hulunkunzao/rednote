package com.example.rednote.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rednote.common.utils.ThreadLocalUtils;
import com.example.rednote.mapper.FollowMapper;
import com.example.rednote.mapper.PostMapper;
import com.example.rednote.mapper.UserDetailsMapper;
import com.example.rednote.model.dto.PasswordUpdateDTO;
import com.example.rednote.model.dto.UserUpdateDTO;
import com.example.rednote.model.po.FollowPO;
import com.example.rednote.model.po.PostPO;
import com.example.rednote.model.po.UserDetailsPO;
import com.example.rednote.model.vo.PostVO;
import com.example.rednote.model.vo.UserDetailsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.rednote.common.utils.JwtUtils;
import com.example.rednote.mapper.UserMapper;
import com.example.rednote.model.dto.LoginDTO;
import com.example.rednote.model.dto.UserDTO;
import com.example.rednote.model.exception.LoginFailedException;
import com.example.rednote.model.po.UserPO;
import com.example.rednote.model.vo.UserVO;
import com.example.rednote.service.UserService;

import cn.hutool.crypto.digest.BCrypt;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserDetailsMapper userDetailsMapper;
    private final FollowMapper followMapper;
    private final PostMapper postMapper;
    private final JwtUtils jwtUtils;

    @Override
    public UserVO getById(Integer userId) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userMapper.selectById(userId), userVO);
        return userVO;
    }

    @Override
    public Integer register(UserDTO userDTO) {
        userDTO.setUserId(null);
        userDTO.setPassword(BCrypt.hashpw(userDTO.getPassword()));
        UserPO userPO = new UserPO();
        BeanUtils.copyProperties(userDTO, userPO);
        return userMapper.insert(userPO);
    }

    @Override
    public String login(LoginDTO loginDTO) {
        QueryWrapper<UserPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", loginDTO.getUsername());
        UserPO userPO = userMapper.selectOne(queryWrapper);
        log.info(userPO.toString());
        log.info(loginDTO.toString());
        if (BCrypt.checkpw(loginDTO.getPassword(), userPO.getPassword())) {
            return jwtUtils.generateToken(Integer.toString(userPO.getUserId()), userPO.getUsername());
        } else {
            throw new LoginFailedException("用户名或密码错误");
        }
    }
    @Override
    public UserDetailsVO getCurrentUserDetails(){
        String userIdStr = ThreadLocalUtils.get("userId");
        Integer userId = Integer.parseInt(userIdStr);

        UserPO userPO = userMapper.selectById(userId);
        UserDetailsPO userDetails = userDetailsMapper.selectById(userId);

        UserDetailsVO vo = new UserDetailsVO();
        BeanUtil.copyProperties(userPO, vo);
        BeanUtil.copyProperties(userDetails, vo);
        return vo;
    }
    @Override
    public Boolean updateUserInfo(UserUpdateDTO updateDTO){
        String userIdStr = ThreadLocalUtils.get("userId");
        Integer userId = Integer.parseInt(userIdStr);

        UserDetailsPO details = new UserDetailsPO();
        details.setUserId(userId);
        details.setNickname(updateDTO.getNickname());
        details.setAvatar(updateDTO.getAvatar());
        details.setGender(updateDTO.getGender());
        details.setBio(updateDTO.getBio());
        details.setPhone(updateDTO.getPhone());
        details.setEmail(updateDTO.getEmail());
        details.setUpdatedTime(LocalDateTime.now());

        UserDetailsPO exist = userDetailsMapper.selectById(userId);
        if (exist == null) {
            details.setCreatedTime(LocalDateTime.now());
            userDetailsMapper.insert(details);
        } else {
            userDetailsMapper.updateById(details);
        }
        if (updateDTO.getAvatar() != null) {
            UserPO userPO = new UserPO();
            userPO.setUserId(userId);
            userPO.setAvatar(updateDTO.getAvatar());
            userMapper.updateById(userPO);
        }
        return true;
    }
    @Override
    public Boolean updatePassword(PasswordUpdateDTO passwordDTO){
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("新密码与确认密码不一致");
        }

        String userIdStr = ThreadLocalUtils.get("userId");
        Integer userId = Integer.parseInt(userIdStr);
        UserPO userPO = userMapper.selectById(userId);

        if (!BCrypt.checkpw(passwordDTO.getOldPassword(), userPO.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }

        UserPO updateUser = new UserPO();
        updateUser.setUserId(userId);
        updateUser.setPassword(BCrypt.hashpw(passwordDTO.getNewPassword()));
        userMapper.updateById(updateUser);
        return true;
    }

    @Override
    public Boolean followUser(Integer followUserId){
        String userIdStr = ThreadLocalUtils.get("userId");
        Integer currentUserId = Integer.parseInt(userIdStr);

        if (Objects.equals(currentUserId, followUserId)) {
            throw new IllegalArgumentException("不能关注自己");
        }

        // 检查是否已关注
        LambdaQueryWrapper<FollowPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowPO::getFollowerId, currentUserId)
                .eq(FollowPO::getFollowingId, followUserId);
        FollowPO exist = followMapper.selectOne(wrapper);

        if (exist != null) {
            followMapper.delete(wrapper);
            return false;
        } else {
            FollowPO follow = new FollowPO();
            follow.setFollowerId(currentUserId);
            follow.setFollowingId(followUserId);
            follow.setCreateTime(LocalDateTime.now());
            followMapper.insert(follow);
            return true;
        }
    }
    @Override
    public Page<PostVO> listUserPosts(Integer userId, Integer page, Integer size) {

        Page<PostPO> postPage = postMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<PostPO>().eq(PostPO::getUserId, userId)
        );

        Page<PostVO> voPage = new Page<>(page, size);
        BeanUtil.copyProperties(postPage, voPage);
        voPage.setRecords(BeanUtil.copyToList(postPage.getRecords(), PostVO.class));
        return voPage;
    }
}
