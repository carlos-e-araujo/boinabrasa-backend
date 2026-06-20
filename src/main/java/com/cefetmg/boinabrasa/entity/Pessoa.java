package com.cefetmg.boinabrasa.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pessoa")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true) // unique garante que não existam emails repetidos
    private String email;

    @Column(name = "cpf_cnpj", nullable = false, unique = true) // Mapeia o padrão snake_case do banco
    private String cpfCnpj;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPessoa tipo;
}