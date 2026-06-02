package com.example.onlyone.Service;

import com.example.onlyone.Entity.*;
import com.example.onlyone.Mapper.*;
import com.example.onlyone.Service.ServiceImpl.UserDetailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailServiceImpl 单元测试")
class UserDetailServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserAndRoleMapper userAndRoleMapper;
    @Mock
    private RoleAndPermissionMapper roleAndPermissionMapper;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private PermissionMapper permissionMapper;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Nested
    @DisplayName("loadUserByUsername 测试")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("用户不存在时抛出 UsernameNotFoundException")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userMapper.selectOne(any())).thenReturn(null);

            assertThrows(UsernameNotFoundException.class,
                    () -> userDetailService.loadUserByUsername("notExist"));
            verify(valueOperations, never()).get(anyString());
        }

        @Test
        @DisplayName("缓存命中时直接返回缓存中的权限，不查 DB")
        void shouldReturnFromCacheWhenHit() {
            User user = buildUser(1L, "testUser");
            when(userMapper.selectOne(any())).thenReturn(user);

            String cachedJson = "[\"ROLE_USER\",\"article:read\"]";
            when(valueOperations.get("user:authorities:1")).thenReturn(cachedJson);

            UserDetail result = userDetailService.loadUserByUsername("testUser");

            assertNotNull(result);
            assertEquals(1L, result.getUserId());
            assertEquals("testUser", result.getUsername());

            List<String> authorityNames = result.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            assertTrue(authorityNames.contains("ROLE_USER"));
            assertTrue(authorityNames.contains("article:read"));
            assertEquals(2, authorityNames.size());

            verify(valueOperations).get("user:authorities:1");
            verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
            verify(userAndRoleMapper, never()).queryRoles(anyLong());
        }

        @Test
        @DisplayName("缓存未命中时从 DB 加载全量 RBAC 并写入缓存")
        void shouldLoadFromDBAndWriteCacheOnMiss() {
            User user = buildUser(1L, "testUser");
            when(userMapper.selectOne(any())).thenReturn(user);
            when(valueOperations.get("user:authorities:1")).thenReturn(null);

            UserAndRole uar = new UserAndRole();
            uar.setRoleId(1L);
            when(userAndRoleMapper.queryRoles(1L)).thenReturn(List.of(uar));

            Role role = new Role();
            role.setId(1L);
            role.setRoleName("USER");
            when(roleMapper.selectAllRoles(List.of(1L))).thenReturn(List.of(role));

            RoleAndPermission rap = new RoleAndPermission();
            rap.setPermissionId(1L);
            when(roleAndPermissionMapper.queryPermission(1L)).thenReturn(List.of(rap));

            Permission perm = new Permission();
            perm.setId(1L);
            perm.setName("article:read");
            when(permissionMapper.selectAllPermission(List.of(1L))).thenReturn(List.of(perm));

            UserDetail result = userDetailService.loadUserByUsername("testUser");

            assertNotNull(result);
            List<String> authorityNames = result.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            assertTrue(authorityNames.contains("ROLE_USER"));
            assertTrue(authorityNames.contains("article:read"));

            verify(valueOperations).set(eq("user:authorities:1"), contains("ROLE_USER"),
                    eq(30L), eq(TimeUnit.MINUTES));
            verify(userAndRoleMapper).queryRoles(1L);
            verify(roleMapper).selectAllRoles(List.of(1L));
            verify(roleAndPermissionMapper).queryPermission(1L);
            verify(permissionMapper).selectAllPermission(List.of(1L));
        }

        @Test
        @DisplayName("用户无角色时返回空权限列表并缓存空数组")
        void shouldReturnEmptyAuthoritiesWhenNoRoles() {
            User user = buildUser(1L, "testUser");
            when(userMapper.selectOne(any())).thenReturn(user);
            when(valueOperations.get("user:authorities:1")).thenReturn(null);
            when(userAndRoleMapper.queryRoles(1L)).thenReturn(Collections.emptyList());

            UserDetail result = userDetailService.loadUserByUsername("testUser");

            assertNotNull(result);
            assertTrue(result.getAuthorities().isEmpty());

            verify(valueOperations).set(eq("user:authorities:1"), eq("[]"),
                    eq(30L), eq(TimeUnit.MINUTES));
            verify(roleAndPermissionMapper, never()).queryPermission(anyLong());
            verify(permissionMapper, never()).selectAllPermission(anyList());
        }

        @Test
        @DisplayName("角色无权限时只缓存角色名")
        void shouldReturnOnlyRoleWhenNoPermissions() {
            User user = buildUser(1L, "testUser");
            when(userMapper.selectOne(any())).thenReturn(user);
            when(valueOperations.get("user:authorities:1")).thenReturn(null);

            UserAndRole uar = new UserAndRole();
            uar.setRoleId(1L);
            when(userAndRoleMapper.queryRoles(1L)).thenReturn(List.of(uar));

            Role role = new Role();
            role.setId(1L);
            role.setRoleName("ADMIN");
            when(roleMapper.selectAllRoles(List.of(1L))).thenReturn(List.of(role));
            when(roleAndPermissionMapper.queryPermission(1L)).thenReturn(Collections.emptyList());

            UserDetail result = userDetailService.loadUserByUsername("testUser");

            List<String> authorityNames = result.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            assertTrue(authorityNames.contains("ROLE_ADMIN"));
            assertEquals(1, authorityNames.size());

            verify(permissionMapper, never()).selectAllPermission(anyList());
        }

        @Test
        @DisplayName("用户拥有多个角色时全部加载")
        void shouldLoadMultipleRoles() {
            User user = buildUser(1L, "testUser");
            when(userMapper.selectOne(any())).thenReturn(user);
            when(valueOperations.get("user:authorities:1")).thenReturn(null);

            UserAndRole uar1 = new UserAndRole();
            uar1.setRoleId(1L);
            UserAndRole uar2 = new UserAndRole();
            uar2.setRoleId(2L);
            when(userAndRoleMapper.queryRoles(1L)).thenReturn(Arrays.asList(uar1, uar2));

            Role role1 = new Role();
            role1.setId(1L);
            role1.setRoleName("USER");
            Role role2 = new Role();
            role2.setId(2L);
            role2.setRoleName("ADMIN");
            when(roleMapper.selectAllRoles(argThat(ids -> ids.size() == 2)))
                    .thenReturn(Arrays.asList(role1, role2));

            when(roleAndPermissionMapper.queryPermission(1L)).thenReturn(Collections.emptyList());
            when(roleAndPermissionMapper.queryPermission(2L)).thenReturn(Collections.emptyList());

            UserDetail result = userDetailService.loadUserByUsername("testUser");

            List<String> authorityNames = result.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            assertTrue(authorityNames.contains("ROLE_USER"));
            assertTrue(authorityNames.contains("ROLE_ADMIN"));
            assertEquals(2, authorityNames.size());
        }

        @Test
        @DisplayName("角色名包含 ROLE_ 前缀时不重复添加")
        void shouldNotDuplicateRolePrefix() {
            User user = buildUser(1L, "testUser");
            when(userMapper.selectOne(any())).thenReturn(user);
            when(valueOperations.get("user:authorities:1")).thenReturn(null);

            UserAndRole uar = new UserAndRole();
            uar.setRoleId(1L);
            when(userAndRoleMapper.queryRoles(1L)).thenReturn(List.of(uar));

            Role role = new Role();
            role.setId(1L);
            role.setRoleName("ROLE_USER");
            when(roleMapper.selectAllRoles(List.of(1L))).thenReturn(List.of(role));
            when(roleAndPermissionMapper.queryPermission(1L)).thenReturn(Collections.emptyList());

            UserDetail result = userDetailService.loadUserByUsername("testUser");

            List<String> authorityNames = result.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            assertEquals(1, authorityNames.size());
            assertTrue(authorityNames.contains("ROLE_USER"));
        }
    }

    @Nested
    @DisplayName("evictAuthoritiesCache 测试")
    class EvictAuthoritiesCacheTests {

        @Test
        @DisplayName("清除指定用户的权限缓存")
        void shouldDeleteCacheKey() {
            when(stringRedisTemplate.delete("user:authorities:1")).thenReturn(true);

            userDetailService.evictAuthoritiesCache(1L);

            verify(stringRedisTemplate).delete("user:authorities:1");
        }

        @Test
        @DisplayName("再次加载时从 DB 重建缓存")
        void shouldReloadFromDBAfterEviction() {
            when(stringRedisTemplate.delete("user:authorities:1")).thenReturn(true);
            userDetailService.evictAuthoritiesCache(1L);

            User user = buildUser(1L, "testUser");
            when(userMapper.selectOne(any())).thenReturn(user);
            when(valueOperations.get("user:authorities:1")).thenReturn(null);

            UserAndRole uar = new UserAndRole();
            uar.setRoleId(1L);
            when(userAndRoleMapper.queryRoles(1L)).thenReturn(List.of(uar));

            Role role = new Role();
            role.setId(1L);
            role.setRoleName("USER");
            when(roleMapper.selectAllRoles(List.of(1L))).thenReturn(List.of(role));
            when(roleAndPermissionMapper.queryPermission(1L)).thenReturn(Collections.emptyList());

            UserDetail result = userDetailService.loadUserByUsername("testUser");

            assertNotNull(result);
            verify(valueOperations).set(eq("user:authorities:1"), anyString(),
                    eq(30L), eq(TimeUnit.MINUTES));
        }
    }

    private User buildUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("encodedPassword");
        return user;
    }
}
