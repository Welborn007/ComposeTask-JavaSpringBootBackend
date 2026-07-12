package com.composetask.mobileapp.vendor.repository;

import com.composetask.mobileapp.vendor.model.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VendorRepositoryCustom {
    Page<Vendor> searchVendorsCustom(
            String search,
            String category,
            String location,
            Boolean verified,
            Double minRating,
            Double minTrustScore,
            Pageable pageable
    );
}
