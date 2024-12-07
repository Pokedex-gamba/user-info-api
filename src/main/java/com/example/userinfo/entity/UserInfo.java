package com.example.userinfo.entity;

import com.example.userinfo.dto.UserInfoDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    private String id;

    private String username;

    private String name;

    private String surname;


    public UserInfo(String id, String username, String surname, String name, String email) {
        this.id = id;
        this.username = username;
        this.surname = surname;
        this.name = name;
    }

    public UserInfo(UserInfoDTO userInfoDTO, String id) {
        this.id = id;
        this.username = userInfoDTO.getUsername();
        this.surname = userInfoDTO.getSurname();
        this.name = userInfoDTO.getName();
    }

    public UserInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
