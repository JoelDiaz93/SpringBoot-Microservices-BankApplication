package com.devsu.hackerearth.backend.account.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsu.hackerearth.backend.account.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        Optional<Transaction> findTopByAccountIdOrderByIdDesc(
                        Long accountId);

        List<Transaction> findByAccountId(
                        Long accountId);

        List<Transaction> findByAccountIdAndDateBetween(
                        Long accountId,
                        Date dateTransactionStart,
                        Date dateTransactionEnd);

        List<Transaction> findByAccountIdOrderByDateAscIdAsc(
                        Long accountId);
}
