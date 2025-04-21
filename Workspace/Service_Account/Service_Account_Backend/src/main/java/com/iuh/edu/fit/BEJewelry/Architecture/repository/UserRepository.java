package com.iuh.edu.fit.BEJewelry.Architecture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    // Email related queries
    User findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findOptionalByEmail(String email);
    
    // Token related queries
    User findByRefreshTokenAndEmail(String token, String email);
    
    Optional<User> findByResetToken(String resetToken);
}
