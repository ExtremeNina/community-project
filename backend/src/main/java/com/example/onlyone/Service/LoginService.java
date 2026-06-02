package com.example.onlyone.Service;

import com.example.onlyone.DTO.QQLoginDTO;
import com.example.onlyone.DTO.RegisterDTO;
import com.example.onlyone.DTO.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

public interface LoginService {

    void insertUser(LoginDTO userdto);

    String login(LoginDTO userDTO);

    Map<String, String> login(LoginDTO userDTO, HttpServletResponse response);

    String setCode(RegisterDTO registerDTO);

    void registerUser(RegisterDTO registerDTO);

    void loginOut(HttpServletRequest request);

    void loginOut(HttpServletRequest request, HttpServletResponse response);

    Boolean sendQQEmail(String email);

    String qqLogin(QQLoginDTO qqLoginDTO);

    Map<String, String> qqLogin(QQLoginDTO qqLoginDTO, HttpServletResponse response);
}
