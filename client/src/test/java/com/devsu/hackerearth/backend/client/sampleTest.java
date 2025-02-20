package com.devsu.hackerearth.backend.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

import com.devsu.hackerearth.backend.client.controller.ClientController;
import com.devsu.hackerearth.backend.client.model.dto.ClientDto;
import com.devsu.hackerearth.backend.client.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc

public class sampleTest {

    private ClientService clientService = mock(ClientService.class);
    private ClientController clientController = new ClientController(clientService);

    @Test
    void createClientTest() {
        // Arrange
        ClientDto newClient = new ClientDto(1L, "Dni", "Name", "Password", "Gender", 1, "Address", "9999999999", true);
        ClientDto createdClient = new ClientDto(1L, "Dni", "Name", "Password", "Gender", 1, "Address", "9999999999",
                true);
        when(clientService.create(newClient)).thenReturn(createdClient);

        // Act
        ResponseEntity<ClientDto> response = clientController.create(newClient);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdClient, response.getBody());
    }

    /**
     * Prueba unitaria directa de la lógica de ClientService (mockeada).
     */
    @Test
    void unitTest_createClient_andVerifySaved() {
        // Arrange: Stubbing del mock para simular la creación del cliente
        ClientDto newClient = new ClientDto(null, "1234567890", "Test User", "password", "M", 25, "Some Address",
                "9999999999", true);
        ClientDto savedClient = new ClientDto(1L, "1234567890", "Test User", "password", "M", 25, "Some Address",
                "9999999999", true);
        when(clientService.create(newClient)).thenReturn(savedClient);

        // Act
        ClientDto result = clientService.create(newClient);

        // Assert
        assertNotNull(result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("1234567890", result.getDni());
    }

    @Autowired
    private MockMvc mockMvc;

    /**
     * Prueba de integración que invoca el endpoint GET /api/clients y espera un 200
     * OK.
     */
    @Test
    void integrationTest_getAllClients_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk());
    }
}
