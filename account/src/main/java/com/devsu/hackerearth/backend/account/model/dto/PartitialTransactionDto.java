package com.devsu.hackerearth.backend.account.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartitialTransactionDto {
    private Date date;
    private String type;
    private Double amount;
}
