package com.cefetmg.boinabrasa.controller;

import com.cefetmg.boinabrasa.dto.VendaRequestDTO;
import com.cefetmg.boinabrasa.dto.VendaResponseDTO;
import com.cefetmg.boinabrasa.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @GetMapping
    public ResponseEntity<List<VendaResponseDTO>> listarTodos() {
        List<VendaResponseDTO> vendas = vendaService.listarTodos();
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {
        VendaResponseDTO venda = vendaService.buscarPorId(id);
        return ResponseEntity.ok(venda);
    }

    @PostMapping
    public ResponseEntity<VendaResponseDTO> criar(@Valid @RequestBody VendaRequestDTO request) {
        VendaResponseDTO novaVenda = vendaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaVenda);
    }

    // nao criei alterar venda, pq nao faz muito sentido alterar que ja foi feito
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        vendaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}