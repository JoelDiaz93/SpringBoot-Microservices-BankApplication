package com.devsu.hackerearth.backend.account.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.dto.AccountDto;
import com.devsu.hackerearth.backend.account.model.dto.PartialAccountDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<AccountDto> getAll() {
        return accountRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto getById(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElse(null);

        if (account == null) {
            return null;
        }
        return toDto(account);
    }

    @Override
    public AccountDto create(AccountDto accountDto) {
        Account account = toEntity(accountDto);

        account.setId(null);

        return toDto(accountRepository.save(account));
    }

    @Override
    public AccountDto update(AccountDto accountDto) {
        if (accountDto == null || accountDto.getId() == null) {
            return null;
        }

        Account account = accountRepository
                .findById(accountDto.getId())
                .orElse(null);

        if (account == null) {
            return null;
        }

        account.setNumber(accountDto.getNumber());
        account.setType(accountDto.getType());
        account.setInitialAmount(accountDto.getInitialAmount());
        account.setActive(accountDto.isActive());
        account.setClientId(accountDto.getClientId());

        accountRepository.save(account);

        return toDto(account);
    }

    @Override
    public AccountDto partialUpdate(Long id, PartialAccountDto partialAccountDto) {
        Account account = findById(id);

        account.setActive(partialAccountDto.isActive());

        return toDto(accountRepository.save(account));
    }

    @Override
    public void deleteById(Long id) {
        Account account = findById(id);
        accountRepository.delete(account);
    }

    private Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
    }

    private AccountDto toDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getNumber(),
                account.getType(),
                account.getInitialAmount(),
                account.isActive(),
                account.getClientId());
    }

    private Account toEntity(AccountDto accountDto) {
        Account account = new Account();

        account.setId(accountDto.getId());
        account.setNumber(accountDto.getNumber());
        account.setType(accountDto.getType());
        account.setInitialAmount(accountDto.getInitialAmount());
        account.setActive(accountDto.isActive());
        account.setClientId(accountDto.getClientId());

        return account;
    }
}
