package com.example.onlyone.Service;

import java.util.Map;

public interface RecallService {

    Map<Long,Double> collaborativeRecall(Long userId);

    Map<Long,Double> hotRecall();

    Map<Long,Double> tagRecall(Long userId);

}
