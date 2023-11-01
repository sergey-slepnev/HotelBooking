package com.sspdev.hotelbooking.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class RoomContentReadDto extends ContentReadDto {

    Integer roomId;
}
