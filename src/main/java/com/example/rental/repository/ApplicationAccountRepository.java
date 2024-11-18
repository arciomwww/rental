package com.example.rental.repository;

import com.example.rental.entity.ApplicationAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationAccountRepository extends JpaRepository<ApplicationAccount, Long> {
}
