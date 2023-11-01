package com.sspdev.hotelbooking.mapper;

import com.sspdev.hotelbooking.database.entity.RoomContent;
import com.sspdev.hotelbooking.dto.RoomContentReadDto;
import org.springframework.stereotype.Component;

@Component
public class RoomContentReadMapper implements Mapper<RoomContent, RoomContentReadDto> {

    @Override
    public RoomContentReadDto map(RoomContent roomContent) {
        return RoomContentReadDto.builder()
                .id(roomContent.getId())
                .roomId(roomContent.getRoom().getId())
                .link(roomContent.getLink())
                .type(roomContent.getType().name())
                .build();
    }
}