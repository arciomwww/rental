package com.example.rental.service;

import com.example.rental.dto.AccountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    AccountDto createAccount(AccountDto accountDto);
    AccountDto getAccount(Long id);
    AccountDto updateAccount(Long id, AccountDto accountDto);
    void deleteAccount(Long id);

    Page<AccountDto> getAllAccounts(Pageable pageable);
}
