package com.cefetmg.boinabrasa.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class ProdutoRequestDTO {

    @NotBlank(message = "A descrição do produto é obrigatória.")
    private String descricao;

    @NotNull(message = "O valor do produto é obrigatório.")
    @Positive(message = "O valor do produto tem que ser positivo.")
    private BigDecimal valor;

    @NotBlank(message = "A unidade de referência do produto é obrigatória.")
    private String unidade;

    private BigDecimal quantidadeEstoque;

    @NotNull(message = "Verificação obrigatória")
    private Boolean controleEstoque;

    private Boolean ativo; 
}