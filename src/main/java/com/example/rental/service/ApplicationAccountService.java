package com.example.rental.service;

import com.example.rental.dto.ApplicationAccountDto;

public interface ApplicationAccountService {
    ApplicationAccountDto createApplicationAccount(ApplicationAccountDto applicationAccountDto);
    ApplicationAccountDto getApplicationAccount(Long id);
    ApplicationAccountDto updateApplicationAccount(Long id, ApplicationAccountDto applicationAccountDto);

    void deleteApplicationAccount(Long id);
}
