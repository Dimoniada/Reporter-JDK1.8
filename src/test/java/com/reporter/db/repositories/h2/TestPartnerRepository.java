package com.reporter.db.repositories.h2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestPartnerRepository extends JpaRepository<TestPartner, Long> {
    @Query(
        nativeQuery = true,
        value = "select * from \"partners\""
    )
    List<TestPartner> findAllPartners();
}
