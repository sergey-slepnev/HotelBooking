package com.sspdev.hotelbooking.database.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.sspdev.hotelbooking.database.entity.Review;
import com.sspdev.hotelbooking.dto.filter.ReviewFilter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sspdev.hotelbooking.database.entity.QReview.review;

@RequiredArgsConstructor
public class FilterReviewRepositoryImpl implements FilterReviewRepository {

    private final EntityManager entityManager;

    @Override
    public List<Review> findAllByFilter(ReviewFilter filter) {
        var predicate = com.sspdev.hotelrepository.database.querydsl.QPredicates.builder()
                .add(filter.hotelName(), review.hotel.name::eq)
                .add(filter.ratingFrom(), review.rating::goe)
                .add(filter.ratingTo(), review.rating::loe)
                .build();

        return new JPAQuery<Review>(entityManager)
                .select(review)
                .from(review)
                .where(predicate)
                .orderBy(review.rating.desc())
                .fetch();
    }
}