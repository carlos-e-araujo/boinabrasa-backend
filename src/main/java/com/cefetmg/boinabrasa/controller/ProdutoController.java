package com.cefetmg.boinabrasa.controller;

import com.cefetmg.boinabrasa.dto.ProdutoRequestDTO;
import com.cefetmg.boinabrasa.dto.ProdutoResponseDTO;
import com.cefetmg.boinabrasa.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos); 
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO request) {
        ProdutoResponseDTO novoProduto = produtoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> alterar(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO request) {
        ProdutoResponseDTO produtoAtualizado = produtoService.alterar(id, request);
        return ResponseEntity.ok(produtoAtualizado); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build(); 
    }
}