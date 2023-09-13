package com.sspdev.hotelbooking.integration.repository;

import com.sspdev.hotelbooking.database.repository.ReviewRepository;
import com.sspdev.hotelbooking.dto.filter.ReviewFilter;
import com.sspdev.hotelbooking.integration.IntegrationTestBase;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class ReviewRepositoryIT extends IntegrationTestBase {

    private static final Integer NO_PREDICATE_FILTER_COLLECTION_SIZE = 20;
    private static final Integer PREDICATE_FILTER_COLLECTION_SIZE = 3;

    private final ReviewRepository reviewRepository;

    @ParameterizedTest
    @MethodSource("getDataForFindByFilterMethod")
    void checkFindByFilter(ReviewFilter filter, Integer expectedCollectionSize, Integer... expectedRatings) {
        var actualReviews = reviewRepository.findAllByFilter(filter);

        var actualRatings = actualReviews.stream().map(review -> review.getRating().rating).toList();

        assertThat(actualRatings).hasSize(expectedCollectionSize);
        assertThat(actualRatings).contains(expectedRatings);
    }

    static Stream<Arguments> getDataForFindByFilterMethod() {
        return Stream.of(
//                no-predicate filter
                Arguments.of(ReviewFilter.builder().build(),
                        NO_PREDICATE_FILTER_COLLECTION_SIZE,
                        new Integer[]{5, 2, 4, 4, 3, 3, 5, 3, 5, 4, 4, 3, 5, 4, 3, 5, 4, 3, 3, 5}),
//                predicate filter
                Arguments.of(ReviewFilter.builder().hotelName("MinskPlaza").build(),
                        PREDICATE_FILTER_COLLECTION_SIZE,
                        new Integer[]{5, 2, 4})
        );
    }
}