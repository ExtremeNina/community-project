package com.example.onlyone.Service;

import com.example.onlyone.DTO.LoginDTO;
import com.example.onlyone.DTO.QQLoginDTO;
import com.example.onlyone.Entity.User;
import com.example.onlyone.Mapper.UserMapper;
import com.example.onlyone.Properties.JwtProperties;
import com.example.onlyone.Service.ServiceImpl.LoginServiceImpl;
import com.example.onlyone.Utils.JwtProvider;
import com.example.onlyone.Utils.SpringContextUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("双Token认证 单元测试")
class LoginServiceImplDualTokenTest {

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private UserMapper userMapper;
    @Mock
    private MailSender mailSender;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private SetOperations<String, String> setOperations;
    @Mock
    private Authentication authentication;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private LoginServiceImpl loginService;

    private MockedStatic<SpringContextUtils> springContextUtilsMock;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-for-unit-testing-1234567890");
        jwtProperties.setAccessExpiration(1800000L);
        jwtProperties.setRefreshExpiration(604800000L);

        springContextUtilsMock = mockStatic(SpringContextUtils.class);
        springContextUtilsMock.when(() -> SpringContextUtils.getBean(JwtProperties.class))
                .thenReturn(jwtProperties);

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @AfterEach
    void tearDown() {
        springContextUtilsMock.close();
    }

    @Nested
    @DisplayName("login(LoginDTO, HttpServletResponse) 双Token登录测试")
    class DualTokenLoginTests {

        @Test
        @DisplayName("登录成功 → 返回AT，设置RT Cookie，RT存入Redis")
        void shouldReturnAccessTokenAndSetRefreshTokenCookie() {
            LoginDTO dto = new LoginDTO();
            dto.setUsername("testUser");
            dto.setPassword("password123");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(jwtProvider.generateAccessToken(authentication)).thenReturn("mock-access-token");
            when(jwtProvider.generateRefreshToken(authentication)).thenReturn("mock-refresh-token");
            when(jwtProvider.getJtiFromToken(anyString())).thenReturn("mock-jti");
            when(jwtProvider.getExpirationDateFromToken(anyString()))
                    .thenReturn(new Date(System.currentTimeMillis() + 604800000L));

            com.example.onlyone.Entity.UserDetail userDetail =
                    new com.example.onlyone.Entity.UserDetail();
            ReflectionTestUtils.setField(userDetail, "userId", 1L);
            when(authentication.getPrincipal()).thenReturn(userDetail);

            Map<String, String> tokens = loginService.login(dto, response);

            assertNotNull(tokens);
            assertEquals("mock-access-token", tokens.get("accessToken"));

            ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
            verify(response, atLeastOnce()).addCookie(cookieCaptor.capture());
            Cookie rtCookie = cookieCaptor.getAllValues().stream()
                    .filter(c -> "refresh_token".equals(c.getName()))
                    .findFirst().orElse(null);
            assertNotNull(rtCookie);
            assertTrue(rtCookie.isHttpOnly());
            assertEquals("/api/auth", rtCookie.getPath());

            verify(valueOperations).set(eq("refresh:1:mock-jti"), eq("active"), anyLong(), eq(TimeUnit.MILLISECONDS));
            verify(setOperations).add(eq("refresh:index:1"), eq("mock-jti"));
        }

        @Test
        @DisplayName("密码为空 → 返回 null")
        void shouldReturnNullWhenPasswordEmpty() {
            LoginDTO dto = new LoginDTO();
            dto.setUsername("testUser");
            dto.setPassword("");

            Map<String, String> tokens = loginService.login(dto, response);

            assertNull(tokens);
            verify(authenticationManager, never()).authenticate(any());
            verify(response, never()).addCookie(any(Cookie.class));
        }
    }

    @Nested
    @DisplayName("单参兼容 login(LoginDTO) 测试")
    class LegacyLoginTests {

        @Test
        @DisplayName("单参 login → 内部委托双参，返回 AT 字符串")
        void shouldDelegateToDualTokenLogin() {
            LoginDTO dto = new LoginDTO();
            dto.setUsername("testUser");
            dto.setPassword("password123");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(jwtProvider.generateAccessToken(authentication)).thenReturn("mock-access-token");
            when(jwtProvider.generateRefreshToken(authentication)).thenReturn("mock-refresh-token");
            when(jwtProvider.getJtiFromToken(anyString())).thenReturn("mock-jti");
            when(jwtProvider.getExpirationDateFromToken(anyString()))
                    .thenReturn(new Date(System.currentTimeMillis() + 604800000L));

            com.example.onlyone.Entity.UserDetail userDetail =
                    new com.example.onlyone.Entity.UserDetail();
            ReflectionTestUtils.setField(userDetail, "userId", 1L);
            when(authentication.getPrincipal()).thenReturn(userDetail);

            String token = loginService.login(dto);

            assertEquals("mock-access-token", token);
        }
    }

