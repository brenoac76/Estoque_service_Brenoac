package com.vendas.estoque_service.controller;

import com.vendas.estoque_service.dto.ClienteDTO;
import com.vendas.estoque_service.model.Cliente;
import com.vendas.estoque_service.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<Cliente> getAllClientes() {
        return clienteService.findAll();
    }

    @GetMapping("/search")
    public List<Cliente> searchClientes(@RequestParam String nome) {
        return clienteService.findByNome(nome);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.findById(id);
        return cliente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cliente> createCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        Cliente novoCliente = new Cliente();
        novoCliente.setNome(clienteDTO.getNome());
        novoCliente.setCpf(clienteDTO.getCpf());
        novoCliente.setEmail(clienteDTO.getEmail());
        novoCliente.setTelefone(clienteDTO.getTelefone());
        Cliente clienteSalvo = clienteService.save(novoCliente);
        return new ResponseEntity<>(clienteSalvo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        Optional<Cliente> clienteExistente = clienteService.findById(id);
        if (clienteExistente.isPresent()) {
            Cliente cliente = clienteExistente.get();
            cliente.setNome(clienteDTO.getNome());
            cliente.setCpf(clienteDTO.getCpf());
            cliente.setEmail(clienteDTO.getEmail());
            cliente.setTelefone(clienteDTO.getTelefone());
            Cliente clienteAtualizado = clienteService.save(cliente);
            return ResponseEntity.ok(clienteAtualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        if (clienteService.findById(id).isPresent()) {
            clienteService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}