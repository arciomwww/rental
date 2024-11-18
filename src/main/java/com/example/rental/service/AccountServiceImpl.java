package com.example.rental.service;

import com.example.rental.dto.AccountDto;
import com.example.rental.entity.Account;
import com.example.rental.entity.User;
import com.example.rental.repository.AccountRepository;
import com.example.rental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public AccountDto createAccount(AccountDto accountDto) {
        User user = userRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account = Account.builder()
                .balance(BigDecimal.valueOf(0.0))
                .user(user)
                .build();

        Account savedAccount = accountRepository.save(account);

        return AccountDto.builder()
                .id(savedAccount.getId())
                .balance(savedAccount.getBalance())
                .userId(savedAccount.getUser().getId())
                .build();
    }


    public AccountDto getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        return AccountDto.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .userId(account.getUser().getId())
                .build();
    }

    public AccountDto updateAccount(Long id, AccountDto accountDto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        User user = userRepository.findById(accountDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        account.setBalance(accountDto.getBalance());
        account.setUser(user);

        Account updatedAccount = accountRepository.save(account);

        return AccountDto.builder()
                .id(updatedAccount.getId())
                .balance(updatedAccount.getBalance())
                .userId(updatedAccount.getUser().getId())
                .build();
    }

    public Page<AccountDto> getAllAccounts(Pageable pageable) {
        Page<Account> accounts = accountRepository.findAll(pageable);
        return accounts.map(account -> AccountDto.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .userId(account.getUser().getId())
                .build());
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}
