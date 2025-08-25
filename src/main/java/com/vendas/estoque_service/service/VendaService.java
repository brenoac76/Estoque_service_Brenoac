package com.vendas.estoque_service.service;

import com.vendas.estoque_service.model.Venda;
import com.vendas.estoque_service.model.Cliente;
import com.vendas.estoque_service.model.Produto;
import com.vendas.estoque_service.model.ItemVenda;
import com.vendas.estoque_service.dto.VendaDTO;
import com.vendas.estoque_service.dto.ItemVendaDTO;
import com.vendas.estoque_service.repository.VendaRepository;
import com.vendas.estoque_service.repository.ClienteRepository;
import com.vendas.estoque_service.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Venda> findAll() {
        return vendaRepository.findAll();
    }

    public Optional<Venda> findById(Long id) {
        return vendaRepository.findById(id);
    }

    // Método para salvar a venda e seus itens
    @Transactional
    public Venda save(VendaDTO vendaDTO) {
        Cliente cliente = clienteRepository.findById(vendaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Venda novaVenda = new Venda();
        novaVenda.setCliente(cliente);
        novaVenda.setDataVenda(LocalDateTime.now());

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemVendaDTO itemDTO : vendaDTO.getItensVenda()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (produto.getQuantidade() < itemDTO.getQuantidade()) {
                throw new RuntimeException("Quantidade de " + produto.getNome() + " insuficiente em estoque.");
            }

            int novaQuantidade = produto.getQuantidade() - itemDTO.getQuantidade();
            produto.setQuantidade(novaQuantidade);
            produtoRepository.save(produto);

            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setVenda(novaVenda);
            itemVenda.setProduto(produto);
            itemVenda.setQuantidade(itemDTO.getQuantidade());
            itemVenda.setPrecoUnitario(produto.getValorDeVenda());

            valorTotal = valorTotal.add(itemVenda.getPrecoUnitario().multiply(new BigDecimal(itemVenda.getQuantidade())));
            novaVenda.getItensVenda().add(itemVenda);
        }

        novaVenda.setValorTotal(valorTotal);
        return vendaRepository.save(novaVenda);
    }

    // Método para atualizar uma venda existente
    @Transactional
    public Venda update(Long id, VendaDTO vendaDTO) {
        Venda vendaExistente = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com o ID: " + id));

        // 1. Devolve os itens antigos ao estoque antes de processar os novos
        for (ItemVenda itemAntigo : vendaExistente.getItensVenda()) {
            Produto produto = itemAntigo.getProduto();
            produto.setQuantidade(produto.getQuantidade() + itemAntigo.getQuantidade());
            produtoRepository.save(produto);
        }

        // 2. Limpa a lista de itens da venda existente
        vendaExistente.getItensVenda().clear();
        vendaRepository.saveAndFlush(vendaExistente);

        // 3. Processa os novos itens do DTO
        BigDecimal novoValorTotal = BigDecimal.ZERO;

        for (ItemVendaDTO itemDto : vendaDTO.getItensVenda()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (produto.getQuantidade() < itemDto.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // Atualiza o estoque com as novas quantidades
            produto.setQuantidade(produto.getQuantidade() - itemDto.getQuantidade());
            produtoRepository.save(produto);

            // Adiciona os novos itens à venda
            ItemVenda novoItem = new ItemVenda();
            novoItem.setVenda(vendaExistente);
            novoItem.setProduto(produto);
            novoItem.setQuantidade(itemDto.getQuantidade());
            novoItem.setPrecoUnitario(produto.getValorDeVenda());

            novoValorTotal = novoValorTotal.add(novoItem.getPrecoUnitario().multiply(new BigDecimal(novoItem.getQuantidade())));
            vendaExistente.getItensVenda().add(novoItem);
        }

        // Atualiza o cliente e o valor total
        Cliente cliente = clienteRepository.findById(vendaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        vendaExistente.setCliente(cliente);
        vendaExistente.setValorTotal(novoValorTotal);

        return vendaRepository.save(vendaExistente);
    }

    // Método para deletar uma venda
    @Transactional
    public void deleteById(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        // Devolve a quantidade de cada produto ao estoque
        for (ItemVenda item : venda.getItensVenda()) {
            Produto produto = item.getProduto();
            produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        vendaRepository.deleteById(id);
    }
}