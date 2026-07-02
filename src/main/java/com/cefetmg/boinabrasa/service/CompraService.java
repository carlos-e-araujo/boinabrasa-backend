package com.cefetmg.boinabrasa.service;

import com.cefetmg.boinabrasa.dto.*;
import com.cefetmg.boinabrasa.entity.*;
import com.cefetmg.boinabrasa.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final PessoaRepository pessoaRepository;
    private final ProdutoRepository produtoRepository;

    public CompraService(CompraRepository compraRepository, PessoaRepository pessoaRepository, ProdutoRepository produtoRepository) {
        this.compraRepository = compraRepository;
        this.pessoaRepository = pessoaRepository;
        this.produtoRepository = produtoRepository;
    }
  
    public List<CompraResponseDTO> listarTodos() {
        List<Compra> compras = compraRepository.findAll();
        List<CompraResponseDTO> resposta = new ArrayList<>();

        // percorre os registros um a um   
        for (int i = 0; i < compras.size(); i++) {
            Compra c = compras.get(i);
            resposta.add(paraResponseDTO(c));
        }

        return resposta;
    }

    // mapeia a entidade compra e seus itens para dto de resposta
    private CompraResponseDTO paraResponseDTO(Compra c) {
        List<CompraProdutoResponseDTO> itens = new ArrayList<>();
        for (CompraProduto item : c.getItens()) {
            itens.add(new CompraProdutoResponseDTO(item.getProduto().getId(), item.getProduto().getDescricao(), item.getQuantidade(), item.getValor()));
        }
        return new CompraResponseDTO(c.getId(), c.getDataCompra(), c.getValorCompra(), c.getFornecedor().getNome(), itens);
    }

    // registra uma nova nota de compra e incrementa o estoque 
    @Transactional
    public CompraResponseDTO criar(CompraRequestDTO request) {
        Pessoa fornecedor = pessoaRepository.findById(request.getIdFornecedor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"));

        Compra compra = new Compra();
        compra.setDataCompra(request.getDataCompra());
        compra.setFornecedor(fornecedor);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (CompraProdutoRequestDTO itemReq : request.getItens()) {
            Produto produto = produtoRepository.findById(itemReq.getIdProduto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

            CompraProduto item = new CompraProduto();
            item.setCompra(compra);
            item.setProduto(produto);
            item.setQuantidade(itemReq.getQuantidade());
            item.setValor(itemReq.getValor());
            compra.getItens().add(item);

            BigDecimal subtotal = item.getValor().multiply(item.getQuantidade());
            valorTotal = valorTotal.add(subtotal);

            // adiciona a quantidade comprada ao estoque se o controleEstoque estiver ativo
            if (Boolean.TRUE.equals(produto.getControleEstoque())) {
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque().add(item.getQuantidade()));
                produtoRepository.save(produto);
            }
        }

        compra.setValorCompra(valorTotal);
        Compra salva = compraRepository.save(compra);
        return paraResponseDTO(salva);
    }

    // atualiza os dados da compra realizando o estorno e recalculo do estoque
    @Transactional
    public CompraResponseDTO alterar(Long id, CompraRequestDTO request) {
        Compra compraExistente = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra não encontrada"));

        Pessoa fornecedor = pessoaRepository.findById(request.getIdFornecedor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"));

        // remove temporariamente as quantidades antigas do estoque antes de atualizar
        for (CompraProduto itemAntigo : compraExistente.getItens()) {
            Produto produtoAntigo = itemAntigo.getProduto();
            if (Boolean.TRUE.equals(produtoAntigo.getControleEstoque())) {
                produtoAntigo.setQuantidadeEstoque(produtoAntigo.getQuantidadeEstoque().subtract(itemAntigo.getQuantidade()));
                produtoRepository.save(produtoAntigo);
            }
        }

        compraExistente.getItens().clear();
        compraExistente.setDataCompra(request.getDataCompra());
        compraExistente.setFornecedor(fornecedor);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (CompraProdutoRequestDTO itemReq : request.getItens()) {
            Produto produto = produtoRepository.findById(itemReq.getIdProduto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

            CompraProduto item = new CompraProduto();
            item.setCompra(compraExistente);
            item.setProduto(produto);
            item.setQuantidade(itemReq.getQuantidade());
            item.setValor(itemReq.getValor());
            compraExistente.getItens().add(item);

            BigDecimal subtotal = item.getValor().multiply(item.getQuantidade());
            valorTotal = valorTotal.add(subtotal);

            // insere a nova quantidade atualizada no estoque do produto
            if (Boolean.TRUE.equals(produto.getControleEstoque())) {
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque().add(item.getQuantidade()));
                produtoRepository.save(produto);
            }
        }

        compraExistente.setValorCompra(valorTotal);
        Compra c = compraRepository.save(compraExistente);
        return paraResponseDTO(c);
    }

    // remove o registro da nota fiscal e subtrai as quantidades do estoque
    @Transactional
    public void excluir(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra não encontrada"));

        // desfaz o incremento de estoque decrementando os produtos desta compra
        for (CompraProduto item : compra.getItens()) {
            Produto produto = item.getProduto();
            if (Boolean.TRUE.equals(produto.getControleEstoque())) {
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque().subtract(item.getQuantidade()));
                produtoRepository.save(produto);
            }
        }
        compraRepository.deleteById(id);
    }
}