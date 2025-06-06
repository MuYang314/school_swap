package com.example.school_swap;

public class User {
    private int id;
    private String nickname;
    private String email;
    private String avatar_url;
    private String credit_score;
    private String token;

    User() {}
    User(int id, String nickname, String email, String avatar_url,
         String credit_score, String token) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.avatar_url = avatar_url;
        this.credit_score = credit_score;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getCredit_score() {
        return credit_score;
    }

    public String getToken() {
        return token;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public void setCredit_score(String credit_score) {
        this.credit_score = credit_score;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
