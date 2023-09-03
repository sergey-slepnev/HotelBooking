package com.sspdev.hotelbooking.converter;

import com.sspdev.hotelbooking.database.entity.enums.Rating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, Integer> {

    private static final Integer DIFFERENCE = 1;
    private static final Integer DEFAULT_RATING = 5;

    @Override
    public Integer convertToDatabaseColumn(Rating attribute) {
        return attribute != null
                ? attribute.ordinal() + DIFFERENCE
                : DEFAULT_RATING;
    }

    @Override
    public Rating convertToEntityAttribute(Integer dbData) {
        return switch (dbData) {
            case 1 -> Rating.ONE;
            case 2 -> Rating.TWO;
            case 3 -> Rating.THREE;
            case 4 -> Rating.FOUR;
            case 5 -> Rating.FIVE;
            default -> null;
        };
    }
}