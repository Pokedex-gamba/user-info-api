package com.example.userinfo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    private UUID id;

    private String username;

    private String name;

    private String surname;

    private String email;

    public UserInfo(UUID id, String username, String surname, String name, String email) {
        this.id = id;
        this.username = username;
        this.surname = surname;
        this.name = name;
        this.email = email;
    }

    public UserInfo() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
