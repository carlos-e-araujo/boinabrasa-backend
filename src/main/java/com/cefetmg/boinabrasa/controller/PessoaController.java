package com.cefetmg.boinabrasa.controller;

import com.cefetmg.boinabrasa.dto.PessoaRequestDTO;
import com.cefetmg.boinabrasa.dto.PessoaResponseDTO;
import com.cefetmg.boinabrasa.service.PessoaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pessoas")
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public ResponseEntity<List<PessoaResponseDTO>> listarTodos() {
        List<PessoaResponseDTO> pessoas = pessoaService.listarTodos();
        return ResponseEntity.ok(pessoas); 
    }

    @PostMapping
    public ResponseEntity<PessoaResponseDTO> criar(@Valid @RequestBody PessoaRequestDTO request) {
        PessoaResponseDTO novaPessoa = pessoaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPessoa); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> alterar(@PathVariable Long id, @Valid @RequestBody PessoaRequestDTO request) {
        PessoaResponseDTO pessoaAtualizada = pessoaService.alterar(id, request);
        return ResponseEntity.ok(pessoaAtualizada); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pessoaService.excluir(id);
        return ResponseEntity.noContent().build(); 
    }
}