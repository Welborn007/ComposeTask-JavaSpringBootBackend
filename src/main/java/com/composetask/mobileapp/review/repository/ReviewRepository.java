package com.composetask.mobileapp.review.repository;

import com.composetask.mobileapp.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Review entity queries.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Find all reviews for a specific vendor (paginated).
     */
    @EntityGraph(value = "Review.withCustomerAndVendor")
    Page<Review> findByVendorId(Long vendorId, Pageable pageable);

    /**
     * Find reviews written by a specific customer (paginated).
     */
    @EntityGraph(value = "Review.withCustomerAndVendor")
    Page<Review> findByCustomerId(Long customerId, Pageable pageable);

    @EntityGraph(value = "Review.withCustomerAndVendor")
    List<Review> findByVendorId(Long vendorId);
    
    @EntityGraph(value = "Review.withCustomerAndVendor")
    List<Review> findByCustomerId(Long customerId);

    Optional<Review> findByVendorIdAndCustomerId(Long vendorId, Long customerId);

    /**
     * Get average rating for a vendor.qb
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vendor.id = :vendorId")
    Double getAverageRatingForVendor(@Param("vendorId") Long vendorId);

    /**
     * Count reviews for a vendor.
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.vendor.id = :vendorId")
    Long countReviewsForVendor(@Param("vendorId") Long vendorId);

    /**
     * Check if a customer has already reviewed a vendor (to prevent duplicates).
     */
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.vendor.id = :vendorId AND r.customer.id = :customerId")
    boolean hasCustomerReviewedVendor(@Param("vendorId") Long vendorId, @Param("customerId") Long customerId);
}