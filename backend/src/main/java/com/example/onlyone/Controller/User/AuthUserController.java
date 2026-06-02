package com.example.onlyone.Controller.User;

import com.example.onlyone.DTO.QQLoginDTO;
import com.example.onlyone.DTO.RegisterDTO;
import com.example.onlyone.DTO.LoginDTO;
import com.example.onlyone.Common.Result;
import com.example.onlyone.Service.LoginService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthUserController {

    @Resource
    private LoginService userService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO userDTO, HttpServletResponse response) {
        Map<String, String> tokens = userService.login(userDTO, response);
        if (tokens == null) {
            return Result.error("用户名或密码错误");
        }
        Map<String, String> result = new HashMap<>();
        result.put("accessToken", tokens.get("accessToken"));
        result.put("refreshToken", tokens.get("refreshToken"));
        result.put("expiresIn", tokens.get("expiresIn"));
        return Result.success(result);
    }

    @PostMapping("/refresh")
    public Result refreshToken(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) Map<String, String> body) {
        com.example.onlyone.Service.ServiceImpl.LoginServiceImpl impl =
                (com.example.onlyone.Service.ServiceImpl.LoginServiceImpl) userService;
        String refreshToken = null;
        if (body != null && body.containsKey("refreshToken")) {
            refreshToken = body.get("refreshToken");
        } else {
            refreshToken = impl.getRefreshTokenFromCookie(request);
        }
        if (refreshToken == null) {
            return Result.error("未找到 Refresh Token");
        }
        Map<String, String> tokens = impl.refreshToken(refreshToken, response);
        if (tokens == null) {
            return Result.error("Refresh Token 无效或已过期，请重新登录");
        }
        return Result.success(tokens);
    }

    @PostMapping("/QQ/sendCode")
    public Result SendQQEmail(@RequestBody Map<String, String> emailMap) {
        String email = emailMap.get("email");
        Boolean isSend = userService.sendQQEmail(email);
        return Result.success(isSend);
    }

    @PostMapping("/QQ/login")
    public Result loginQQ(@RequestBody QQLoginDTO qqLoginDTO, HttpServletResponse response) {
        Map<String, String> tokens = userService.qqLogin(qqLoginDTO, response);
        if (tokens == null) {
            return Result.error("QQ邮箱登录失败");
        }
        Map<String, String> result = new HashMap<>();
        result.put("accessToken", tokens.get("accessToken"));
        result.put("refreshToken", tokens.get("refreshToken"));
        return Result.success(result);
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        userService.registerUser(registerDTO);
        return Result.success();
    }

    @PostMapping("/verify-code")
    public Result setCode(@RequestBody RegisterDTO registerDTO) {
        String code = userService.setCode(registerDTO);
        return Result.success(code);
    }

    @PostMapping("/loginOut")
    public Result loginOut(HttpServletRequest request, HttpServletResponse response) {
        userService.loginOut(request, response);
        return Result.success();
    }
}
