package com.example.rental.service;

import com.example.rental.dto.TransactionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    void transferFunds(TransactionDto transactionDto);
    void depositToAccount(TransactionDto transactionDto);

    Page<TransactionDto> getAllTransactions(Pageable pageable);
}
