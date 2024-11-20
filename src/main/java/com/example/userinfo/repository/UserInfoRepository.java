package com.example.userinfo.repository;

import com.example.userinfo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {

    UserInfo findByUsername(String username);

    UserInfo findByEmail(String email);
}
