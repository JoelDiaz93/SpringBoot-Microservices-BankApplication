package com.devsu.hackerearth.backend.account.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsu.hackerearth.backend.account.exception.InsufficientBalanceException;
import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.Transaction;
import com.devsu.hackerearth.backend.account.model.dto.BankStatementDto;
import com.devsu.hackerearth.backend.account.model.dto.PartitialTransactionDto;
import com.devsu.hackerearth.backend.account.model.dto.TransactionDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;
import com.devsu.hackerearth.backend.account.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ClientApiService clientApiService;

    public TransactionServiceImpl(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            ClientApiService clientApiService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.clientApiService = clientApiService;
    }

    @Override
    public List<TransactionDto> getAll() {
        return transactionRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDto getById(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found with id: " + id);
        }

        return toDto(transaction);
    }

    @Override
    public TransactionDto create(TransactionDto transactionDto) {
        if (transactionDto.getAccountId() == null) {
            throw new IllegalArgumentException("Account id is required");
        }

        Account account = accountRepository
                .findById(transactionDto.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Account not found with id: " + transactionDto.getAccountId()));

        if (!account.isActive()) {
            throw new IllegalArgumentException("Account is inactive");
        }

        double currentBalance = transactionRepository
                .findTopByAccountIdOrderByIdDesc(account.getId())
                .map(Transaction::getBalance)
                .orElse(account.getInitialAmount());

        double newBalance = currentBalance + transactionDto.getAmount();

        if (newBalance < 0) {
            throw new InsufficientBalanceException("Saldo no disponible");
        }

        Transaction transaction = new Transaction();

        transaction.setId(null);

        transaction.setDate(
                transactionDto.getDate() != null
                        ? transactionDto.getDate()
                        : new Date());

        transaction.setAmount(transactionDto.getAmount());

        transaction.setBalance(newBalance);

        transaction.setAccountId(account.getId());

        if (transactionDto.getType() != null && !transactionDto.getType().trim().isEmpty()) {
            transaction.setType(transactionDto.getType());
        } else {
            transaction.setType(
                    transactionDto.getAmount() >= 0
                            ? "DEPOSIT"
                            : "WITHDRAWAL");
        }
        return toDto(
                transactionRepository.save(transaction));
    }

    @Override
    public List<BankStatementDto> getAllByAccountClientIdAndDateBetween(Long clientId, Date dateTransactionStart,
            Date dateTransactionEnd) {
        CompletableFuture<String> clientNameFuture = clientApiService.getClientName(clientId);

        List<Account> accounts = accountRepository.findByClientId(clientId);

        Date endOfDay = toEndOfDay(dateTransactionEnd);

        List<BankStatementDto> statement = new ArrayList<>();

        for (Account account : accounts) {
            List<Transaction> transactions = transactionRepository
                    .findByAccountIdAndDateBetween(account.getId(), dateTransactionStart, endOfDay);

            if (transactions.isEmpty()) {
                statement.add(
                        new BankStatementDto(
                                null,
                                null,
                                account.getNumber(),
                                account.getType(),
                                account.getInitialAmount(),
                                account.isActive(),
                                null,
                                0,
                                account.getInitialAmount()));
            }

            for (Transaction transaction : transactions) {
                statement.add(
                        new BankStatementDto(
                                transaction.getDate(),
                                null,
                                account.getNumber(),
                                account.getType(),
                                account.getInitialAmount(),
                                account.isActive(),
                                transaction.getType(),
                                transaction.getAmount(),
                                transaction.getBalance()));
            }
        }

        String clientName;

        try {
            clientName = clientNameFuture.join();
        } catch (Exception e) {
            clientName = String.valueOf(clientId);
        }

        for (BankStatementDto item : statement) {
            item.setClient(clientName);
        }

        return statement;
    }

    @Override
    public TransactionDto getLastByAccountId(Long accountId) {
        Transaction transaction = transactionRepository
                .findTopByAccountIdOrderByIdDesc(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No transaction found for account: " + accountId));

        return toDto(transaction);
    }

    @Override
    @Transactional
    public TransactionDto update(
            Long id,
            TransactionDto transactionDto) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found with id: " + id);
        }

        if (transactionDto.getAccountId() != null
                && !transactionDto.getAccountId().equals(transaction.getAccountId())) {

            throw new IllegalArgumentException("Changing transaction account is not allowed");
        }

        if (transactionDto.getDate() != null) {
            transaction.setDate(transactionDto.getDate());
        }

        if (transactionDto.getType() != null) {
            transaction.setType(transactionDto.getType());
        }

        transaction.setAmount(transactionDto.getAmount());

        Long accountId = transaction.getAccountId();

        transactionRepository.save(transaction);

        recalculateBalances(accountId);

        Transaction update = transactionRepository.findById(id).orElse(null);

        return toDto(update);
    }

    @Override
    @Transactional
    public TransactionDto partitialUpdate(Long id, PartitialTransactionDto partitialTransactionDto) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found with id: " + id);
        }

        if (partitialTransactionDto.getDate() != null) {
            transaction.setDate(partitialTransactionDto.getDate());
        }

        if (partitialTransactionDto.getType() != null) {
            transaction.setType(partitialTransactionDto.getType());
        }

        if (partitialTransactionDto.getAmount() != null) {
            transaction.setAmount(partitialTransactionDto.getAmount());
        }

        Long accountId = transaction.getAccountId();

        transactionRepository.save(transaction);

        recalculateBalances(accountId);

        return toDto(transactionRepository.findById(id).orElse(null));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Transaction transaction = transactionRepository.findById(id).orElse(null);

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found with id :" + id);
        }

        Long accountId = transaction.getAccountId();

        transactionRepository.delete(transaction);
        transactionRepository.flush();

        recalculateBalances(accountId);
    }

    private void recalculateBalances(Long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);

        if (account == null) {
            throw new IllegalArgumentException("Account not found with id: " + accountId);
        }

        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByDateAscIdAsc(accountId);

        double balance = account.getInitialAmount();

        for (Transaction transaction : transactions) {
            balance += transaction.getAmount();

            if (balance < 0) {
                throw new InsufficientBalanceException("Saldo no disponible");
            }

            transaction.setBalance(balance);
        }

        transactionRepository.saveAll(transactions);
    }

    private Date toEndOfDay(Date date) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 23);

        calendar.set(Calendar.MINUTE, 59);

        calendar.set(Calendar.SECOND, 59);

        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    private TransactionDto toDto(Transaction transactionDto) {
        return new TransactionDto(
                transactionDto.getId(),
                transactionDto.getDate(),
                transactionDto.getType(),
                transactionDto.getAmount(),
                transactionDto.getBalance(),
                transactionDto.getAccountId());
    }
}
