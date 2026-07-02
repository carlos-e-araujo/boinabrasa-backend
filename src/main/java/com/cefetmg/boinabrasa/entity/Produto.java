package com.cefetmg.boinabrasa.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private BigDecimal valorUni;

    @Column(nullable = false)
    private String unidade;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidadeEstoque;

    @Column(nullable = false)
    private Boolean controleEstoque;

    @Column(nullable = false)
    private Boolean ativo = true; 
}