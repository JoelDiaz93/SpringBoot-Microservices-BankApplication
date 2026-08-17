package com.devsu.hackerearth.backend.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.devsu.hackerearth.backend.account.controller.AccountController;
import com.devsu.hackerearth.backend.account.model.dto.AccountDto;
import com.devsu.hackerearth.backend.account.model.dto.TransactionDto;
import com.devsu.hackerearth.backend.account.service.AccountService;
import com.devsu.hackerearth.backend.account.service.TransactionService;

@SpringBootTest
public class sampleTest {

	private AccountService accountService = mock(AccountService.class);
	private AccountController accountController = new AccountController(accountService);

	@Autowired
	private AccountService integrationAccountService;

	@Autowired
	private TransactionService integrationTransactionService;

	@Test
	void createAccountTest() {
		// Arrange
		AccountDto newAccount = new AccountDto(1L, "number", "savings", 0.0, true, 1L);
		AccountDto createdAccount = new AccountDto(1L, "number", "savings", 0.0, true, 1L);
		when(accountService.create(newAccount)).thenReturn(createdAccount);

		// Act
		ResponseEntity<AccountDto> response = accountController.create(newAccount);

		// Assert
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(createdAccount, response.getBody());
	}

	@Test
	void createTransactionIntegrationTest() {
		AccountDto account = integrationAccountService.create(
				new AccountDto(
						null,
						"ACC001",
						"SAVING",
						1000.0,
						true,
						1L));

		TransactionDto transaction = integrationTransactionService.create(

				new TransactionDto(
						null,
						new Date(),
						"DEPOSIT",
						500.0,
						0.0,
						account.getId()));

		assertNotNull(transaction.getId());
		assertEquals(1500.0, transaction.getBalance(), 0.001);
	}
}
