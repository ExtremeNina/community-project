package com.example.onlyone.Utils;

public class RedisKeyUtils {

    private static final String SIM_PREFIX = "sim:note:";
    private static final String HOT_NOTES = "hot:notes";
    private static final String CANDIDATE_PREFIX = "rec:candidate:";
    private static final String DISPLAYED_PREFIX = "rec:displayed:";
    private static final String REFILL_LOCK_PREFIX = "rec:lock:refill:";

    public static String simKey(Long noteId) {
        return SIM_PREFIX + noteId;
    }

    public static String hotNotesKey() {
        return HOT_NOTES;
    }

    public static String candidateKey(Long userId) {
        return CANDIDATE_PREFIX + userId;
    }

    public static String displayedKey(Long userId) {
        return DISPLAYED_PREFIX + userId;
    }

    public static String refillLockKey(Long userId) {
        return REFILL_LOCK_PREFIX + userId;
    }

}