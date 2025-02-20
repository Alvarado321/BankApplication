package com.devsu.hackerearth.backend.client.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.devsu.hackerearth.backend.client.model.Client;
import com.devsu.hackerearth.backend.client.model.dto.ClientDto;
import com.devsu.hackerearth.backend.client.model.dto.PartialClientDto;
import com.devsu.hackerearth.backend.client.repository.ClientRepository;
import com.devsu.hackerearth.backend.exception.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

	private final ClientRepository clientRepository;

	public ClientServiceImpl(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	@Override
	public List<ClientDto> getAll() {
		// Get all clients
		List<Client> entities = clientRepository.findAll();
		return entities.stream()
				.map(this::entityToDto)
				.collect(Collectors.toList());
	}

	@Override
	public ClientDto getById(Long id) {
		// Get clients by id
		Client entity = clientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found with id=" + id));
		return entityToDto(entity);
	}

	@Override
	public ClientDto create(ClientDto clientDto) {
		// Create client
		Client entity = dtoToEntity(clientDto);
		Client saved = clientRepository.save(entity);
		return entityToDto(saved);
	}

	@Override
	public ClientDto update(ClientDto clientDto) {
		// Update client
		Client existing = clientRepository.findById(clientDto.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Client not found with id=" + clientDto.getId()));

		existing.setName(clientDto.getName());
		existing.setDni(clientDto.getDni());
		existing.setGender(clientDto.getGender());
		existing.setAddress(clientDto.getAddress());
		existing.setPhone(clientDto.getPhone());
		existing.setAge(clientDto.getAge());
		existing.setPassword(clientDto.getPassword());
		existing.setActive(clientDto.isActive());

		Client saved = clientRepository.save(existing);
		return entityToDto(saved);
	}

	@Override
	public ClientDto partialUpdate(Long id, PartialClientDto PartialClientDto) {
		// Partial update account
		Client existing = clientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found with id=" + id));

		if (PartialClientDto != null) {
			existing.setActive(PartialClientDto.isActive());
		}

		Client saved = clientRepository.save(existing);
		return entityToDto(saved);
	}

	@Override
	public void deleteById(Long id) {
		// Delete client
		clientRepository.deleteById(id);
	}

	// Metodos de conversión
	private ClientDto entityToDto(Client entity) {
		return new ClientDto(
				entity.getId(),
				entity.getDni(),
				entity.getName(),
				entity.getPassword(),
				entity.getGender(),
				entity.getAge(),
				entity.getAddress(),
				entity.getPhone(),
				entity.isActive());
	}

	private Client dtoToEntity(ClientDto dto) {
		Client entity = new Client();
		entity.setName(dto.getName());
		entity.setDni(dto.getDni());
		entity.setGender(dto.getGender());
		entity.setAddress(dto.getAddress());
		entity.setPhone(dto.getPhone());
		entity.setAge(dto.getAge());
		entity.setPassword(dto.getPassword());
		entity.setActive(dto.isActive());
		return entity;
	}
}
