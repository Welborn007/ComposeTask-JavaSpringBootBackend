package com.composetask.mobileapp.vendor.repository;

import com.composetask.mobileapp.vendor.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Placeholder repository for vendor persistence operations.
 */
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    Page<Vendor> findByUserId(Long userId, Pageable pageable);

}
