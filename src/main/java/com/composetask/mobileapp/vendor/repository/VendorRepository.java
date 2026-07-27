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
public interface VendorRepository extends JpaRepository<Vendor, Long>, VendorRepositoryCustom {

    Page<Vendor> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserId(Long userId);

}
