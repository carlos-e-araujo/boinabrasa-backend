package com.cefetmg.boinabrasa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendaProdutoResponseDTO {
    private Long idProduto;
    private String descricaoProduto;
    private BigDecimal quantidade;
    private BigDecimal valor;
}