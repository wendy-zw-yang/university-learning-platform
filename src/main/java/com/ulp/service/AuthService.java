package com.ulp.service;

import com.ulp.bean.UserModel;
import com.ulp.util.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {
    // Simulate database or storage (in-memory for now)
    // In real scenario, use DAO to interact with DB

    public boolean authenticate(String username, String password) {
        // Mock logic: Check against stored users
        // Example: if username == "test" && password == "pass", return true
        return "test".equals(username) && "pass".equals(password);
    }

    public void registerUser(UserModel user) {
        if (!user.validate()) {
            throw new IllegalArgumentException("Invalid user data");
        }
        // Mock: Save to DB (print for simulation)

        if(findUser(user.getUsername())!=null )
            throw new IllegalArgumentException("User is exist. Can not create duplicate users!");

        String sql="insert into users(username,password,role,email,avatar) values(?,?,?,?,?)";

        try(Connection conn=DBHelper.getConnection();
            PreparedStatement prestat=conn.prepareStatement(sql)){
            prestat.setString(1, user.getUsername());
            prestat.setString(2, user.getPassword());
            prestat.setString(3, user.getRole());
            prestat.setString(4, user.getEmail());
            prestat.setString(5, user.getAvatar());
            prestat.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error registering user", e);
        }
        System.out.println("Registered user: " + user.getUsername());
    }

    public UserModel findUser(String username) {

        UserModel user=null;

        String sql="select * from users where username=?";

        try(Connection conn=DBHelper.getConnection();
            PreparedStatement prestat=conn.prepareStatement(sql)){
            prestat.setString(1, username);
            ResultSet rs=prestat.executeQuery();
            if(rs.next()){
                user=new UserModel();
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
                user.setAvatar(rs.getString("avatar"));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error registering user", e);
        }
        return user;
    }
}