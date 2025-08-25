package com.vendas.estoque_service.controller;

import com.vendas.estoque_service.dto.FornecedorDTO;
import com.vendas.estoque_service.model.Fornecedor;
import com.vendas.estoque_service.service.FornecedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
@CrossOrigin(origins = "http://localhost:8080")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @PostMapping
    public ResponseEntity<Fornecedor> criarFornecedor(@Valid @RequestBody FornecedorDTO fornecedorDTO) {
        Fornecedor fornecedor = fornecedorService.criarFornecedor(fornecedorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedor);
    }

    @GetMapping
    public List<Fornecedor> listarFornecedores() {
        return fornecedorService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> buscarFornecedorPorId(@PathVariable Long id) {
        return fornecedorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fornecedor> atualizarFornecedor(@PathVariable Long id, @Valid @RequestBody FornecedorDTO fornecedorDTO) {
        return fornecedorService.atualizarFornecedor(id, fornecedorDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFornecedor(@PathVariable Long id) {
        if (fornecedorService.deletarFornecedor(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Adicione este novo endpoint para a busca por nome
    @GetMapping("/search")
    public ResponseEntity<List<Fornecedor>> searchFornecedores(@RequestParam(required = false) String nome) {
        if (nome != null && !nome.isEmpty()) {
            return ResponseEntity.ok(fornecedorService.buscarPorNome(nome));
        } else {
            return ResponseEntity.ok(fornecedorService.listarTodos());
        }
    }
}