package com.cefetmg.boinabrasa.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.util.List;

// representa os dados que chegam do sistema quando uma nova venda é realizada
// o valor total é o próprio sistema que calcula 
@Data
public class VendaRequestDTO {

    @NotNull(message = "A pessoa (funcionário) da venda é obrigatória.")
    private Long idPessoa;

    @NotEmpty(message = "A venda precisa ter pelo menos um item.")
    private List<@Valid VendaProdutoRequestDTO> itens;
}