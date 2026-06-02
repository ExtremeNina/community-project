package com.example.onlyone.Service;


import com.example.onlyone.DTO.LoveDTO;

public interface LoveService {


    void toggleLike(LoveDTO loveDTO);

    Boolean isEntityLovedByUser(Long entityId, Long loveTypeId);

    String getEntityLoveCount(Long entityId, Long loveTypeId);
}
