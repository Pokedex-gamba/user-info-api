package com.example.userinfo.repository;

import com.example.userinfo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserInfoRepository extends JpaRepository<UserInfo, String> {

    UserInfo findByUsername(String username);

    UserInfo findByEmail(String email);
}