    @Nested
    @DisplayName("loginOut(HttpServletRequest, HttpServletResponse) 双Token登出测试")
    class DualTokenLogoutTests {

        @Test
        @DisplayName("登出 → 清除RT Redis白名单 + 清除RT Cookie + AT进入黑名单")
        void shouldClearRefreshTokenAndAddToBlacklist() {
            when(request.getHeader("Authorization")).thenReturn("Bearer mock-access-token");
            when(jwtProvider.getJtiFromToken("mock-access-token")).thenReturn("mock-jti");
            when(jwtProvider.getUserIdFromToken("mock-access-token")).thenReturn(1L);
            when(jwtProvider.getExpirationDateFromToken("mock-access-token"))
                    .thenReturn(new Date(System.currentTimeMillis() + 10000L));

            loginService.loginOut(request, response);

            verify(stringRedisTemplate).delete("refresh:index:1");
            verify(stringRedisTemplate).delete("refresh:1:mock-jti");
            verify(valueOperations).set(contains("black:jwt:"), eq("mock-access-token"), anyLong(), eq(TimeUnit.MILLISECONDS));

            ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
            verify(response, atLeastOnce()).addCookie(cookieCaptor.capture());
            Cookie rtCookie = cookieCaptor.getAllValues().stream()
                    .filter(c -> "refresh_token".equals(c.getName()))
                    .findFirst().orElse(null);
            assertNotNull(rtCookie);
            assertEquals(0, rtCookie.getMaxAge());
        }

        @Test
        @DisplayName("请求无 Authorization header → 仅清除 Cookie")
        void shouldOnlyClearCookieWhenNoAuthHeader() {
            when(request.getHeader("Authorization")).thenReturn(null);

            loginService.loginOut(request, response);

            verify(stringRedisTemplate, never()).delete(anyString());
            verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
        }
    }

    @Nested
    @DisplayName("refreshToken 刷新测试")
    class RefreshTokenTests {

        @Test
        @DisplayName("RT 有效 → 返回新 AT，设置新 RT Cookie，旧 RT 标记为 used")
        void shouldReturnNewAccessTokenWhenRefreshTokenValid() {
            when(jwtProvider.validateToken("valid-rt")).thenReturn(true);
            when(jwtProvider.getTokenType("valid-rt")).thenReturn("refresh");
            when(jwtProvider.getUserIdFromToken("valid-rt")).thenReturn(1L);
            when(jwtProvider.getJtiFromToken("valid-rt")).thenReturn("old-jti");
            when(jwtProvider.getUsernameFromToken("valid-rt")).thenReturn("testUser");
            when(jwtProvider.getExpirationDateFromToken("valid-rt"))
                    .thenReturn(new Date(System.currentTimeMillis() + 600000L));

            when(valueOperations.get("refresh:1:old-jti")).thenReturn("active");

            User user = new User();
            user.setId(1L);
            user.setUsername("testUser");
            when(userMapper.selectByUsername("testUser")).thenReturn(user);

            when(jwtProvider.getJtiFromToken(anyString()))
                    .thenReturn("old-jti")
                    .thenReturn("new-jti");
            when(jwtProvider.getExpirationDateFromToken(anyString()))
                    .thenReturn(new Date(System.currentTimeMillis() + 604800000L));

            String newAT = loginService.refreshToken("valid-rt", response).toString();

            assertNotNull(newAT);
            verify(valueOperations).set(eq("refresh:1:old-jti"), eq("used"), anyLong(), eq(TimeUnit.MILLISECONDS));
            verify(valueOperations).set(eq("refresh:1:new-jti"), eq("active"), anyLong(), eq(TimeUnit.MILLISECONDS));
            verify(setOperations).add(eq("refresh:index:1"), eq("new-jti"));
            verify(response, atLeastOnce()).addCookie(any(Cookie.class));
        }

        @Test
        @DisplayName("RT 为空 → 返回 null")
        void shouldReturnNullWhenRefreshTokenEmpty() {
            String newAT = loginService.refreshToken("", response).toString();
            assertNull(newAT);
        }

