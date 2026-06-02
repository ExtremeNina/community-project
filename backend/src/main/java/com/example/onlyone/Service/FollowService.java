package com.example.onlyone.Service;

import java.util.List;

public interface FollowService {

    boolean toggleFollow(Long userId);

    Boolean isFollowing(Long currentUserId, Long userId);

    Long getFansCount(Long userId);

    Long getFollowingCount(Long userId);

    List<Long> getFanList(Long userId);
}
