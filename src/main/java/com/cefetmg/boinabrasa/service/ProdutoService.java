package com.cefetmg.boinabrasa.service;

import com.cefetmg.boinabrasa.dto.ProdutoRequestDTO;
import com.cefetmg.boinabrasa.dto.ProdutoResponseDTO;
import com.cefetmg.boinabrasa.entity.Produto;
import com.cefetmg.boinabrasa.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // lista os registros  
    public List<ProdutoResponseDTO> listarTodos() {
        List<Produto> produtos = produtoRepository.findAll();
        List<ProdutoResponseDTO> resposta = new ArrayList<>();

        // percorre a lista bruta e filtra manualmente os registros ativos
        for (int i = 0; i < produtos.size(); i++) {
            Produto p = produtos.get(i);
            
            if (p.getAtivo() != null && p.getAtivo()) {
                ProdutoResponseDTO dto = new ProdutoResponseDTO(
                        p.getId(),
                        p.getDescricao(),
                        p.getValorUni(),
                        p.getUnidade(),
                        p.getQuantidadeEstoque(),
                        p.getControleEstoque(),
                        p.getAtivo()
                );
                resposta.add(dto);
            }
        }

        return resposta;
    }

    @Transactional
    public ProdutoResponseDTO criar(ProdutoRequestDTO request) {
        Produto produto = new Produto();
        produto.setDescricao(request.getDescricao());
        produto.setValorUni(request.getValor());
        produto.setUnidade(request.getUnidade());
        
        // o estoque inicial de um novo produto para zero 
        produto.setQuantidadeEstoque(BigDecimal.ZERO);
        produto.setControleEstoque(request.getControleEstoque());

        Produto p = produtoRepository.save(produto);

        return new ProdutoResponseDTO(p.getId(), p.getDescricao(), p.getValorUni(), p.getUnidade(),
                p.getQuantidadeEstoque(), p.getControleEstoque(), true);
    }

    // funcao para alterar
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

        return new ProdutoResponseDTO(p.getId(), p.getDescricao(), p.getValorUni(), p.getUnidade(),
                p.getQuantidadeEstoque(), p.getControleEstoque(), p.getAtivo());
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));

        // executa a exclusao logica ou seja ativo = false
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }
}