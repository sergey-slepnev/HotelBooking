package com.sspdev.hotelbooking.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class RoomContentReadDto extends ContentReadDto {

    private Integer roomId;

    public RoomContentReadDto(Integer id, String link, String type, Integer roomId) {
        super(id, link, type);
        this.roomId = roomId;
    }
}
