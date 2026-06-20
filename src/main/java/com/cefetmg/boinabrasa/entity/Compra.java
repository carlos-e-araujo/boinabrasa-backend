package com.cefetmg.boinabrasa.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Compra")
public class Compra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataCompra;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorCompra;

    @ManyToOne
    @JoinColumn(name="id_fornecedor", nullable = false)
    private Pessoa forncedor;


}
