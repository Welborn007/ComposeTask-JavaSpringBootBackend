package com.thastmyshop.mobileapp.userapi.repository;

import com.thastmyshop.mobileapp.userapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
