package com.example.userinfo.service;

import com.example.userinfo.entity.UserInfo;
import com.example.userinfo.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;

    public void saveUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    public UserInfo getUserInfo(String id) {
        return userInfoRepository.findById(id).get();
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
