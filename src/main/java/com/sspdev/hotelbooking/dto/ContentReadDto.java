package com.sspdev.hotelbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@SuperBuilder
@Data
public abstract class ContentReadDto {

    private Integer id;

    private String link;

    private String type;

}