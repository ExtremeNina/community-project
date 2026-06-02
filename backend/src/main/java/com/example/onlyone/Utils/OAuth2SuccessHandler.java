package com.example.onlyone.Utils;

import com.example.onlyone.Entity.CustomOAuth2User;
import com.example.onlyone.Service.ServiceImpl.LoginServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        JwtProvider jwtProvider = SpringContextUtils.getBean(JwtProvider.class);
        LoginServiceImpl loginService = SpringContextUtils.getBean(LoginServiceImpl.class);

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuth2User) {
            String accessToken = jwtProvider.generateAccessToken(authentication);
            String refreshToken = jwtProvider.generateRefreshToken(authentication);

            CustomOAuth2User oAuth2User = (CustomOAuth2User) principal;
            java.util.Map<String, Object> attributes = oAuth2User.getAttributes();
            Long userId = attributes.containsKey("id") ? ((Number) attributes.get("id")).longValue() : null;

            loginService.setRefreshTokenCookie(response, refreshToken);
            loginService.saveRefreshToken(userId, refreshToken);

            log.info("GitHub 用户 Token 生成成功, userId={}", userId);
            String redirectUrl = String.format("%s/oauth/callback?token=%s",
                    "http://localhost:5173",
                    URLEncoder.encode(accessToken, StandardCharsets.UTF_8));

            response.sendRedirect(redirectUrl);
        } else {
            log.info("Authentication 类型: {}", authentication.getClass().getName());
            log.info("principal类型为: {}", principal.getClass().getName());
        }
    }
}