        @Test
        @DisplayName("RT 已过期 → 返回 null")
        void shouldReturnNullWhenRefreshTokenExpired() {
            when(jwtProvider.validateToken("expired-rt")).thenReturn(false);

            String newAT = loginService.refreshToken("expired-rt", response).toString();

            assertNull(newAT);
            verify(valueOperations, never()).set(eq(contains("refresh:")), anyString(), anyLong(), any());
        }

        @Test
        @DisplayName("Token 类型不是 refresh → 返回 null")
        void shouldReturnNullWhenTokenTypeNotRefresh() {
            when(jwtProvider.validateToken("access-type-token")).thenReturn(true);
            when(jwtProvider.getTokenType("access-type-token")).thenReturn("access");

            String newAT = loginService.refreshToken("access-type-token", response).toString();

            assertNull(newAT);
        }

        @Test
        @DisplayName("RT 已被使用（重用检测）→ 返回 null")
        void shouldDetectTokenReuse() {
            when(jwtProvider.validateToken("used-rt")).thenReturn(true);
            when(jwtProvider.getTokenType("used-rt")).thenReturn("refresh");
            when(jwtProvider.getUserIdFromToken("used-rt")).thenReturn(1L);
            when(jwtProvider.getJtiFromToken("used-rt")).thenReturn("reused-jti");

            when(valueOperations.get("refresh:1:reused-jti")).thenReturn("used");

            String newAT = loginService.refreshToken("used-rt", response).toString();

            assertNull(newAT);
            verify(stringRedisTemplate).delete("refresh:index:1");
        }
    }

    @Nested
    @DisplayName("getRefreshTokenFromCookie 测试")
    class RefreshTokenCookieTests {

        @Test
        @DisplayName("从 Cookie 中提取 Refresh Token")
        void shouldExtractRefreshTokenFromCookie() {
            Cookie refreshCookie = new Cookie("refresh_token", "test-rt-value");
            Cookie otherCookie = new Cookie("other", "value");
            when(request.getCookies()).thenReturn(new Cookie[]{otherCookie, refreshCookie});

            String rt = loginService.getRefreshTokenFromCookie(request);

            assertEquals("test-rt-value", rt);
        }

        @Test
        @DisplayName("无 Cookie → 返回 null")
        void shouldReturnNullWhenNoCookies() {
            when(request.getCookies()).thenReturn(null);

            String rt = loginService.getRefreshTokenFromCookie(request);

            assertNull(rt);
        }

        @Test
        @DisplayName("Cookie 中无 refresh_token → 返回 null")
        void shouldReturnNullWhenNoRefreshTokenCookie() {
            when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("other", "value")});

            String rt = loginService.getRefreshTokenFromCookie(request);

            assertNull(rt);
        }
    }

    @Nested
    @DisplayName("QQ 双Token登录测试")
    class QQDualTokenLoginTests {

        @Test
        @DisplayName("QQ 老用户登录 → 返回AT + 设RT Cookie")
        void shouldLoginExistingQQUserWithDualToken() {
            QQLoginDTO dto = new QQLoginDTO();
            dto.setMail("test@qq.com");
            dto.setCode("123456");

            when(stringRedisTemplate.hasKey("QQlogin:codetest@qq.com")).thenReturn(true);
            when(valueOperations.get("QQlogin:codetest@qq.com")).thenReturn("123456");

            User user = new User();
            user.setId(1L);
            user.setUsername("testUser");
            user.setPassword("encodedPassword");
            when(userMapper.selectByMail("test@qq.com")).thenReturn(user);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(jwtProvider.generateAccessToken(authentication)).thenReturn("mock-at");
            when(jwtProvider.generateRefreshToken(authentication)).thenReturn("mock-rt");
            when(jwtProvider.getJtiFromToken(anyString())).thenReturn("mock-jti");
            when(jwtProvider.getExpirationDateFromToken(anyString()))
                    .thenReturn(new Date(System.currentTimeMillis() + 604800000L));

            com.example.onlyone.Entity.UserDetail userDetail =
                    new com.example.onlyone.Entity.UserDetail();
            ReflectionTestUtils.setField(userDetail, "userId", 1L);
            when(authentication.getPrincipal()).thenReturn(userDetail);

            Map<String, String> tokens = loginService.qqLogin(dto, response);

            assertNotNull(tokens);
            assertEquals("mock-at", tokens.get("accessToken"));
            verify(response, atLeastOnce()).addCookie(any(Cookie.class));
        }
    }
}
