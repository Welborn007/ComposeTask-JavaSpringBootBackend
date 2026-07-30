package com.composetask.mobileapp.verification.repository;

import com.composetask.mobileapp.verification.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VerificationRepository extends JpaRepository<Verification, UUID> {

    List<Verification> findByVendorId(UUID vendorId);
}