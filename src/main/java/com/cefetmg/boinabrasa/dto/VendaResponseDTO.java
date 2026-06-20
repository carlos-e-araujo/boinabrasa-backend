package com.cefetmg.boinabrasa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendaResponseDTO {
    private Long id;
    private LocalDateTime data;
    private BigDecimal valorTotal;
    private Long idPessoa;
    private String nomePessoa;
    private List<VendaProdutoResponseDTO> itens;
}