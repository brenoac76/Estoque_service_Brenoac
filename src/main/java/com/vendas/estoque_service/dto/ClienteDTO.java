package com.vendas.estoque_service.dto;



import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;



public class ClienteDTO {



    @NotBlank(message = "O nome do cliente é obrigatório")

    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")

    private String nome;



    @NotBlank(message = "O CPF é obrigatório")

    @Size(min = 11, max = 11, message = "O CPF deve ter 11 caracteres")

    private String cpf;



    @NotBlank(message = "O e-mail é obrigatório")

    @Email(message = "E-mail inválido")

    private String email;



    @NotBlank(message = "O telefone é obrigatório")

    private String telefone;



// Getters e Setters

    public String getNome() {

        return nome;

    }



    public void setNome(String nome) {

        this.nome = nome;

    }



    public String getCpf() {

        return cpf;

    }



    public void setCpf(String cpf) {

        this.cpf = cpf;

    }



    public String getEmail() {

        return email;

    }



    public void setEmail(String email) {

        this.email = email;

    }



    public String getTelefone() {

        return telefone;

    }



    public void setTelefone(String telefone) {

        this.telefone = telefone;

    }

}