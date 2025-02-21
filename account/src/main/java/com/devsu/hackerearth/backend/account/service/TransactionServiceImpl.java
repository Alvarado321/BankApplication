package com.devsu.hackerearth.backend.account.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.Transaction;
import com.devsu.hackerearth.backend.account.model.dto.BankStatementDto;
import com.devsu.hackerearth.backend.account.model.dto.TransactionDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;
import com.devsu.hackerearth.backend.account.repository.TransactionRepository;
import com.devsu.hackerearth.backend.exception.InsufficientBalanceException;
import com.devsu.hackerearth.backend.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<TransactionDto> getAll() {
        return transactionRepository.findAll()
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDto getById(Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found, id=" + id));
        return entityToDto(tx);
    }

    @Override
    public TransactionDto create(TransactionDto transactionDto) {
        Long accountId = transactionDto.getAccountId();
        double currentBalance = getCurrentBalance(accountId);

        double newBalance;
        if ("WITHDRAW".equalsIgnoreCase(transactionDto.getType())) {
            if (currentBalance < transactionDto.getAmount()) {
                throw new InsufficientBalanceException("Saldo no disponible");
            }
            newBalance = currentBalance - transactionDto.getAmount();
        } else if ("DEPOSIT".equalsIgnoreCase(transactionDto.getType())) {
            newBalance = currentBalance + transactionDto.getAmount();
        } else {
            throw new RuntimeException("Invalid transaction type: " + transactionDto.getType());
        }

        Transaction entity = new Transaction();
        entity.setDate(new Date());
        entity.setType(transactionDto.getType());
        entity.setAmount(transactionDto.getAmount());
        entity.setBalance(newBalance);
        entity.setAccountId(accountId);

        Transaction saved = transactionRepository.save(entity);
        return entityToDto(saved);
    }

    @Override
    public List<BankStatementDto> getAllByAccountClientIdAndDateBetween(
            Long clientId,
            Date dateTransactionStart,
            Date dateTransactionEnd) {

        List<Account> accounts = accountRepository.findByClientId(clientId);
        if (accounts.isEmpty()) {
            return Collections.emptyList();
        }

        List<BankStatementDto> result = new ArrayList<>();

        for (Account acc : accounts) {
            List<Transaction> txs = transactionRepository.findByAccountIdAndDateBetween(
                    acc.getId(),
                    dateTransactionStart,
                    dateTransactionEnd);

            for (Transaction tx : txs) {
                BankStatementDto statement = new BankStatementDto(
                        tx.getDate(),
                        "client",
                        acc.getNumber(),
                        acc.getType(),
                        acc.getInitialAmount(),
                        acc.isActive(),
                        tx.getType(),
                        tx.getAmount(),
                        tx.getBalance());
                result.add(statement);
            }
        }
        return result;
    }

    @Override
    public TransactionDto getLastByAccountId(Long accountId) {
        Transaction lastTx = transactionRepository.findTopByAccountIdOrderByDateDesc(accountId);
        if (lastTx == null) {
            return null;
        }
        return entityToDto(lastTx);
    }

    private double getCurrentBalance(Long accountId) {
        Transaction lastTx = transactionRepository.findTopByAccountIdOrderByDateDesc(accountId);
        if (lastTx != null) {
            return lastTx.getBalance();
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found, id=" + accountId));
        return account.getInitialAmount();
    }

    private TransactionDto entityToDto(Transaction entity) {
        return new TransactionDto(
                entity.getId(),
                entity.getDate(),
                entity.getType(),
                entity.getAmount(),
                entity.getBalance(),
                entity.getAccountId());
    }
}
