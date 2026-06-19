package com.cefetmg.boinabrasa.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; 
import java.math.BigDecimal;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class ProdutoResponseDTO {
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private String unidade;
    private Integer quantidadeEstoque;
    private Boolean controleEstoque;
}