package com.cefetmg.boinabrasa.dto;

import com.cefetmg.boinabrasa.entity.TipoPessoa;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String cpfCnpj;
    private TipoPessoa tipo;
}