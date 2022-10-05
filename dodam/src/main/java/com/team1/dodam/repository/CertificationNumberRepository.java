package com.team1.dodam.repository;

import com.team1.dodam.domain.CertificationNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificationNumberRepository extends JpaRepository<CertificationNumber, Long> {

    Optional<CertificationNumber> findByEmail(String email);
    void deleteByEmail(String email);
}
