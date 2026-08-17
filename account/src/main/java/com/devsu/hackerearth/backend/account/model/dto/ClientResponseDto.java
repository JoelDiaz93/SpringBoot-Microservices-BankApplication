package com.devsu.hackerearth.backend.account.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientResponseDto {
    private Long id;
    private String dni;
    private String name;
    private String password;
    private String gender;
    private int age;
    private String address;
    private String phone;
    private boolean isActive;
}
