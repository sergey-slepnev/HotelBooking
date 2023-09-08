package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ReviewRepository extends
        JpaRepository<Review, Long>,
        FilterReviewRepository,
        QuerydslPredicateExecutor<Review> {

}