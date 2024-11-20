package com.example.userinfo.service;

import com.example.userinfo.entity.UserInfo;
import com.example.userinfo.repository.UserInfoRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserInfoService {

    private UserInfoRepository userInfoRepository;

    public void saveUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    public Optional<UserInfo> getUserInfo(UUID id) {
        return userInfoRepository.findById(id);
    }

    public UserInfo getUserInfo(String id) {
        return userInfoRepository.findById(UUID.fromString(id)).get();
    }

    public UserInfo getUserInfoByEmail(String email) {
        return userInfoRepository.findByEmail(email);
    }

    public UserInfo getUserInfoByUsername(String username) {
        return userInfoRepository.findByUsername(username);
    }

    @Autowired
    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }
}
