package com.example.rental.service;

import com.example.rental.dto.TransactionDto;
import com.example.rental.entity.Account;
import com.example.rental.entity.ApplicationAccount;
import com.example.rental.entity.Transaction;
import com.example.rental.repository.AccountRepository;
import com.example.rental.repository.ApplicationAccountRepository;
import com.example.rental.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;


@Service
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final ApplicationAccountRepository applicationAccountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(AccountRepository accountRepository,
                                  ApplicationAccountRepository applicationAccountRepository,
                                  TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.applicationAccountRepository = applicationAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transferFunds(TransactionDto transactionDto) {

        Account userAccount = accountRepository.findByUserId(transactionDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User account not found"));

        ApplicationAccount appAccount = applicationAccountRepository.findById(transactionDto.getApplicationAccountId())
                .orElseThrow(() -> new RuntimeException("Application account not found"));

        if (userAccount.getBalance().compareTo(transactionDto.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        userAccount.setBalance(userAccount.getBalance().subtract(transactionDto.getAmount()));
        appAccount.setBalance(appAccount.getBalance().add(transactionDto.getAmount()));

        accountRepository.save(userAccount);
        applicationAccountRepository.save(appAccount);


        Transaction transaction = Transaction.builder()
                .user(userAccount.getUser())
                .amount(transactionDto.getAmount())
                .type("TRANSFER")
                .createdAt(LocalDateTime.now())
                .description(transactionDto.getDescription())
                .build();

        transactionRepository.save(transaction);
    }

    @Transactional
    public void depositToAccount(TransactionDto transactionDto) {

        Account userAccount = accountRepository.findByUserId(transactionDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User account not found"));

        userAccount.setBalance(userAccount.getBalance().add(transactionDto.getAmount()));

        accountRepository.save(userAccount);

        Transaction transaction = Transaction.builder()
                .user(userAccount.getUser())
                .amount(transactionDto.getAmount())
                .type("DEPOSIT")
                .createdAt(LocalDateTime.now())
                .description(transactionDto.getDescription())
                .build();

        transactionRepository.save(transaction);
    }

    public Page<TransactionDto> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(this::convertToDto);
    }
    private TransactionDto convertToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .createdAt(transaction.getCreatedAt())
                .description(transaction.getDescription())
                .build();
    }
}

