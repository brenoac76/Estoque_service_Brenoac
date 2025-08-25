package com.vendas.estoque_service.repository;

import com.vendas.estoque_service.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    // Adicione esta linha para buscar por nome (ignorando maiúsculas/minúsculas)
    List<Fornecedor> findByNomeContainingIgnoreCase(String nome);
}