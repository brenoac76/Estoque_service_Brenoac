package com.vendas.estoque_service.service;

import com.vendas.estoque_service.model.Cliente;
import com.vendas.estoque_service.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }

    // NOVO MÉTODO PARA BUSCA POR NOME
    public List<Cliente> findByNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }
}