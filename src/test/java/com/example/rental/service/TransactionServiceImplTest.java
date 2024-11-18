package com.example.rental.service;

import com.example.rental.dto.TransactionDto;
import com.example.rental.entity.Account;
import com.example.rental.entity.ApplicationAccount;
import com.example.rental.entity.Transaction;
import com.example.rental.repository.AccountRepository;
import com.example.rental.repository.ApplicationAccountRepository;
import com.example.rental.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImplTest.class);

    private AutoCloseable mocks;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ApplicationAccountRepository applicationAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account userAccount;
    private ApplicationAccount appAccount;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        // Инициализация тестовых данных
        userAccount = new Account();
        userAccount.setBalance(BigDecimal.valueOf(1000));

        appAccount = new ApplicationAccount();
        appAccount.setBalance(BigDecimal.valueOf(500));

        logger.info("Запущен тест: Инициализация данных для тестов");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
        logger.info("Запущен тест: Освобождение ресурсов");
    }

    @Test
    void transferFunds_Success() {
        logger.info("Запущен тест: transferFunds_Success");

        TransactionDto transactionDto = TransactionDto.builder()
                .userId(1L)
                .applicationAccountId(1L)
                .amount(BigDecimal.valueOf(200))
                .type("TRANSFER")
                .description("Оплата аренды")
                .build();

        when(accountRepository.findByUserId(transactionDto.getUserId())).thenReturn(Optional.of(userAccount));
        when(applicationAccountRepository.findById(transactionDto.getApplicationAccountId())).thenReturn(Optional.of(appAccount));

        logger.info("Тест: Перевод средств с ID пользователя {} на ID приложения {}", transactionDto.getUserId(), transactionDto.getApplicationAccountId());
        transactionService.transferFunds(transactionDto);

        logger.info("Тест: Баланс пользователя после перевода: {}", userAccount.getBalance());
        logger.info("Тест: Баланс приложения после перевода: {}", appAccount.getBalance());

        assertEquals(BigDecimal.valueOf(800), userAccount.getBalance());
        assertEquals(BigDecimal.valueOf(700), appAccount.getBalance());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        Transaction transaction = transactionCaptor.getValue();
        assertEquals(transactionDto.getType(), transaction.getType());
        assertEquals(transactionDto.getAmount(), transaction.getAmount());
        assertEquals(transactionDto.getDescription(), transaction.getDescription());
        assertEquals(userAccount.getUser(), transaction.getUser());
        assertEquals(LocalDateTime.now().getMinute(), transaction.getCreatedAt().getMinute(), 1); // допускаем разницу в одну минуту
        logger.info("Тест: Перевод средств успешно завершен");
    }

    @Test
    void transferFunds_InsufficientFunds() {
        logger.info("Запущен тест: transferFunds_InsufficientFunds");

        TransactionDto transactionDto = TransactionDto.builder()
                .userId(1L)
                .applicationAccountId(1L)
                .amount(BigDecimal.valueOf(1200))
                .type("TRANSFER")
                .description("Оплата аренды")
                .build();

        when(accountRepository.findByUserId(transactionDto.getUserId())).thenReturn(Optional.of(userAccount));
        when(applicationAccountRepository.findById(transactionDto.getApplicationAccountId())).thenReturn(Optional.of(appAccount));

        logger.info("Тест: Перевод средств с ID пользователя {} на ID приложения {}", transactionDto.getUserId(), transactionDto.getApplicationAccountId());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionService.transferFunds(transactionDto));
        assertEquals("Insufficient funds", exception.getMessage());
        logger.info("Тест: Ожидание исключения: {}", exception.getMessage());
    }

    @Test
    void transferFunds_UserAccountNotFound() {
        logger.info("Запущен тест: transferFunds_UserAccountNotFound");

        TransactionDto transactionDto = TransactionDto.builder()
                .userId(1L)
                .applicationAccountId(1L)
                .amount(BigDecimal.valueOf(200))
                .type("TRANSFER")
                .description("Оплата аренды")
                .build();

        when(accountRepository.findByUserId(transactionDto.getUserId())).thenReturn(Optional.empty());

        logger.info("Тест: Проверка отсутствия учетной записи пользователя");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionService.transferFunds(transactionDto));
        assertEquals("User account not found", exception.getMessage());
        logger.info("Тест: Ожидание исключения: {}", exception.getMessage());
    }

    @Test
    void transferFunds_ApplicationAccountNotFound() {
        logger.info("Запущен тест: transferFunds_ApplicationAccountNotFound");

        TransactionDto transactionDto = TransactionDto.builder()
                .userId(1L)
                .applicationAccountId(1L)
                .amount(BigDecimal.valueOf(200))
                .type("TRANSFER")
                .description("Оплата аренды")
                .build();

        when(accountRepository.findByUserId(transactionDto.getUserId())).thenReturn(Optional.of(userAccount));
        when(applicationAccountRepository.findById(transactionDto.getApplicationAccountId())).thenReturn(Optional.empty());

        logger.info("Тест: Проверка отсутствия учетной записи приложения");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionService.transferFunds(transactionDto));
        assertEquals("Application account not found", exception.getMessage());
        logger.info("Тест: Ожидание исключения: {}", exception.getMessage());
    }

    @Test
    void depositToAccount_Success() {
        logger.info("Запущен тест: depositToAccount_Success");

        TransactionDto transactionDto = TransactionDto.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(300))
                .type("DEPOSIT")
                .description("Пополнение средств")
                .build();

        when(accountRepository.findByUserId(transactionDto.getUserId())).thenReturn(Optional.of(userAccount));

        logger.info("Тест: Пополнение средств для пользователя с ID {}", transactionDto.getUserId());
        transactionService.depositToAccount(transactionDto);

        logger.info("Тест: Баланс пользователя после пополнения: {}", userAccount.getBalance());

        assertEquals(BigDecimal.valueOf(1300), userAccount.getBalance());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(transactionCaptor.capture());

        Transaction transaction = transactionCaptor.getValue();
        assertEquals(transactionDto.getType(), transaction.getType());
        assertEquals(transactionDto.getAmount(), transaction.getAmount());
        assertEquals(transactionDto.getDescription(), transaction.getDescription());
        assertEquals(userAccount.getUser(), transaction.getUser());
        assertEquals(LocalDateTime.now().getMinute(), transaction.getCreatedAt().getMinute(), 1); // допускаем разницу в одну минуту
        logger.info("Тест: Пополнение средств успешно завершено");
    }

    @Test
    void depositToAccount_UserAccountNotFound() {
        logger.info("Запущен тест: depositToAccount_UserAccountNotFound");

        TransactionDto transactionDto = TransactionDto.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(300))
                .type("DEPOSIT")
                .description("Пополнение средств")
                .build();

        when(accountRepository.findByUserId(transactionDto.getUserId())).thenReturn(Optional.empty());

        logger.info("Тест: Проверка отсутствия учетной записи пользователя");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionService.depositToAccount(transactionDto));
        assertEquals("User account not found", exception.getMessage());
        logger.info("Тест: Ожидание исключения: {}", exception.getMessage());
    }
}
