package com.vendas.estoque_service.controller;

import com.vendas.estoque_service.dto.VendaDTO;
import com.vendas.estoque_service.model.Venda;
import com.vendas.estoque_service.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @GetMapping
    public List<Venda> getAllVendas() {
        return vendaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> getVendaById(@PathVariable Long id) {
        Optional<Venda> venda = vendaService.findById(id);
        return venda.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Venda> createVenda(@Valid @RequestBody VendaDTO vendaDTO) {
        try {
            Venda novaVenda = vendaService.save(vendaDTO);
            return new ResponseEntity<>(novaVenda, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venda> updateVenda(@PathVariable Long id, @Valid @RequestBody VendaDTO vendaDTO) {
        try {
            Venda vendaAtualizada = vendaService.update(id, vendaDTO);
            return ResponseEntity.ok(vendaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenda(@PathVariable Long id) {
        if (vendaService.findById(id).isPresent()) {
            vendaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}