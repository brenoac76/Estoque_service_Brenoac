package com.vendas.estoque_service.dto;

import com.vendas.estoque_service.model.Produto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal; // Importação necessária

public class ProdutoDTO {

    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 0, message = "A quantidade deve ser um valor positivo")
    private Integer quantidade;

    @NotNull(message = "O preço de custo é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço de custo deve ser um valor positivo")
    private BigDecimal precoDeCusto;

    @NotNull(message = "O valor de venda é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O valor de venda deve ser um valor positivo")
    private BigDecimal valorDeVenda;

    // Construtor
    public ProdutoDTO() {}

    public ProdutoDTO(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.quantidade = produto.getQuantidade();
        this.precoDeCusto = produto.getPrecoDeCusto();
        this.valorDeVenda = produto.getValorDeVenda();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoDeCusto() {
        return precoDeCusto;
    }

    public void setPrecoDeCusto(BigDecimal precoDeCusto) {
        this.precoDeCusto = precoDeCusto;
    }

    public BigDecimal getValorDeVenda() {
        return valorDeVenda;
    }

    public void setValorDeVenda(BigDecimal valorDeVenda) {
        this.valorDeVenda = valorDeVenda;
    }
}