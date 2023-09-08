package com.sspdev.hotelbooking.database.repository;

import com.sspdev.hotelbooking.database.entity.Review;
import com.sspdev.hotelbooking.dto.filter.ReviewFilter;

import java.util.List;

public interface FilterReviewRepository {

    List<Review> findAllByFilter(ReviewFilter filter);
}
