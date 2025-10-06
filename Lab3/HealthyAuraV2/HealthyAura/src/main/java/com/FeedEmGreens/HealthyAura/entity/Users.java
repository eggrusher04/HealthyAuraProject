package com.FeedEmGreens.HealthyAura.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String username;
    private String password;

    @Column(nullable = false)
    private String role;

    private Integer points = 0;

    @Column(length = 512)
    private String preferences;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Integer getPoints() {return points;}

    public String getPreferences() {return preferences;}

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPoints(Integer points) {this.points = points;}

    public void setPreferences(String preferences) {this.preferences = preferences;}
}
