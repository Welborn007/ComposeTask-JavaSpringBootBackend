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
import java.util.UUID;

/**
 * Repository for Review entity queries.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    /**
     * Find all reviews for a specific vendor (paginated).
     */
    @EntityGraph(value = "Review.withCustomerAndVendor")
    Page<Review> findByVendorId(UUID vendorId, Pageable pageable);

    /**
     * Find reviews written by a specific customer (paginated).
     */
    @EntityGraph(value = "Review.withCustomerAndVendor")
    Page<Review> findByCustomerId(UUID customerId, Pageable pageable);

    @EntityGraph(value = "Review.withCustomerAndVendor")
    List<Review> findByVendorId(UUID vendorId);
    
    @EntityGraph(value = "Review.withCustomerAndVendor")
    List<Review> findByCustomerId(UUID customerId);

    Optional<Review> findByVendorIdAndCustomerId(UUID vendorId, UUID customerId);

    /**
     * Get average rating for a vendor.qb
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vendor.id = :vendorId")
    Double getAverageRatingForVendor(@Param("vendorId") UUID vendorId);

    /**
     * Count reviews for a vendor.
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.vendor.id = :vendorId")
    Long countReviewsForVendor(@Param("vendorId") UUID vendorId);

    /**
     * Check if a customer has already reviewed a vendor (to prevent duplicates).
     * Returns count; service checks if result > 0.
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.vendor.id = :vendorId AND r.customer.id = :customerId")
    Long countReviewsByCustomerForVendor(@Param("vendorId") UUID vendorId, @Param("customerId") UUID customerId);
}