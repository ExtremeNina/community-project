package com.example.onlyone.Service;

import com.example.onlyone.Entity.Article;
import com.example.onlyone.Entity.User;
import com.example.onlyone.Mapper.ArticleAndLabelMapper;
import com.example.onlyone.Mapper.ArticleMapper;
import com.example.onlyone.Mapper.LabelMapper;
import com.example.onlyone.Mapper.UserMapper;
import com.example.onlyone.Service.ServiceImpl.CreateCenterServiceImpl;
import com.example.onlyone.Utils.ArticleDataConverterUtils;
import com.example.onlyone.Utils.HtmlSanitizer;
import com.example.onlyone.Utils.MinioUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCenterService 单元测试")
class CreateCenterServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private HtmlSanitizer htmlSanitizer;
    @Mock
    private ArticleAndLabelMapper articleAndLabelMapper;
    @Mock
    private MinioUtils minioUtils;
    @Mock
    private ArticleMapper articleMapper;
    @Mock
    private LabelMapper labelMapper;
    @Mock
    private ArticleDataConverterUtils articleDataConverterUtils;

    @InjectMocks
    private CreateCenterServiceImpl createCenterService;

    @Nested
    @DisplayName("deleteDraft 测试")
    class DeleteDraftTests {

        @Test
        @DisplayName("文章不存在时抛出异常")
        void shouldThrowExceptionWhenArticleNotFound() {
            when(articleMapper.selectById(999L)).thenReturn(null);

            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, "testUser", 1L);

                assertThrows(RuntimeException.class, () -> createCenterService.deleteDraft(999L));
                verify(articleMapper, never()).deleteById(anyLong());
            }
        }

        @Test
        @DisplayName("无权限删除他人草稿时抛出异常")
        void shouldThrowExceptionWhenNotOwner() {
            Article article = new Article();
            article.setId(100L);
            article.setAuthorId(2L);
            article.setStatus(1L);
            when(articleMapper.selectById(100L)).thenReturn(article);

            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, "testUser", 1L);

                assertThrows(SecurityException.class, () -> createCenterService.deleteDraft(100L));
                verify(articleMapper, never()).deleteById(anyLong());
            }
        }

        @Test
        @DisplayName("非草稿状态删除时抛出异常")
        void shouldThrowExceptionWhenNotDraftStatus() {
            Article article = new Article();
            article.setId(100L);
            article.setAuthorId(1L);
            article.setStatus(3L);
            when(articleMapper.selectById(100L)).thenReturn(article);

            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, "testUser", 1L);

                assertThrows(RuntimeException.class, () -> createCenterService.deleteDraft(100L));
                verify(articleMapper, never()).deleteById(anyLong());
            }
        }

        @Test
        @DisplayName("正常删除草稿")
        void shouldDeleteDraftSuccessfully() {
            Article article = new Article();
            article.setId(100L);
            article.setAuthorId(1L);
            article.setStatus(1L);
            when(articleMapper.selectById(100L)).thenReturn(article);
            when(articleMapper.deleteById(100L)).thenReturn(1);
            when(articleAndLabelMapper.deleteById(100L)).thenReturn(1);

            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, "testUser", 1L);

                assertDoesNotThrow(() -> createCenterService.deleteDraft(100L));
                verify(articleMapper).deleteById(100L);
                verify(articleAndLabelMapper).deleteById(100L);
            }
        }
    }

    @Nested
    @DisplayName("modifyDraft 测试")
    class ModifyDraftTests {

        @Test
        @DisplayName("草稿不存在时抛出异常")
        void shouldThrowExceptionWhenDraftNotFound() {
            when(articleMapper.selectDraftByUserId(100L)).thenReturn(null);

            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, "testUser", 1L);

                assertThrows(RuntimeException.class, () -> createCenterService.modifyDraft(100L));
            }
        }

        @Test
        @DisplayName("无权限编辑他人草稿时抛出异常")
        void shouldThrowExceptionWhenNotOwnerEdit() {
            Article article = new Article();
            article.setId(100L);
            article.setAuthorId(2L);
            when(articleMapper.selectDraftByUserId(100L)).thenReturn(article);

            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, "testUser", 1L);

                assertThrows(SecurityException.class, () -> createCenterService.modifyDraft(100L));
            }
        }

        @Test
        @DisplayName("正常获取草稿回显")
        void shouldReturnDraftForEdit() {
            Article article = new Article();
            article.setId(100L);
            article.setAuthorId(1L);
            article.setTitle("草稿文章");
            when(articleMapper.selectDraftByUserId(100L)).thenReturn(article);

            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, "testUser", 1L);

                Article result = createCenterService.modifyDraft(100L);

                assertNotNull(result);
                assertEquals("草稿文章", result.getTitle());
            }
        }
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> securityMock,
                                     String username, Long userId) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        com.example.onlyone.Entity.UserDetail userDetail =
                mock(com.example.onlyone.Entity.UserDetail.class);

        securityMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn(username);
        when(auth.getPrincipal()).thenReturn(userDetail);
        when(userDetail.getUserId()).thenReturn(userId);

        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        when(userMapper.selectByUsername(username)).thenReturn(user);
    }
}
