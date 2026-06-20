package com.cefetmg.boinabrasa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "venda")
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal; // BigDecimal é mt mais preciso para operacoes financeiras

    // Toda venda pertence a uma Pessoa (idPessoaFisica)
    // varias vendas podem apontar para a mesma pessoa
    // FetchType.LAZY evita carregar a Pessoa inteira sempre que uma venda é buscada,
    // so carrega quando alguem realmente chamar getPessoa()
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pessoa_fisica", nullable = false)
    private Pessoa pessoa;

    // Uma Venda tem vários itens (VendaProduto)
    // mappedBy = "venda" indica que quem tem a FK é a classe VendaProduto e nao a Venda
    // cascade = ALL: se salvar/excluir uma venda, os itens dela são salvos/excluidos junto
    // orphanRemoval = true: se remover um item dessa lista em memoria ele é apagado do banco tambem
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VendaProduto> itens = new ArrayList<>();
}