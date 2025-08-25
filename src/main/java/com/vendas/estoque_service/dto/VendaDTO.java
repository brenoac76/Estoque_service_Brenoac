package com.vendas.estoque_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class VendaDTO {

    @NotNull(message = "O ID do cliente não pode ser nulo.")
    private Long clienteId;

    @NotEmpty(message = "A venda deve conter pelo menos um item.")
    @Valid
    private List<ItemVendaDTO> itensVenda;

    // Construtor
    public VendaDTO() {
    }

    // Getters and Setters

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public List<ItemVendaDTO> getItensVenda() {
        return itensVenda;
    }

    public void setItensVenda(List<ItemVendaDTO> itensVenda) {
        this.itensVenda = itensVenda;
    }
}