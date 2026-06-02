package com.example.onlyone.Utils;

import com.example.onlyone.Entity.UserDetail;
import com.example.onlyone.Properties.JwtProperties;
import com.example.onlyone.Security.JwtKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtProvider 双Token 单元测试")
class JwtProviderDualTokenTest {

    @Mock
    private Authentication authentication;

    private JwtProvider jwtProvider;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() throws Exception {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-for-unit-testing-1234567890");
        jwtProperties.setAccessExpiration(1800000L);
        jwtProperties.setRefreshExpiration(604800000L);

        java.security.Key key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes());
        jwtProvider = new JwtProvider(jwtProperties, key);
    }

    @Nested
    @DisplayName("generateAccessToken 测试")
    class GenerateAccessTokenTests {

        @Test
        @DisplayName("生成 Access Token 包含 type=access")
        void shouldContainAccessTokenType() {
            UserDetail userDetail = new UserDetail();
            org.springframework.test.util.ReflectionTestUtils.setField(userDetail, "userId", 1L);
            when(authentication.getName()).thenReturn("testUser");
            when(authentication.getPrincipal()).thenReturn(userDetail);

            String token = jwtProvider.generateAccessToken(authentication);

            assertNotNull(token);
            assertEquals("access", jwtProvider.getTokenType(token));
            assertEquals("testUser", jwtProvider.getUsernameFromToken(token));
            assertEquals(1L, jwtProvider.getUserIdFromToken(token));
            assertNotNull(jwtProvider.getJtiFromToken(token));
        }

        @Test
        @DisplayName("Access Token 过期时间 = accessExpiration 配置值")
        void shouldHaveAccessTokenExpiration() {
            UserDetail userDetail = new UserDetail();
            org.springframework.test.util.ReflectionTestUtils.setField(userDetail, "userId", 1L);
            when(authentication.getName()).thenReturn("testUser");
            when(authentication.getPrincipal()).thenReturn(userDetail);

            String token = jwtProvider.generateAccessToken(authentication);

            java.util.Date expiration = jwtProvider.getExpirationDateFromToken(token);
            long diff = expiration.getTime() - System.currentTimeMillis();
            assertTrue(diff > 1700000 && diff <= 1800000,
                    "expiration should be ~30min, actual: " + diff + "ms");
        }
    }

    @Nested
    @DisplayName("generateRefreshToken 测试")
    class GenerateRefreshTokenTests {

        @Test
        @DisplayName("生成 Refresh Token 包含 type=refresh")
        void shouldContainRefreshTokenType() {
            UserDetail userDetail = new UserDetail();
            org.springframework.test.util.ReflectionTestUtils.setField(userDetail, "userId", 1L);
            when(authentication.getName()).thenReturn("testUser");
            when(authentication.getPrincipal()).thenReturn(userDetail);

            String token = jwtProvider.generateRefreshToken(authentication);

            assertNotNull(token);
            assertEquals("refresh", jwtProvider.getTokenType(token));
            assertEquals("testUser", jwtProvider.getUsernameFromToken(token));
            assertEquals(1L, jwtProvider.getUserIdFromToken(token));
            assertNotNull(jwtProvider.getJtiFromToken(token));
        }

        @Test
        @DisplayName("Refresh Token 过期时间 = refreshExpiration 配置值")
        void shouldHaveRefreshTokenExpiration() {
            UserDetail userDetail = new UserDetail();
            org.springframework.test.util.ReflectionTestUtils.setField(userDetail, "userId", 1L);
            when(authentication.getName()).thenReturn("testUser");
            when(authentication.getPrincipal()).thenReturn(userDetail);

            String token = jwtProvider.generateRefreshToken(authentication);

            java.util.Date expiration = jwtProvider.getExpirationDateFromToken(token);
            long diff = expiration.getTime() - System.currentTimeMillis();
            assertTrue(diff > 600000000 && diff <= 604800000,
                    "expiration should be ~7 days, actual: " + diff + "ms");
        }
    }

    @Nested
    @DisplayName("isTokenExpired 测试")
    class IsTokenExpiredTests {

        @Test
        @DisplayName("有效 Token → isTokenExpired 返回 false")
        void shouldReturnFalseForValidToken() {
            UserDetail userDetail = new UserDetail();
            org.springframework.test.util.ReflectionTestUtils.setField(userDetail, "userId", 1L);
            when(authentication.getName()).thenReturn("testUser");
            when(authentication.getPrincipal()).thenReturn(userDetail);

            String token = jwtProvider.generateAccessToken(authentication);

            assertFalse(jwtProvider.isTokenExpired(token));
        }

        @Test
        @DisplayName("无效 Token → isTokenExpired 返回 true")
        void shouldReturnTrueForInvalidToken() {
            assertTrue(jwtProvider.isTokenExpired("invalid-token-string"));
        }
    }

    @Nested
    @DisplayName("getTokenType 测试")
    class GetTokenTypeTests {

        @Test
        @DisplayName("Access Token → 返回 'access'")
        void shouldReturnAccessForAccessToken() {
            UserDetail userDetail = new UserDetail();
            org.springframework.test.util.ReflectionTestUtils.setField(userDetail, "userId", 1L);
            when(authentication.getName()).thenReturn("testUser");
            when(authentication.getPrincipal()).thenReturn(userDetail);

            String token = jwtProvider.generateAccessToken(authentication);

            assertEquals("access", jwtProvider.getTokenType(token));
        }

        @Test
        @DisplayName("Refresh Token → 返回 'refresh'")
        void shouldReturnRefreshForRefreshToken() {
            UserDetail userDetail = new UserDetail();
            org.springframework.test.util.ReflectionTestUtils.setField(userDetail, "userId", 1L);
            when(authentication.getName()).thenReturn("testUser");
            when(authentication.getPrincipal()).thenReturn(userDetail);

            String token = jwtProvider.generateRefreshToken(authentication);

            assertEquals("refresh", jwtProvider.getTokenType(token));
        }
    }

    @Nested
    @DisplayName("generateToken 兼容性测试")
    class GenerateTokenCompatibility {

        @Test
        @DisplayName("generateToken 仍正常工作（委托给 generateAccessToken）")
        void shouldStillWork() {
            UserDetail userDetail = new UserDetail();
            org.springframework.test.util.ReflectionTestUtils.setField(userDetail, "userId", 1L);
            when(authentication.getName()).thenReturn("testUser");
            when(authentication.getPrincipal()).thenReturn(userDetail);

            String token = jwtProvider.generateToken(authentication);

            assertNotNull(token);
            assertEquals("access", jwtProvider.getTokenType(token));
        }
    }
}
