package com.devsu.hackerearth.backend.client.model;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Client extends Person {
	private String password;
	private boolean isActive;
}
