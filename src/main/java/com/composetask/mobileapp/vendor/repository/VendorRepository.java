package com.composetask.mobileapp.vendor.repository;

import com.composetask.mobileapp.vendor.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Placeholder repository for vendor persistence operations.
 */
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    @EntityGraph(attributePaths = "user")
    @Query("""
            SELECT v
            FROM Vendor v
            WHERE (COALESCE(:search, '') = ''
                OR LOWER(v.businessName) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(v.description) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(v.category) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(v.location) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (COALESCE(:category, '') = '' OR LOWER(v.category) = LOWER(:category))
              AND (COALESCE(:location, '') = '' OR LOWER(v.location) LIKE LOWER(CONCAT('%', :location, '%')))
              AND (:verified IS NULL OR v.verified = :verified)
              AND (:minRating IS NULL OR COALESCE((
                    SELECT AVG(r.rating)
                    FROM Review r
                    WHERE r.vendor = v
                  ), 0.0) >= :minRating)
              AND (:minTrustScore IS NULL OR (
                    ((COALESCE((
                        SELECT AVG(r.rating)
                        FROM Review r
                        WHERE r.vendor = v
                    ), 0.0) / 5.0) * 100.0 * 0.6)
                    + ((CASE WHEN v.verified = true THEN 100.0 ELSE 50.0 END) * 0.4)
                  ) >= :minTrustScore)
            """)
    Page<Vendor> searchVendors(
            @Param("search") String search,
            @Param("category") String category,
            @Param("location") String location,
            @Param("verified") Boolean verified,
            @Param("minRating") Double minRating,
            @Param("minTrustScore") Double minTrustScore,
            Pageable pageable
    );

    Page<Vendor> findByUserId(Long userId, Pageable pageable);

}
