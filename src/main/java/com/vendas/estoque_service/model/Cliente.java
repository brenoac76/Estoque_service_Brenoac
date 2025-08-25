package com.vendas.estoque_service.model;



import jakarta.persistence.*;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;



@Entity

@Table(name = "clientes")

public class Cliente {



    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



    @NotBlank(message = "O nome é obrigatório")

    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")

    @Column(nullable = false, length = 100)

    private String nome;



    @NotBlank(message = "O CPF é obrigatório")

    @Size(min = 11, max = 11, message = "O CPF deve ter 11 caracteres")

    @Column(nullable = false, unique = true, length = 11)

    private String cpf;



    @Email(message = "E-mail inválido")

    @Column(length = 100)

    private String email;



    @NotBlank(message = "O telefone é obrigatório")

    @Column(length = 20)

    private String telefone;



// Construtores

    public Cliente() {}



    public Cliente(String nome, String cpf, String email, String telefone) {

        this.nome = nome;

        this.cpf = cpf;

        this.email = email;

        this.telefone = telefone;

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