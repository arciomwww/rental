package com.example.rental.service;

import com.example.rental.dto.AccountDto;
import com.example.rental.entity.Account;
import com.example.rental.entity.User;
import com.example.rental.repository.AccountRepository;
import com.example.rental.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {
    private static final Logger logger = Logger.getLogger(AccountServiceImplTest.class.getName());
    private AutoCloseable mocks;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        logger.info("Инициализация теста");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
            logger.info("Закрытие моков после теста");
        }
    }

    @Test
    void createAccount_ShouldCreateNewAccount() {
        logger.info("Тест createAccount_ShouldCreateNewAccount: Начало теста");

        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        AccountDto accountDto = AccountDto.builder()
                .userId(userId)
                .build();

        Account account = Account.builder()
                .id(1L)
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        logger.info("Создание нового аккаунта для пользователя с ID: " + userId);
        AccountDto result = accountService.createAccount(accountDto);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(BigDecimal.ZERO, result.getBalance(), "Баланс должен быть 0");
        assertEquals(userId, result.getUserId(), "ID пользователя должен совпадать");

        verify(accountRepository, times(1)).save(any(Account.class));
        logger.info("Тест createAccount_ShouldCreateNewAccount: Успешно завершён");
    }

    @Test
    void getAccount_ShouldReturnAccount_WhenAccountExists() {
        logger.info("Тест getAccount_ShouldReturnAccount_WhenAccountExists: Начало теста");

        Long accountId = 1L;
        Account account = Account.builder()
                .id(accountId)
                .balance(BigDecimal.TEN)
                .user(new User())
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        logger.info("Получение аккаунта с ID: " + accountId);

        AccountDto result = accountService.getAccount(accountId);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(accountId, result.getId(), "ID аккаунта должен совпадать");
        assertEquals(BigDecimal.TEN, result.getBalance(), "Баланс должен быть 10");

        logger.info("Тест getAccount_ShouldReturnAccount_WhenAccountExists: Успешно завершён");
    }

    @Test
    void getAccount_ShouldThrowException_WhenAccountNotFound() {
        logger.info("Тест getAccount_ShouldThrowException_WhenAccountNotFound: Начало теста");

        Long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        logger.info("Проверка, что исключение выбрасывается, если аккаунт не найден");
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccount(accountId));

        logger.info("Тест getAccount_ShouldThrowException_WhenAccountNotFound: Успешно завершён");
    }

    @Test
    void updateAccount_ShouldUpdateAccount_WhenAccountExists() {
        logger.info("Тест updateAccount_ShouldUpdateAccount_WhenAccountExists: Начало теста");

        Long accountId = 1L;
        Long userId = 2L;
        Account existingAccount = Account.builder()
                .id(accountId)
                .balance(BigDecimal.ZERO)
                .user(new User())
                .build();

        User user = new User();
        user.setId(userId);

        AccountDto accountDto = AccountDto.builder()
                .balance(BigDecimal.TEN)
                .userId(userId)
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.save(any(Account.class))).thenReturn(existingAccount);

        logger.info("Обновление аккаунта с ID: " + accountId);
        AccountDto result = accountService.updateAccount(accountId, accountDto);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(accountId, result.getId(), "ID аккаунта должен совпадать");
        assertEquals(BigDecimal.TEN, result.getBalance(), "Баланс должен быть 10");
        assertEquals(userId, result.getUserId(), "ID пользователя должен совпадать");

        verify(accountRepository, times(1)).save(existingAccount);
        logger.info("Тест updateAccount_ShouldUpdateAccount_WhenAccountExists: Успешно завершён");
    }

    @Test
    void updateAccount_ShouldThrowException_WhenAccountNotFound() {
        logger.info("Тест updateAccount_ShouldThrowException_WhenAccountNotFound: Начало теста");

        Long accountId = 1L;
        AccountDto accountDto = AccountDto.builder()
                .balance(BigDecimal.TEN)
                .userId(1L)
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        logger.info("Проверка, что исключение выбрасывается, если аккаунт не найден");
        assertThrows(IllegalArgumentException.class, () -> accountService.updateAccount(accountId, accountDto));

        logger.info("Тест updateAccount_ShouldThrowException_WhenAccountNotFound: Успешно завершён");
    }

    @Test
    void getAllAccounts_ShouldReturnPagedAccounts() {
        logger.info("Тест getAllAccounts_ShouldReturnPagedAccounts: Начало теста");

        Pageable pageable = PageRequest.of(0, 10);
        Account account = Account.builder()
                .id(1L)
                .balance(BigDecimal.TEN)
                .user(new User())
                .build();
        Page<Account> accounts = new PageImpl<>(Collections.singletonList(account));

        when(accountRepository.findAll(pageable)).thenReturn(accounts);
        logger.info("Получение всех аккаунтов постранично");

        Page<AccountDto> result = accountService.getAllAccounts(pageable);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(1, result.getTotalElements(), "Количество элементов должно быть 1");
        assertEquals(BigDecimal.TEN, result.getContent().get(0).getBalance(), "Баланс должен быть 10");

        logger.info("Тест getAllAccounts_ShouldReturnPagedAccounts: Успешно завершён");
    }

    @Test
    void deleteAccount_ShouldDeleteAccount_WhenAccountExists() {
        logger.info("Тест deleteAccount_ShouldDeleteAccount_WhenAccountExists: Начало теста");

        Long accountId = 1L;
        doNothing().when(accountRepository).deleteById(accountId);

        logger.info("Удаление аккаунта с ID: " + accountId);
        accountService.deleteAccount(accountId);

        verify(accountRepository, times(1)).deleteById(accountId);
        logger.info("Тест deleteAccount_ShouldDeleteAccount_WhenAccountExists: Успешно завершён");
    }
}
