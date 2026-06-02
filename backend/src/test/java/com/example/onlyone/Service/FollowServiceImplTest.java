package com.example.onlyone.Service;

import com.example.onlyone.Entity.Follow;
import com.example.onlyone.Entity.UserDetail;
import com.example.onlyone.Exception.BusinessException;
import com.example.onlyone.Mapper.FollowMapper;
import com.example.onlyone.Service.ServiceImpl.FollowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowService 单元测试（优化后）")
class FollowServiceImplTest {

    @Mock
    private FollowMapper followMapper;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private FollowServiceImpl followService;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Nested
    @DisplayName("toggleFollow 测试")
    class ToggleFollowTests {

        @Test
        @DisplayName("目标用户 ID 为空时抛出 BusinessException")
        void shouldThrowExceptionWhenAuthorIdNull() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                BusinessException ex = assertThrows(BusinessException.class,
                        () -> followService.toggleFollow(null));
                assertEquals(400, ex.getCode());
                assertTrue(ex.getMessage().contains("目标用户ID不能为空"));
            }
        }

        @Test
        @DisplayName("关注自己时抛出 BusinessException")
        void shouldThrowExceptionWhenFollowSelf() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                BusinessException ex = assertThrows(BusinessException.class,
                        () -> followService.toggleFollow(1L));
                assertEquals(400, ex.getCode());
                assertTrue(ex.getMessage().contains("不能关注自己"));
            }
        }

        @Test
        @DisplayName("未关注时 insert 成功返回 true")
        void shouldReturnTrueWhenNotFollowing() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                when(followMapper.selectByUserId(1L, 2L)).thenReturn(null);
                when(followMapper.insert(any(Follow.class))).thenReturn(1);

                boolean result = followService.toggleFollow(2L);

                assertTrue(result);
                ArgumentCaptor<Follow> captor = ArgumentCaptor.forClass(Follow.class);
                verify(followMapper).selectByUserId(1L, 2L);
                verify(followMapper).insert(captor.capture());
                assertEquals(1L, captor.getValue().getFollowerId());
                assertEquals(2L, captor.getValue().getFollowingId());
            }
        }

        @Test
        @DisplayName("已关注时 delete 成功返回 false")
        void shouldReturnFalseWhenAlreadyFollowing() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                Follow existing = new Follow();
                existing.setFollowerId(1L);
                existing.setFollowingId(2L);
                when(followMapper.selectByUserId(1L, 2L)).thenReturn(existing);

                boolean result = followService.toggleFollow(2L);

                assertFalse(result);
                verify(followMapper).selectByUserId(1L, 2L);
                verify(followMapper).deleteByUserId(1L, 2L);
                verify(followMapper, never()).insert(any());
            }
        }
    }

    @Nested
    @DisplayName("isFollowing 测试")
    class IsFollowingTests {

        @Test
        @DisplayName("缓存命中时返回 true")
        void shouldReturnTrueWhenCacheHit() {
            when(setOperations.isMember(anyString(), eq("2"))).thenReturn(true);

            Boolean result = followService.isFollowing(1L, 2L);

            assertTrue(result);
            verify(followMapper, never()).selectByUserId(anyLong(), anyLong());
        }

        @Test
        @DisplayName("缓存未命中查数据库")
        void shouldQueryDatabaseWhenCacheMiss() {
            when(setOperations.isMember(anyString(), eq("2"))).thenReturn(false);

            Follow follow = new Follow();
            follow.setFollowerId(1L);
            follow.setFollowingId(2L);
            when(followMapper.selectByUserId(1L, 2L)).thenReturn(follow);

            Boolean result = followService.isFollowing(1L, 2L);

            assertTrue(result);
            verify(followMapper).selectByUserId(1L, 2L);
        }

        @Test
        @DisplayName("缓存和数据库都没有返回 false")
        void shouldReturnFalseWhenNotFollowing() {
            when(setOperations.isMember(anyString(), eq("2"))).thenReturn(false);
            when(followMapper.selectByUserId(1L, 2L)).thenReturn(null);

            Boolean result = followService.isFollowing(1L, 2L);

            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("getFansCount 测试")
    class GetFansCountTests {

        @Test
        @DisplayName("缓存命中返回粉丝数")
        void shouldReturnCachedFansCount() {
            when(valueOperations.get("user:fansCount:1")).thenReturn("100");

            Long result = followService.getFansCount(1L);

            assertEquals(100L, result);
            verify(followMapper, never()).countFollowers(anyLong());
        }

        @Test
        @DisplayName("缓存未命中查数据库并回填")
        void shouldQueryAndFillCacheWhenMiss() {
            when(valueOperations.get("user:fansCount:1")).thenReturn(null);
            when(followMapper.countFollowers(1L)).thenReturn(50L);

            Long result = followService.getFansCount(1L);

            assertEquals(50L, result);
            verify(followMapper).countFollowers(1L);
        }
    }

    @Nested
    @DisplayName("getFollowingCount 测试")
    class GetFollowingCountTests {

        @Test
        @DisplayName("正常获取关注数")
        void shouldReturnFollowingCount() {
            when(valueOperations.get("user:followingCount:1")).thenReturn("30");

            Long result = followService.getFollowingCount(1L);

            assertEquals(30L, result);
        }
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> securityMock, Long userId) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        UserDetail userDetail = mock(UserDetail.class);

        securityMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(userDetail);
        when(userDetail.getUserId()).thenReturn(userId);
    }
}
