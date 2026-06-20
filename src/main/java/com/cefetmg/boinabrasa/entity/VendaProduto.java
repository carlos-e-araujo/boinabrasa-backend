package com.cefetmg.boinabrasa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "venda_produto")
public class VendaProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // bigdecimal para qunatidade pois tem produto vendido em kg e unidade continua funcionando normal
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidade;

    // valor é o preço do momento da venda
    //  porque se o preço do produto mudar
    // no futuro o historico dessa venda não pode mudar junto
    @Column(nullable = false)
    private BigDecimal valor;

    // é essa coluna (id_venda) que aparece na tabela venda_produto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venda", nullable = false)
    private Venda venda;

    // cada item se refere a um produto especifico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;
}