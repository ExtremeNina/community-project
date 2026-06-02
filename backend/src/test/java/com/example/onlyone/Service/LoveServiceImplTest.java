package com.example.onlyone.Service;

import com.example.onlyone.DTO.LoveDTO;
import com.example.onlyone.Entity.Article;
import com.example.onlyone.Entity.Comment;
import com.example.onlyone.Entity.UserLove;
import com.example.onlyone.Exception.BusinessException;
import com.example.onlyone.Mapper.ArticleMapper;
import com.example.onlyone.Mapper.CommentMapper;
import com.example.onlyone.Mapper.UserLoveMapper;
import com.example.onlyone.Service.ServiceImpl.LoveServiceImpl;
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

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoveService 单元测试（优化后）")
class LoveServiceImplTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private UserLoveMapper userLoveMapper;
    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private LoveServiceImpl loveService;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Nested
    @DisplayName("toggleLike 测试")
    class ToggleLikeTests {

        @Test
        @DisplayName("实体 ID 为空时抛出 BusinessException")
        void shouldThrowExceptionWhenEntityIdNull() {
            LoveDTO dto = new LoveDTO();
            dto.setId(null);
            dto.setLoveTypeId(LoveServiceImpl.LOVE_TYPE_ARTICLE);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> loveService.toggleLike(dto));
            assertEquals(400, ex.getCode());
            verify(userLoveMapper, never()).insert(any());
        }

        @Test
        @DisplayName("无效点赞类型 ID 时抛出 BusinessException")
        void shouldThrowExceptionWhenInvalidLoveType() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                LoveDTO dto = new LoveDTO();
                dto.setId(100L);
                dto.setLoveTypeId(999L);

                BusinessException ex = assertThrows(BusinessException.class,
                        () -> loveService.toggleLike(dto));
                assertEquals(400, ex.getCode());
                assertTrue(ex.getMessage().contains("无效的点赞类型ID"));
            }
        }

        @Test
        @DisplayName("分布式锁获取失败时抛出 BusinessException")
        void shouldThrowExceptionWhenLockFailed() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                    .thenReturn(false);

            LoveDTO dto = new LoveDTO();
            dto.setId(100L);
            dto.setLoveTypeId(LoveServiceImpl.LOVE_TYPE_ARTICLE);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> loveService.toggleLike(dto));
            assertEquals(409, ex.getCode());
        }

        @Test
        @DisplayName("首次点赞：insert 新记录 + 更新文章点赞数")
        void shouldInsertNewLoveRecord() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                        .thenReturn(true);
                when(userLoveMapper.selectByUserAndEntity(1L, 100L, LoveServiceImpl.LOVE_TYPE_ARTICLE))
                        .thenReturn(null);
                when(userLoveMapper.insert(any(UserLove.class))).thenReturn(1);

                Article article = new Article();
                article.setId(100L);
                article.setLove(5L);
                when(articleMapper.selectById(100L)).thenReturn(article);

                LoveDTO dto = new LoveDTO();
                dto.setId(100L);
                dto.setLoveTypeId(LoveServiceImpl.LOVE_TYPE_ARTICLE);

                assertDoesNotThrow(() -> loveService.toggleLike(dto));

                verify(userLoveMapper).selectByUserAndEntity(1L, 100L, LoveServiceImpl.LOVE_TYPE_ARTICLE);
                verify(userLoveMapper).insert(any(UserLove.class));
                verify(userLoveMapper, never()).cancelLove(anyLong(), anyLong(), anyLong());
            }
        }

        @Test
        @DisplayName("取消点赞：cancelLove 软删除 + 更新文章点赞数")
        void shouldCancelLoveSoftDelete() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class)))
                        .thenReturn(true);

                UserLove existing = new UserLove();
                existing.setId(10L);
                existing.setUserId(1L);
                existing.setEntityId(100L);
                existing.setLoveTypeId(LoveServiceImpl.LOVE_TYPE_ARTICLE);
                existing.setStatus(1);
                when(userLoveMapper.selectByUserAndEntity(1L, 100L, LoveServiceImpl.LOVE_TYPE_ARTICLE))
                        .thenReturn(existing);

                Article article = new Article();
                article.setId(100L);
                article.setLove(5L);
                when(articleMapper.selectById(100L)).thenReturn(article);

                LoveDTO dto = new LoveDTO();
                dto.setId(100L);
                dto.setLoveTypeId(LoveServiceImpl.LOVE_TYPE_ARTICLE);

                assertDoesNotThrow(() -> loveService.toggleLike(dto));

                verify(userLoveMapper).cancelLove(1L, 100L, LoveServiceImpl.LOVE_TYPE_ARTICLE);
                verify(userLoveMapper, never()).insert(any(UserLove.class));
            }
        }
    }

    @Nested
    @DisplayName("isEntityLovedByUser 测试")
    class IsEntityLovedByUserTests {

        @Test
        @DisplayName("entityId 为 null 返回 false")
        void shouldReturnFalseWhenEntityIdNull() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                Boolean result = loveService.isEntityLovedByUser(null, LoveServiceImpl.LOVE_TYPE_ARTICLE);
                assertFalse(result);
            }
        }

        @Test
        @DisplayName("缓存命中返回 true")
        void shouldReturnTrueWhenCacheHit() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                when(setOperations.isMember(anyString(), eq("100"))).thenReturn(true);

                Boolean result = loveService.isEntityLovedByUser(100L, LoveServiceImpl.LOVE_TYPE_ARTICLE);
                assertTrue(result);
            }
        }

        @Test
        @DisplayName("缓存未命中查询 status=1 的有效记录")
        void shouldQueryActiveLoveRecord() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                when(setOperations.isMember(anyString(), eq("100"))).thenReturn(false);

                UserLove userLove = new UserLove();
                userLove.setUserId(1L);
                userLove.setEntityId(100L);
                userLove.setLoveTypeId(LoveServiceImpl.LOVE_TYPE_ARTICLE);
                userLove.setStatus(1);
                when(userLoveMapper.selectByUserAndEntity(1L, 100L, LoveServiceImpl.LOVE_TYPE_ARTICLE))
                        .thenReturn(userLove);

                Boolean result = loveService.isEntityLovedByUser(100L, LoveServiceImpl.LOVE_TYPE_ARTICLE);
                assertTrue(result);
            }
        }
    }

    @Nested
    @DisplayName("getEntityLoveCount 测试")
    class GetEntityLoveCountTests {

        @Test
        @DisplayName("entityId 为 null 返回 0")
        void shouldReturnZeroWhenEntityIdNull() {
            assertEquals("0", loveService.getEntityLoveCount(null, LoveServiceImpl.LOVE_TYPE_ARTICLE));
        }

        @Test
        @DisplayName("缓存命中直接返回")
        void shouldReturnCachedValue() {
            when(valueOperations.get(anyString())).thenReturn("42");

            String result = loveService.getEntityLoveCount(100L, LoveServiceImpl.LOVE_TYPE_ARTICLE);

            assertEquals("42", result);
        }

        @Test
        @DisplayName("缓存未命中从文章表加载")
        void shouldLoadFromArticleWhenCacheMiss() {
            when(valueOperations.get(anyString())).thenReturn(null);

            Article article = new Article();
            article.setId(100L);
            article.setLove(10L);
            when(articleMapper.selectById(100L)).thenReturn(article);

            String result = loveService.getEntityLoveCount(100L, LoveServiceImpl.LOVE_TYPE_ARTICLE);

            assertEquals("10", result);
        }
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> securityMock, Long userId) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        com.example.onlyone.Entity.UserDetail userDetail =
                mock(com.example.onlyone.Entity.UserDetail.class);

        securityMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(userDetail);
        when(userDetail.getUserId()).thenReturn(userId);
    }
}
