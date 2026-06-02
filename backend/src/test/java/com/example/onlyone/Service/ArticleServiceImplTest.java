package com.example.onlyone.Service;

import com.example.onlyone.Entity.Article;
import com.example.onlyone.Entity.Category;
import com.example.onlyone.Entity.User;
import com.example.onlyone.Mapper.ArticleMapper;
import com.example.onlyone.Mapper.CategoryMapper;
import com.example.onlyone.Mapper.CommentMapper;
import com.example.onlyone.Mapper.UserMapper;
import com.example.onlyone.Service.ServiceImpl.ArticleServiceImpl;
import com.example.onlyone.Utils.ArticleDataConverterUtils;
import com.example.onlyone.VO.UserProfileVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleService 单元测试")
class ArticleServiceImplTest {

    @Mock
    private LoveService loveService;
    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private FollowService followService;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    private ArticleDataConverterUtils articleDataConverterUtils;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Nested
    @DisplayName("getArticleDetail 测试")
    class GetArticleDetailTests {

        @Test
        @DisplayName("文章不存在时抛出异常")
        void shouldThrowExceptionWhenArticleNotFound() {
            when(articleMapper.selectByArticleId(999L)).thenReturn(null);

            assertThrows(RuntimeException.class, () -> articleService.getArticleDetail(999L));
        }

        @Test
        @DisplayName("正常获取文章详情")
        void shouldReturnArticleDetail() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                SecurityContext securityContext = mock(SecurityContext.class);
                Authentication auth = mock(Authentication.class);
                securityMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(auth);
                when(auth.getName()).thenReturn("testUser");

                Article article = new Article();
                article.setId(100L);
                article.setTitle("测试文章");
                article.setAuthorId(1L);
                article.setCategoryId(10L);
                article.setContent("文章内容");
                article.setCreateTime(LocalDateTime.now());
                article.setPageview(100L);
                when(articleMapper.selectByArticleId(100L)).thenReturn(article);

                User author = new User();
                author.setId(1L);
                author.setUsername("authorUser");
                author.setIcon("/icons/author.png");
                when(userMapper.selectByUsername("testUser")).thenReturn(author);
                when(userMapper.selectById(1L)).thenReturn(author);

                Category category = new Category();
                category.setId(10L);
                category.setCategoryName("技术");
                when(categoryMapper.selectById(10L)).thenReturn(category);

                when(followService.isFollowing(anyLong(), anyLong())).thenReturn(false);
                when(userProfileService.getFansCountFromCacheOrDB(anyLong())).thenReturn(0L);
                when(userProfileService.getFollowingCountFromCacheOrDB(anyLong())).thenReturn(0L);
                when(userProfileService.getAllLoveCount(anyLong())).thenReturn(0L);
                when(loveService.isEntityLovedByUser(anyLong(), anyLong())).thenReturn(false);
                when(loveService.getEntityLoveCount(anyLong(), anyLong())).thenReturn("0");
                when(commentMapper.countByArticleId(anyLong())).thenReturn(5L);

                var result = articleService.getArticleDetail(100L);

                assertNotNull(result);
                assertEquals("测试文章", result.getTitle());
                assertEquals("技术", result.getCategory());
                verify(articleMapper).incrementViewCount(100L);
            }
        }
    }

    @Nested
    @DisplayName("updateArticleStatus 测试")
    class UpdateArticleStatusTests {

        @Test
        @DisplayName("articleId 为 null 时抛出异常")
        void shouldThrowExceptionWhenArticleIdNull() {
            assertThrows(RuntimeException.class, () -> articleService.updateArticleStatus(null, 3));
            verify(articleMapper, never()).updateArticleByStatus(anyLong(), anyInt());
        }

        @Test
        @DisplayName("status 为 null 时抛出异常")
        void shouldThrowExceptionWhenStatusNull() {
            assertThrows(RuntimeException.class, () -> articleService.updateArticleStatus(100L, null));
            verify(articleMapper, never()).updateArticleByStatus(anyLong(), anyInt());
        }

        @Test
        @DisplayName("正常更新文章状态")
        void shouldUpdateArticleStatusSuccessfully() {
            when(articleMapper.selectById(100L)).thenReturn(new Article());
            doNothing().when(articleMapper).updateArticleByStatus(eq(100L), eq(3));

            assertDoesNotThrow(() -> articleService.updateArticleStatus(100L, 3));
            verify(articleMapper).updateArticleByStatus(100L, 3);
        }
    }
}
