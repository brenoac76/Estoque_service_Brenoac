package com.vendas.estoque_service.controller;

import com.vendas.estoque_service.dto.ProdutoDTO;
import com.vendas.estoque_service.model.Produto;
import com.vendas.estoque_service.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> findAll() {
        List<ProdutoDTO> produtos = produtoService.findAll().stream()
                .map(ProdutoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProdutoDTO>> searchProdutos(@RequestParam("nome") String nome) {
        List<ProdutoDTO> produtos = produtoService.findByNome(nome).stream()
                .map(ProdutoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> findById(@PathVariable Long id) {
        return produtoService.findById(id)
                .map(produto -> ResponseEntity.ok(new ProdutoDTO(produto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProdutoDTO> createProduto(@Valid @RequestBody ProdutoDTO produtoDTO) {
        Produto produto = new Produto();
        produto.setNome(produtoDTO.getNome());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setQuantidade(produtoDTO.getQuantidade());
        produto.setPrecoDeCusto(produtoDTO.getPrecoDeCusto());
        produto.setValorDeVenda(produtoDTO.getValorDeVenda());

        Produto savedProduto = produtoService.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProdutoDTO(savedProduto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> updateProduto(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoDTO) {
        return produtoService.findById(id)
                .map(produto -> {
                    produto.setNome(produtoDTO.getNome());
                    produto.setDescricao(produtoDTO.getDescricao());
                    produto.setQuantidade(produtoDTO.getQuantidade());
                    produto.setPrecoDeCusto(produtoDTO.getPrecoDeCusto());
                    produto.setValorDeVenda(produtoDTO.getValorDeVenda());
                    Produto updatedProduto = produtoService.save(produto);
                    return ResponseEntity.ok(new ProdutoDTO(updatedProduto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id) {
        produtoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}