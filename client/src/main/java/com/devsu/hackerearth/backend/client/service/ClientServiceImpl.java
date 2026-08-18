package com.devsu.hackerearth.backend.client.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.devsu.hackerearth.backend.client.model.Client;
import com.devsu.hackerearth.backend.client.model.dto.ClientDto;
import com.devsu.hackerearth.backend.client.model.dto.PartialClientDto;
import com.devsu.hackerearth.backend.client.repository.ClientRepository;

@Service
public class ClientServiceImpl implements ClientService {

	private final ClientRepository clientRepository;

	public ClientServiceImpl(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	@Override
	public List<ClientDto> getAll() {
		return clientRepository.findAll()
				.stream()
				.map(this::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public ClientDto getById(Long id) {
		return clientRepository.findById(id)
				.map(this::toDto)
				.orElse(null);
	}

	@Override
	public ClientDto create(ClientDto clientDto) {
		Client client = toEntity(clientDto);

		client.setId(null);
		Client savedClient = clientRepository.save(client);

		return toDto(savedClient);
	}

	@Override
	public ClientDto update(ClientDto clientDto) {
		if (clientDto == null || clientDto.getId() == null) {
			return null;
		}

		Client client = clientRepository
				.findById(clientDto.getId())
				.orElse(null);

		if (client == null) {
			return null;
		}
		client.setDni(clientDto.getDni());
		client.setName(clientDto.getName());
		client.setPassword(clientDto.getPassword());
		client.setGender(clientDto.getGender());
		client.setAge(clientDto.getAge());
		client.setAddress(clientDto.getAddress());
		client.setPhone(clientDto.getPhone());
		client.setActive(clientDto.isActive());

		clientRepository.save(client);

		return toDto(client);
	}

	@Override
	public ClientDto partialUpdate(Long id, PartialClientDto partialClientDto) {
		Client client = findById(id);

		if (client == null) {
			return null;
		}

		client.setActive(partialClientDto.isActive());

		Client updateClient = clientRepository.save(client);

		return toDto(updateClient);
	}

	@Override
	public void deleteById(Long id) {
		Client client = findById(id);

		if (client != null) {
			clientRepository.delete(client);
		}
	}

	public Client findById(Long id) {
		return clientRepository.findById(id)
				.orElse(null);
	}

	private ClientDto toDto(Client client) {
		return new ClientDto(
				client.getId(),
				client.getDni(),
				client.getName(),
				client.getPassword(),
				client.getGender(),
				client.getAge(),
				client.getAddress(),
				client.getPhone(),
				client.isActive());
	}

	private Client toEntity(ClientDto clientDto) {
		Client client = new Client();

		client.setId(clientDto.getId());
		client.setDni(clientDto.getDni());
		client.setName(clientDto.getName());
		client.setPassword(clientDto.getPassword());
		client.setGender(clientDto.getGender());
		client.setAge(clientDto.getAge());
		client.setAddress(clientDto.getAddress());
		client.setPhone(clientDto.getPhone());
		client.setActive(clientDto.isActive());

		return client;
	}
}
