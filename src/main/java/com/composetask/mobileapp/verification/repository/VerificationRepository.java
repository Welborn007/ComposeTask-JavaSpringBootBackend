package com.composetask.mobileapp.verification.repository;

import com.composetask.mobileapp.verification.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationRepository extends JpaRepository<Verification, Long> {

    List<Verification> findByVendorId(Long vendorId);
}
