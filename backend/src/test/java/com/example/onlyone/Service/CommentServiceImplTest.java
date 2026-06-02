package com.example.onlyone.Service;

import com.example.onlyone.DTO.CommentDTO;
import com.example.onlyone.Entity.Comment;
import com.example.onlyone.Entity.User;
import com.example.onlyone.Mapper.CommentMapper;
import com.example.onlyone.Mapper.UserMapper;
import com.example.onlyone.Service.ServiceImpl.CommentServiceImpl;
import com.example.onlyone.VO.CommentVO;
import com.example.onlyone.VO.UserProfileVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService 单元测试（优化后）")
class CommentServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private LoveService loveService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
    }

    @Nested
    @DisplayName("getUserProfile 测试")
    class GetUserProfileTests {

        @Test
        @DisplayName("正常获取用户基本信息")
        void shouldReturnUserProfile() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                User user = new User();
                user.setId(1L);
                user.setUsername("testUser");
                user.setIcon("/icons/avatar.png");
                when(userMapper.selectById(1L)).thenReturn(user);

                UserProfileVO result = commentService.getUserProfile();

                assertNotNull(result);
                assertEquals(1L, result.getUserId());
                assertEquals("testUser", result.getUsername());
                assertEquals("/icons/avatar.png", result.getIcon());
            }
        }

        @Test
        @DisplayName("用户不存在时抛出空指针")
        void shouldThrowNPEWhenUserNotFound() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);
                when(userMapper.selectById(1L)).thenReturn(null);

                assertThrows(NullPointerException.class, () -> commentService.getUserProfile());
            }
        }
    }

    @Nested
    @DisplayName("publishComment 测试")
    class PublishCommentTests {

        @Test
        @DisplayName("isCommented 为 0 时返回 null")
        void shouldReturnNullWhenNotCommented() {
            CommentDTO dto = new CommentDTO();
            dto.setIsCommented(0);

            CommentVO result = commentService.publishComment(dto);

            assertNull(result);
            verify(commentMapper, never()).insert(any());
        }

        @Test
        @DisplayName("发表根评论：parentId 为空，root_id 设为 0")
        void shouldPublishRootComment() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                User user = new User();
                user.setId(1L);
                user.setUsername("testUser");
                user.setIcon("/icons/avatar.png");
                when(userMapper.selectById(1L)).thenReturn(user);
                when(commentMapper.insert(any(Comment.class))).thenReturn(1);

                CommentDTO dto = new CommentDTO();
                dto.setContent("测试评论");
                dto.setArticleId(100L);
                dto.setIsCommented(1);

                CommentVO result = commentService.publishComment(dto);

                assertNotNull(result);
                assertEquals("testUser", result.getUsername());
                assertEquals("测试评论", result.getContent());

                ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
                verify(commentMapper).insert(captor.capture());
                assertEquals(0L, captor.getValue().getRootId());
                assertNull(captor.getValue().getParentId());
                verify(commentMapper, never()).updateReplyCount(anyLong(), anyInt());
            }
        }

        @Test
        @DisplayName("回复根评论：root_id 指向根评论 ID，触发 reply_count +1")
        void shouldReplyToRootComment() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                User user = new User();
                user.setId(1L);
                user.setUsername("testUser");
                user.setIcon("/icons/avatar.png");
                when(userMapper.selectById(1L)).thenReturn(user);

                Comment parent = new Comment();
                parent.setId(10L);
                parent.setRootId(0L);
                parent.setUsername("authorUser");
                when(commentMapper.selectById(10L)).thenReturn(parent);
                when(commentMapper.insert(any(Comment.class))).thenReturn(1);

                CommentDTO dto = new CommentDTO();
                dto.setContent("回复内容");
                dto.setArticleId(100L);
                dto.setParentId(10L);
                dto.setIsCommented(1);

                CommentVO result = commentService.publishComment(dto);

                assertNotNull(result);
                assertEquals("authorUser", result.getReplyName());

                ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
                verify(commentMapper).insert(captor.capture());
                assertEquals(10L, captor.getValue().getRootId());
                assertEquals(10L, captor.getValue().getParentId());
                verify(commentMapper).updateReplyCount(10L, 1);
            }
        }

        @Test
        @DisplayName("回复子评论：root_id 继承父评论的 root_id")
        void shouldReplyToChildComment() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                User user = new User();
                user.setId(1L);
                user.setUsername("testUser");
                user.setIcon("/icons/avatar.png");
                when(userMapper.selectById(1L)).thenReturn(user);

                Comment parent = new Comment();
                parent.setId(20L);
                parent.setRootId(10L);
                parent.setUsername("replierUser");
                when(commentMapper.selectById(20L)).thenReturn(parent);
                when(commentMapper.insert(any(Comment.class))).thenReturn(1);

                CommentDTO dto = new CommentDTO();
                dto.setContent("子回复");
                dto.setArticleId(100L);
                dto.setParentId(20L);
                dto.setIsCommented(1);

                CommentVO result = commentService.publishComment(dto);

                assertNotNull(result);
                assertEquals("replierUser", result.getReplyName());

                ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
                verify(commentMapper).insert(captor.capture());
                assertEquals(10L, captor.getValue().getRootId());
            }
        }

        @Test
        @DisplayName("父评论不存在时抛出异常")
        void shouldThrowExceptionWhenParentNotFound() {
            try (MockedStatic<SecurityContextHolder> securityMock = mockStatic(SecurityContextHolder.class)) {
                mockSecurityContext(securityMock, 1L);

                User user = new User();
                user.setId(1L);
                user.setUsername("testUser");
                user.setIcon("/icons/avatar.png");
                when(userMapper.selectById(1L)).thenReturn(user);
                when(commentMapper.selectById(999L)).thenReturn(null);

                CommentDTO dto = new CommentDTO();
                dto.setContent("回复评论");
                dto.setArticleId(100L);
                dto.setParentId(999L);
                dto.setIsCommented(1);

                assertThrows(RuntimeException.class, () -> commentService.publishComment(dto));
                verify(commentMapper, never()).insert(any());
            }
        }
    }

    @Nested
    @DisplayName("getCommentList 测试")
    class GetCommentListTests {

        @Test
        @DisplayName("评论列表为空时返回 null")
        void shouldReturnNullWhenNoComments() {
            when(commentMapper.selectAllComment(anyLong())).thenReturn(null);

            var result = commentService.getCommentList(100L);

            assertNull(result);
        }

        @Test
        @DisplayName("根评论无回复时返回空 replyList")
        void shouldReturnRootCommentWithoutReplies() {
            Comment root = buildComment(1L, null, "user1", "评论内容", 0L);

            when(commentMapper.selectAllComment(100L)).thenReturn(List.of(root));
            when(commentMapper.selectAllReply(1L)).thenReturn(Collections.emptyList());
            when(loveService.isEntityLovedByUser(anyLong(), anyLong())).thenReturn(false);
            when(loveService.getEntityLoveCount(anyLong(), anyLong())).thenReturn("5");

            var result = commentService.getCommentList(100L);

            assertNotNull(result);
            assertEquals(1, result.getList().size());
            CommentVO rootVO = result.getList().get(0);
            assertEquals("user1", rootVO.getUsername());
            assertEquals(0L, rootVO.getReplyCount());
            assertNull(rootVO.getReplyList());
        }

        @Test
        @DisplayName("根评论有回复时使用 selectBatchIds 批量查询父用户名（消除 N+1）")
        void shouldBatchQueryParentUsernames() {
            Comment root = buildComment(1L, null, "author", "根评论", 0L);

            Comment reply1 = buildComment(2L, 1L, "replyUser", "回复1", 1L);
            reply1.setParentId(1L);

            Comment reply2 = buildComment(3L, 1L, "replyUser2", "回复2", 1L);
            reply2.setParentId(1L);

            Comment reply3 = buildComment(4L, 2L, "nestedUser", "嵌套回复", 1L);
            reply3.setParentId(2L);

            when(commentMapper.selectAllComment(100L)).thenReturn(List.of(root));
            when(commentMapper.selectAllReply(1L)).thenReturn(Arrays.asList(reply1, reply2, reply3));

            Comment parent1 = new Comment();
            parent1.setId(1L);
            parent1.setUsername("author");
            Comment parent2 = new Comment();
            parent2.setId(2L);
            parent2.setUsername("replyUser");
            when(commentMapper.selectBatchIds(argThat(ids -> ids.size() == 2 && ids.contains(1L) && ids.contains(2L))))
                    .thenReturn(Arrays.asList(parent1, parent2));

            when(loveService.isEntityLovedByUser(anyLong(), anyLong())).thenReturn(false);
            when(loveService.getEntityLoveCount(anyLong(), anyLong())).thenReturn("3");

            var result = commentService.getCommentList(100L);

            assertNotNull(result);
            CommentVO rootVO = result.getList().get(0);
            assertEquals(3, rootVO.getReplyCount());
            assertNotNull(rootVO.getReplyList());
            assertEquals(3, rootVO.getReplyList().size());

            assertEquals("author", rootVO.getReplyList().get(0).getReplyName());
            assertEquals("author", rootVO.getReplyList().get(1).getReplyName());
            assertEquals("replyUser", rootVO.getReplyList().get(2).getReplyName());

            verify(commentMapper, times(1)).selectBatchIds(anyList());
            verify(commentMapper, never()).selectById(anyLong());
        }

        @Test
        @DisplayName("多根评论场景")
        void shouldHandleMultipleRootComments() {
            Comment root1 = buildComment(1L, null, "user1", "评论1", 0L);
            Comment root2 = buildComment(2L, null, "user2", "评论2", 0L);

            Comment reply = buildComment(3L, 1L, "replyUser", "回复", 1L);
            reply.setParentId(1L);

            when(commentMapper.selectAllComment(100L)).thenReturn(Arrays.asList(root1, root2));
            when(commentMapper.selectAllReply(1L)).thenReturn(List.of(reply));
            when(commentMapper.selectAllReply(2L)).thenReturn(Collections.emptyList());

            Comment parent = new Comment();
            parent.setId(1L);
            parent.setUsername("user1");
            when(commentMapper.selectBatchIds(List.of(1L))).thenReturn(List.of(parent));

            when(loveService.isEntityLovedByUser(anyLong(), anyLong())).thenReturn(false);
            when(loveService.getEntityLoveCount(anyLong(), anyLong())).thenReturn("2");

            var result = commentService.getCommentList(100L);

            assertNotNull(result);
            assertEquals(2, result.getList().size());
            assertEquals(1, result.getList().get(0).getReplyCount());
            assertEquals(0, result.getList().get(1).getReplyCount());
        }

        @Test
        @DisplayName("点赞状态正确传递到 VO")
        void shouldSetLoveStatusCorrectly() {
            Comment root = buildComment(1L, null, "user1", "评论", 0L);

            when(commentMapper.selectAllComment(100L)).thenReturn(List.of(root));
            when(commentMapper.selectAllReply(1L)).thenReturn(Collections.emptyList());
            when(loveService.isEntityLovedByUser(1L, LoveServiceImpl.LOVE_TYPE_COMMENT)).thenReturn(true);
            when(loveService.getEntityLoveCount(1L, LoveServiceImpl.LOVE_TYPE_COMMENT)).thenReturn("10");

            var result = commentService.getCommentList(100L);

            CommentVO vo = result.getList().get(0);
            assertTrue(vo.getIsLove());
            assertEquals(10L, vo.getLove());
        }
    }

    private void mockSecurityContext(MockedStatic<SecurityContextHolder> securityMock, Long userId) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        com.example.onlyone.Entity.UserDetail userDetail = mock(com.example.onlyone.Entity.UserDetail.class);

        securityMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(userDetail);
        when(userDetail.getUserId()).thenReturn(userId);
    }

    private Comment buildComment(Long id, Long parentId, String username, String content, Long rootId) {
        Comment c = new Comment();
        c.setId(id);
        c.setUserId(100L);
        c.setArticleId(100L);
        c.setParentId(parentId);
        c.setRootId(rootId);
        c.setUsername(username);
        c.setIcon("/icons/default.png");
        c.setContent(content);
        c.setLove(0L);
        c.setReplyCount(0L);
        c.setStatus(3L);
        c.setCreateTime(LocalDateTime.now());
        return c;
    }
}
