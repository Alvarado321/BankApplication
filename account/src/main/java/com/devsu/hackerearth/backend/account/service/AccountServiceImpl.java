package com.devsu.hackerearth.backend.account.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.dto.AccountDto;
import com.devsu.hackerearth.backend.account.model.dto.PartialAccountDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;
import com.devsu.hackerearth.backend.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<AccountDto> getAll() {
        return accountRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto getById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id=" + id));
        return entityToDto(account);
    }

    @Override
    public AccountDto create(AccountDto accountDto) {
        Account entity = dtoToEntity(accountDto);
        Account saved = accountRepository.save(entity);
        return entityToDto(saved);
    }

    @Override
    public AccountDto update(AccountDto accountDto) {
        Account existing = accountRepository.findById(accountDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id=" + accountDto.getId()));
        existing.setNumber(accountDto.getNumber());
        existing.setType(accountDto.getType());
        existing.setInitialAmount(accountDto.getInitialAmount());
        existing.setActive(accountDto.isActive());
        existing.setClientId(accountDto.getClientId());
        Account saved = accountRepository.save(existing);
        return entityToDto(saved);
    }

    @Override
    public AccountDto partialUpdate(Long id, PartialAccountDto partialAccountDto) {
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id=" + id));
        existing.setActive(partialAccountDto.isActive());
        Account saved = accountRepository.save(existing);
        return entityToDto(saved);
    }

    @Override
    public void deleteById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id=" + id));
        accountRepository.delete(account);
    }

    // Método de mapeo
    private AccountDto entityToDto(Account entity) {
        return new AccountDto(
                entity.getId(),
                entity.getNumber(),
                entity.getType(),
                entity.getInitialAmount(),
                entity.isActive(),
                entity.getClientId());
    }

    private Account dtoToEntity(AccountDto dto) {
        Account entity = new Account();
        entity.setNumber(dto.getNumber());
        entity.setType(dto.getType());
        entity.setInitialAmount(dto.getInitialAmount());
        entity.setActive(dto.isActive());
        entity.setClientId(dto.getClientId());
        return entity;
    }
}
