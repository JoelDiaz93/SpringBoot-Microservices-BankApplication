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
		Client client = findById(id);
		return toDto(client);
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
		if (clientDto.getId() == null) {
			throw new IllegalArgumentException("Client id is required");
		}

		Client client = findById(clientDto.getId());
		client.setDni(clientDto.getDni());
		client.setName(clientDto.getName());
		client.setPassword(clientDto.getPassword());
		client.setGender(clientDto.getGender());
		client.setAge(clientDto.getAge());
		client.setAddress(clientDto.getAddress());
		client.setPhone(clientDto.getPhone());
		client.setActive(clientDto.isActive());

		Client updateClient = clientRepository.save(client);
		return toDto(updateClient);
	}

	@Override
	public ClientDto partialUpdate(Long id, PartialClientDto partialClientDto) {
		Client client = findById(id);
		client.setActive(partialClientDto.isActive());
		Client updateClient = clientRepository.save(client);
		return toDto(updateClient);
	}

	@Override
	public void deleteById(Long id) {
		Client client = findById(id);
		clientRepository.delete(client);
	}

	public Client findById(Long id) {
		return clientRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Client no found with id: " + id));
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
