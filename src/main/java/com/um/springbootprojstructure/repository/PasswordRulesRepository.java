package com.um.springbootprojstructure.repository;

import com.um.springbootprojstructure.entity.PasswordRules;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRulesRepository extends JpaRepository<PasswordRules, Long> {
}
