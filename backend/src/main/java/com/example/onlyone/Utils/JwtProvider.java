package com.example.onlyone.Utils;

import cn.hutool.core.util.IdUtil;
import com.example.onlyone.Entity.CustomOAuth2User;
import com.example.onlyone.Entity.UserDetail;
import com.example.onlyone.Properties.JwtProperties;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JwtProvider {

    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtProvider(JwtProperties jwtProperties, Key key) {
        this.jwtProperties = jwtProperties;
        this.key = key;
    }

    public String generateToken(Authentication authenticate) {
        return generateAccessToken(authenticate);
    }

    public String generateAccessToken(Authentication authenticate) {
        return generateAccessToken(authenticate, "unknown");
    }

    public String generateAccessToken(Authentication authenticate, String deviceId) {
        String username = authenticate.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessExpiration());

        Long userId = extractUserId(authenticate);
        String source = extractSource(authenticate);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("deviceId", deviceId)
                .claim("source", source)
                .claim("type", TOKEN_TYPE_ACCESS)
                .claim("jti", IdUtil.fastSimpleUUID())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authenticate) {
        return generateRefreshToken(authenticate, "unknown");
    }

    public String generateRefreshToken(Authentication authenticate, String deviceId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshExpiration());

        Long userId = extractUserId(authenticate);
        String source = extractSource(authenticate);

        return Jwts.builder()
                .claim("userId", userId)
                .claim("deviceId", deviceId)
                .claim("source", source)
                .claim("type", TOKEN_TYPE_REFRESH)
                .claim("jti", IdUtil.fastSimpleUUID())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getTokenType(String token) {
        try {
            return getClaimsFromToken(token).get("type", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getJtiFromToken(String token) {
        try {
            return getClaimsFromToken(token).get("jti", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getDeviceIdFromToken(String token) {
        try {
            return getClaimsFromToken(token).get("deviceId", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("Token 已过期: {}", ex.getMessage());
            return false;
        } catch (UnsupportedJwtException ex) {
            log.warn("不支持的 Token 格式: {}", ex.getMessage());
            return false;
        } catch (MalformedJwtException ex) {
            log.warn("Token 格式错误: {}", ex.getMessage());
            return false;
        } catch (SignatureException ex) {
            log.warn("Token 签名验证失败: {}", ex.getMessage());
            return false;
        } catch (IllegalArgumentException ex) {
            log.warn("Token 参数异常: {}", ex.getMessage());
            return false;
        } catch (Exception ex) {
            log.error("Token 验证发生未知错误: {}", ex.getMessage(), ex);
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException ex) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Object userIdObj = claims.get("userId");

            if (userIdObj == null) {
                log.warn("Token中没有包含userId: {}", token);
                return null;
            }

            if (userIdObj instanceof Number) {
                return ((Number) userIdObj).longValue();
            } else if (userIdObj instanceof String) {
                try {
                    return Long.parseLong((String) userIdObj);
                } catch (NumberFormatException e) {
                    log.warn("Token中的userId格式错误: {}", userIdObj);
                    return null;
                }
            } else {
                log.warn("Token中的userId类型不支持: {}", userIdObj.getClass());
                return null;
            }
        } catch (ExpiredJwtException ex) {
            log.warn("Token已过期，但尝试解析其中的userId");
            Object userIdObj = ex.getClaims().get("userId");
            return parseUserIdFromObject(userIdObj);
        } catch (Exception ex) {
            log.error("获取用户ID时发生错误: {}", ex.getMessage());
            return null;
        }
    }

    private Long parseUserIdFromObject(Object userIdObj) {
        if (userIdObj == null) {
            return null;
        }
        if (userIdObj instanceof Number) {
            return ((Number) userIdObj).longValue();
        } else if (userIdObj instanceof String) {
            try {
                return Long.parseLong((String) userIdObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public String getSourceFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("source", String.class);
        } catch (Exception e) {
            log.warn("从 JWT 获取用户来源失败: {}", e.getMessage());
            return "local";
        }
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    private Long extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetail) {
            return ((UserDetail) principal).getUserId();
        }
        CustomOAuth2User oAuth2User = (CustomOAuth2User) principal;
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if (attributes.containsKey("login")) {
            return ((Number) attributes.get("id")).longValue();
        }
        return null;
    }

    private String extractSource(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetail) {
            return "local";
        }
        return "github";
    }
}