package com.cefetmg.boinabrasa.service;

import com.cefetmg.boinabrasa.dto.ProdutoRequestDTO;
import com.cefetmg.boinabrasa.dto.ProdutoResponseDTO;
import com.cefetmg.boinabrasa.entity.Produto;
import com.cefetmg.boinabrasa.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<ProdutoResponseDTO> listarTodos() {
        List<Produto> produtos = produtoRepository.findAll();
        return produtos.stream()
                .map(p -> new ProdutoResponseDTO(
                    p.getId(), 
                    p.getDescricao(), 
                    p.getValorUni(), 
                    p.getUnidade(), 
                    p.getQuantidadeEstoque(), 
                    p.getControleEstoque()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO request) {
        Produto produto = new Produto();
        produto.setDescricao(request.getDescricao());
        produto.setValorUni(request.getValor());
        produto.setUnidade(request.getUnidade());
        produto.setQuantidadeEstoque(request.getQuantidadeEstoque());
        produto.setControleEstoque(request.getControleEstoque());

        Produto p = produtoRepository.save(produto);
        
        return new ProdutoResponseDTO(p.getId(), p.getDescricao(), p.getValorUni(), p.getUnidade(), p.getQuantidadeEstoque(), p.getControleEstoque());
    }

    @Transactional
    public ProdutoResponseDTO alterar(Long id, ProdutoRequestDTO request) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));

        produtoExistente.setDescricao(request.getDescricao());
        produtoExistente.setValorUni(request.getValor());
        produtoExistente.setUnidade(request.getUnidade());
        produtoExistente.setQuantidadeEstoque(request.getQuantidadeEstoque());
        produtoExistente.setControleEstoque(request.getControleEstoque());

        Produto p = produtoRepository.save(produtoExistente);
        
        return new ProdutoResponseDTO(p.getId(), p.getDescricao(), p.getValorUni(), p.getUnidade(), p.getQuantidadeEstoque(), p.getControleEstoque());
    }

    @Transactional
    public void excluir(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Não é possível excluir. Produto não encontrado com o ID: " + id);
        }
        produtoRepository.deleteById(id);
    }
}