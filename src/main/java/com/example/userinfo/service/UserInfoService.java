package com.example.userinfo.service;

import com.example.userinfo.entity.UserInfo;
import com.example.userinfo.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;

    public void saveUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    public UserInfo getUserInfo(String id) {
        Optional<UserInfo> userInfo = userInfoRepository.findById(id);
        return userInfo.orElse(null);
    }

    public UserInfo getUserInfoByUsername(String username) {
        return userInfoRepository.findByUsername(username);
    }

    public UserInfo updateUserInfo(UserInfo userInfo) {
        if(userInfoRepository.findById(userInfo.getId()).isPresent()) {
            return userInfoRepository.save(userInfo);
        } else {
            return null;
        }
    }

    public List<UserInfo> getAllUserInfo() {
        return userInfoRepository.findAll();
    }

    @Autowired
    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }
}
