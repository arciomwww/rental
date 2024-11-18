package com.example.rental.service;

import com.example.rental.dto.ApplicationAccountDto;
import com.example.rental.entity.ApplicationAccount;
import com.example.rental.repository.ApplicationAccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApplicationAccountServiceImplImplTest {

    private AutoCloseable closeable;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationAccountServiceImplImplTest.class);

    @Mock
    private ApplicationAccountRepository applicationAccountRepository;

    @InjectMocks
    private ApplicationAccountServiceImpl applicationAccountService;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        logger.info("Тестирование начато - выполнение метода setup()");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        logger.info("Тестирование завершено - выполнение метода tearDown()");
    }

    @Test
    void createApplicationAccount_ShouldCreateNewApplicationAccount() {
        logger.info("Тест createApplicationAccount_ShouldCreateNewApplicationAccount начат");

        ApplicationAccountDto applicationAccountDto = ApplicationAccountDto.builder()
                .balance(BigDecimal.TEN)
                .build();
        logger.debug("Создан DTO для нового аккаунта: {}", applicationAccountDto);

        ApplicationAccount applicationAccount = ApplicationAccount.builder()
                .id(1L)
                .balance(BigDecimal.TEN)
                .build();
        logger.debug("Создан объект ApplicationAccount: {}", applicationAccount);

        when(applicationAccountRepository.save(any(ApplicationAccount.class))).thenReturn(applicationAccount);
        logger.debug("Настроен мок для метода save()");

        ApplicationAccountDto result = applicationAccountService.createApplicationAccount(applicationAccountDto);
        logger.debug("Вызван метод createApplicationAccount()");

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result.getBalance());
        logger.debug("Проверка: ожидаемый баланс {} равен полученному балансу {}", BigDecimal.TEN, result.getBalance());

        verify(applicationAccountRepository, times(1)).save(any(ApplicationAccount.class));
        logger.info("Тест createApplicationAccount_ShouldCreateNewApplicationAccount успешно завершен");
    }

    @Test
    void getApplicationAccount_ShouldReturnApplicationAccount_WhenAccountExists() {
        logger.info("Тест getApplicationAccount_ShouldReturnApplicationAccount_WhenAccountExists начат");

        Long accountId = 1L;
        ApplicationAccount applicationAccount = ApplicationAccount.builder()
                .id(accountId)
                .balance(BigDecimal.TEN)
                .build();
        logger.debug("Создан объект ApplicationAccount для поиска: {}", applicationAccount);

        when(applicationAccountRepository.findById(accountId)).thenReturn(Optional.of(applicationAccount));
        logger.debug("Настроен мок для метода findById()");

        ApplicationAccountDto result = applicationAccountService.getApplicationAccount(accountId);
        logger.debug("Вызван метод getApplicationAccount()");

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals(BigDecimal.TEN, result.getBalance());
        logger.debug("Проверка: идентификатор аккаунта {}, баланс {}", accountId, result.getBalance());

        logger.info("Тест getApplicationAccount_ShouldReturnApplicationAccount_WhenAccountExists успешно завершен");
    }

    @Test
    void getApplicationAccount_ShouldThrowException_WhenAccountNotFound() {
        logger.info("Тест getApplicationAccount_ShouldThrowException_WhenAccountNotFound начат");

        Long accountId = 1L;
        when(applicationAccountRepository.findById(accountId)).thenReturn(Optional.empty());
        logger.debug("Настроен мок для метода findById() на возврат пустого значения");

        assertThrows(IllegalArgumentException.class, () -> applicationAccountService.getApplicationAccount(accountId));
        logger.debug("Проверка исключения при вызове getApplicationAccount() для несуществующего аккаунта");

        logger.info("Тест getApplicationAccount_ShouldThrowException_WhenAccountNotFound успешно завершен");
    }

    @Test
    void updateApplicationAccount_ShouldUpdateApplicationAccount_WhenAccountExists() {
        logger.info("Тест updateApplicationAccount_ShouldUpdateApplicationAccount_WhenAccountExists начат");

        Long accountId = 1L;
        ApplicationAccount existingAccount = ApplicationAccount.builder()
                .id(accountId)
                .balance(BigDecimal.ZERO)
                .build();
        logger.debug("Создан существующий аккаунт для обновления: {}", existingAccount);

        ApplicationAccountDto applicationAccountDto = ApplicationAccountDto.builder()
                .balance(BigDecimal.TEN)
                .build();
        logger.debug("Создан DTO для обновления аккаунта: {}", applicationAccountDto);

        when(applicationAccountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(applicationAccountRepository.save(any(ApplicationAccount.class))).thenReturn(existingAccount);
        logger.debug("Настроены моки для методов findById() и save()");

        ApplicationAccountDto result = applicationAccountService.updateApplicationAccount(accountId, applicationAccountDto);
        logger.debug("Вызван метод updateApplicationAccount()");

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        assertEquals(BigDecimal.TEN, result.getBalance());
        logger.debug("Проверка: идентификатор аккаунта {}, новый баланс {}", accountId, result.getBalance());

        verify(applicationAccountRepository, times(1)).save(existingAccount);
        logger.info("Тест updateApplicationAccount_ShouldUpdateApplicationAccount_WhenAccountExists успешно завершен");
    }

    @Test
    void updateApplicationAccount_ShouldThrowException_WhenAccountNotFound() {
        logger.info("Тест updateApplicationAccount_ShouldThrowException_WhenAccountNotFound начат");

        Long accountId = 1L;
        ApplicationAccountDto applicationAccountDto = ApplicationAccountDto.builder()
                .balance(BigDecimal.TEN)
                .build();
        logger.debug("Создан DTO для обновления аккаунта: {}", applicationAccountDto);

        when(applicationAccountRepository.findById(accountId)).thenReturn(Optional.empty());
        logger.debug("Настроен мок для метода findById() на возврат пустого значения");

        assertThrows(IllegalArgumentException.class, () -> applicationAccountService.updateApplicationAccount(accountId, applicationAccountDto));
        logger.debug("Проверка исключения при вызове updateApplicationAccount() для несуществующего аккаунта");

        logger.info("Тест updateApplicationAccount_ShouldThrowException_WhenAccountNotFound успешно завершен");
    }

    @Test
    void deleteApplicationAccount_ShouldDeleteApplicationAccount_WhenAccountExists() {
        logger.info("Тест deleteApplicationAccount_ShouldDeleteApplicationAccount_WhenAccountExists начат");

        Long accountId = 1L;
        doNothing().when(applicationAccountRepository).deleteById(accountId);
        logger.debug("Настроен мок для метода deleteById()");

        applicationAccountService.deleteApplicationAccount(accountId);
        logger.debug("Вызван метод deleteApplicationAccount()");

        verify(applicationAccountRepository, times(1)).deleteById(accountId);
        logger.info("Тест deleteApplicationAccount_ShouldDeleteApplicationAccount_WhenAccountExists успешно завершен");
    }
}
