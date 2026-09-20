package com.example.pigs.model;
public class User {
    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Basic validation logic belonging to the Model
    public boolean isValid() {
        return email != null && email.contains("@") &&
                password != null && password.length() >= 6;
    }
}
