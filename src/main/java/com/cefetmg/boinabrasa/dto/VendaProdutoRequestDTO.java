package com.cefetmg.boinabrasa.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

import java.math.BigDecimal;

// Item de uma venda (frontend manda só o idProduto, não o objeto inteiro)
// quantidade é BigDecimal pra suportar kg além de inteiro para unidade
// controleEstoque no produto define se debita estoque ou não
@Data
public class VendaProdutoRequestDTO {

    @NotNull(message = "O produto do item é obrigatório.")
    private Long idProduto;

    @NotNull(message = "A quantidade é obrigatória.")
    @Positive(message = "A quantidade tem que ser maior que zero.")
    private BigDecimal quantidade;
}