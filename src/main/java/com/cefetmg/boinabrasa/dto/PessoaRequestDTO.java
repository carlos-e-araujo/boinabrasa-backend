package com.cefetmg.boinabrasa.dto;

import com.cefetmg.boinabrasa.entity.TipoPessoa;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PessoaRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail deve ser válido.")
    private String email;

    @NotBlank(message = "O CPF ou CNPJ é obrigatório.")
    private String cpfCnpj;

    @NotNull(message = "O tipo de pessoa (PF ou PJ) é obrigatório.")
    private TipoPessoa tipo;
}