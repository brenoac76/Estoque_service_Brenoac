package com.vendas.estoque_service.service;

import com.vendas.estoque_service.dto.FornecedorDTO;
import com.vendas.estoque_service.model.Fornecedor;
import com.vendas.estoque_service.repository.FornecedorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    public Fornecedor criarFornecedor(FornecedorDTO fornecedorDTO) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setNome(fornecedorDTO.getNome());
        fornecedor.setCnpj(fornecedorDTO.getCnpj());
        fornecedor.setEmail(fornecedorDTO.getEmail());
        return fornecedorRepository.save(fornecedor);
    }

    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.findAll();
    }

    public Optional<Fornecedor> buscarPorId(Long id) {
        return fornecedorRepository.findById(id);
    }

    @Transactional
    public Optional<Fornecedor> atualizarFornecedor(Long id, FornecedorDTO fornecedorDTO) {
        return fornecedorRepository.findById(id).map(fornecedor -> {
            fornecedor.setNome(fornecedorDTO.getNome());
            fornecedor.setCnpj(fornecedorDTO.getCnpj());
            fornecedor.setEmail(fornecedorDTO.getEmail());
            return fornecedorRepository.save(fornecedor);
        });
    }

    @Transactional
    public boolean deletarFornecedor(Long id) {
        if (fornecedorRepository.existsById(id)) {
            fornecedorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Adicione este novo método de busca
    public List<Fornecedor> buscarPorNome(String nome) {
        return fornecedorRepository.findByNomeContainingIgnoreCase(nome);
    }
}