package com.example.onlyone.Service;

import com.example.onlyone.DTO.LoginDTO;
import com.example.onlyone.DTO.QQLoginDTO;
import com.example.onlyone.DTO.RegisterDTO;
import com.example.onlyone.Entity.User;
import com.example.onlyone.Entity.UserAndRole;
import com.example.onlyone.Mapper.UserAndRoleMapper;
import com.example.onlyone.Mapper.UserMapper;
import com.example.onlyone.Service.ServiceImpl.LoginServiceImpl;
import com.example.onlyone.Utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 单元测试")
class LoginServiceImplTest {

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private UserAndRoleMapper userAndRoleMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private MailSender mailSender;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Nested
    @DisplayName("insertUser 测试")
    class InsertUserTests {

        @Test
        @DisplayName("正常插入用户")
        void shouldInsertUserSuccessfully() {
            LoginDTO dto = new LoginDTO();
            dto.setUsername("testUser");
            dto.setPassword("password123");

            when(userMapper.insert(any(User.class))).thenReturn(1);

            loginService.insertUser(dto);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userMapper).insert(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertEquals("testUser", savedUser.getUsername());
            assertEquals("password123", savedUser.getPassword());
            assertEquals(1, savedUser.getStatus());
            assertNotNull(savedUser.getCreateTime());
            assertNotNull(savedUser.getUpdateTime());
        }
    }

    @Nested
    @DisplayName("login 测试")
    class LoginTests {

        @Test
        @DisplayName("密码为空时返回 null")
        void shouldReturnNullWhenPasswordEmpty() {
            LoginDTO dto = new LoginDTO();
            dto.setUsername("testUser");
            dto.setPassword("");

            String result = loginService.login(dto);

            assertNull(result);
            verify(authenticationManager, never()).authenticate(any());
        }

        @Test
        @DisplayName("正常登录返回 JWT Token")
        void shouldReturnJwtTokenOnSuccess() {
            LoginDTO dto = new LoginDTO();
            dto.setUsername("testUser");
            dto.setPassword("password123");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(jwtProvider.generateToken(authentication)).thenReturn("mock-jwt-token");

            String result = loginService.login(dto);

            assertNotNull(result);
            assertEquals("mock-jwt-token", result);
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtProvider).generateToken(authentication);
        }
    }

    @Nested
    @DisplayName("registerUser 测试")
    class RegisterUserTests {

        @Test
        @DisplayName("信息不完整时抛出异常")
        void shouldThrowExceptionWhenInfoIncomplete() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("");
            dto.setOnePassword("123456");
            dto.setTwoPassword("123456");

            assertThrows(IllegalArgumentException.class, () -> loginService.registerUser(dto));
            verify(userMapper, never()).insert(any());
        }

        @Test
        @DisplayName("两次密码不一致时抛出异常")
        void shouldThrowExceptionWhenPasswordsMismatch() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("testUser");
            dto.setOnePassword("123456");
            dto.setTwoPassword("654321");

            assertThrows(IllegalArgumentException.class, () -> loginService.registerUser(dto));
            verify(userMapper, never()).insert(any());
        }

        @Test
        @DisplayName("验证码错误时抛出异常")
        void shouldThrowExceptionWhenCodeIncorrect() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("testUser");
            dto.setOnePassword("123456");
            dto.setTwoPassword("123456");
            dto.setCode("000000");

            when(valueOperations.get("login:code:testUser")).thenReturn("999999");

            assertThrows(IllegalArgumentException.class, () -> loginService.registerUser(dto));
            verify(userMapper, never()).insert(any());
        }

        @Test
        @DisplayName("注册成功")
        void shouldRegisterSuccessfully() {
            RegisterDTO dto = new RegisterDTO();
            dto.setUsername("testUser");
            dto.setOnePassword("123456");
            dto.setTwoPassword("123456");
            dto.setCode("123456");

            when(valueOperations.get("login:code:testUser")).thenReturn("123456");
            when(userMapper.insert(any(User.class))).thenReturn(1);
            when(userAndRoleMapper.insert(any(UserAndRole.class))).thenReturn(1);

            assertDoesNotThrow(() -> loginService.registerUser(dto));

            verify(stringRedisTemplate).delete("login:code:testUser");
            verify(userMapper).insert(any(User.class));
            verify(userAndRoleMapper).insert(any(UserAndRole.class));
        }
    }

    @Nested
    @DisplayName("sendQQEmail 测试")
    class SendQQEmailTests {

        @Test
        @DisplayName("非法邮箱格式返回 false")
        void shouldReturnFalseForInvalidEmail() {
            boolean result = loginService.sendQQEmail("not-an-email");

            assertFalse(result);
            verify(mailSender, never()).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("正确 QQ 邮箱发送成功")
        void shouldSendEmailSuccessfully() {
            doNothing().when(mailSender).send(any(SimpleMailMessage.class));

            boolean result = loginService.sendQQEmail("test@qq.com");

            assertTrue(result);
            verify(mailSender).send(any(SimpleMailMessage.class));
            verify(valueOperations).set(contains("QQlogin:code"), anyString(), eq(5L), eq(TimeUnit.MINUTES));
        }
    }

    @Nested
    @DisplayName("qqLogin 测试")
    class QqLoginTests {

        @Test
        @DisplayName("邮箱对应 Redis Key 不存在时返回 null")
        void shouldReturnNullWhenEmailKeyNotFound() {
            QQLoginDTO dto = new QQLoginDTO();
            dto.setMail("test@qq.com");
            dto.setCode("123456");

            when(stringRedisTemplate.hasKey("QQlogin:codetest@qq.com")).thenReturn(false);

            String result = loginService.qqLogin(dto);

            assertNull(result);
        }

        @Test
        @DisplayName("验证码错误返回 null")
        void shouldReturnNullWhenCodeIncorrect() {
            QQLoginDTO dto = new QQLoginDTO();
            dto.setMail("test@qq.com");
            dto.setCode("123456");

            when(stringRedisTemplate.hasKey("QQlogin:codetest@qq.com")).thenReturn(true);
            when(valueOperations.get("QQlogin:codetest@qq.com")).thenReturn("999999");

            String result = loginService.qqLogin(dto);

            assertNull(result);
            verify(authenticationManager, never()).authenticate(any());
        }
    }
}
