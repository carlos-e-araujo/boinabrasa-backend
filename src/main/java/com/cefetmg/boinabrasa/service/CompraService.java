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
import java.util.stream.Collectors;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final PessoaRepository pessoaRepository;
    private final ProdutoRepository produtoRepository;

    public CompraService(
            CompraRepository compraRepository,
            PessoaRepository pessoaRepository,
            ProdutoRepository produtoRepository) {
        this.compraRepository = compraRepository;
        this.pessoaRepository = pessoaRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<CompraResponseDTO> listarTodos() {
        List<Compra> compras = compraRepository.findAll();

        return compras.stream()
                .map(this::paraResponseDTO)
                .collect(Collectors.toList());
    }

    private CompraResponseDTO paraResponseDTO(Compra c) {
        List<CompraProdutoResponseDTO> itens = new ArrayList<>();
        for (CompraProduto item : c.getItens()) {
            itens.add(new CompraProdutoResponseDTO(
                    item.getProduto().getId(),
                    item.getProduto().getDescricao(),
                    item.getQuantidade(),
                    item.getValor()));
        }
        return new CompraResponseDTO(
                c.getId(),
                c.getDataCompra(),
                c.getValorCompra(),
                c.getFornecedor().getNome(),
                itens);
    }

    @Transactional
    public CompraResponseDTO criar(CompraRequestDTO request) {
        Pessoa fornecedor = pessoaRepository.findById(request.getIdFornecedor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Fornecedor não encontrado com o ID: " + request.getIdFornecedor()));

        Compra compra = new Compra();
        compra.setDataCompra(request.getDataCompra());
        compra.setFornecedor(fornecedor);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (CompraProdutoRequestDTO itemReq : request.getItens()) {
            Produto produto = produtoRepository.findById(itemReq.getIdProduto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Produto não encontrado com o ID: " + itemReq.getIdProduto()));

            CompraProduto item = new CompraProduto();
            item.setCompra(compra);
            item.setProduto(produto);
            item.setQuantidade(itemReq.getQuantidade());
            item.setValor(itemReq.getValor());

            compra.getItens().add(item);

            BigDecimal subtotal = item.getValor().multiply(item.getQuantidade());
            valorTotal = valorTotal.add(subtotal);

            if (Boolean.TRUE.equals(produto.getControleEstoque())) {
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade().intValue());
                produtoRepository.save(produto);
            }
        }

        compra.setValorCompra(valorTotal);

        Compra salva = compraRepository.save(compra);

        return paraResponseDTO(salva);
    }

    @Transactional
    public CompraResponseDTO alterar(Long id, CompraRequestDTO request) {
        Compra compraExistente = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Compra não encontrada com o ID: " + id));

        Pessoa fornecedor = pessoaRepository.findById(request.getIdFornecedor())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fornecedor não encontrado com o ID: " + request.getIdFornecedor()));

        for (CompraProduto itemAntigo : compraExistente.getItens()) {
            Produto produtoAntigo = itemAntigo.getProduto();
            if (Boolean.TRUE.equals(produtoAntigo.getControleEstoque())) {
                produtoAntigo.setQuantidadeEstoque(
                        produtoAntigo.getQuantidadeEstoque() - itemAntigo.getQuantidade().intValue());
                produtoRepository.save(produtoAntigo);
            }
        }

        compraExistente.getItens().clear();

        compraExistente.setDataCompra(request.getDataCompra());
        compraExistente.setFornecedor(fornecedor);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (CompraProdutoRequestDTO itemReq : request.getItens()) {
            Produto produto = produtoRepository.findById(itemReq.getIdProduto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Produto não encontrado com o ID: " + itemReq.getIdProduto()));

            CompraProduto item = new CompraProduto();
            item.setCompra(compraExistente);
            item.setProduto(produto);
            item.setQuantidade(itemReq.getQuantidade());
            item.setValor(itemReq.getValor());

            compraExistente.getItens().add(item);

            BigDecimal subtotal = item.getValor().multiply(item.getQuantidade());
            valorTotal = valorTotal.add(subtotal);

            if (Boolean.TRUE.equals(produto.getControleEstoque())) {
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade().intValue());
                produtoRepository.save(produto);
            }
        }

        compraExistente.setValorCompra(valorTotal);

        Compra c = compraRepository.save(compraExistente);

        return paraResponseDTO(c);
    }

    @Transactional
    public void excluir(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Não é possível excluir. Compra não encontrada com o ID: " + id));

        for (CompraProduto item : compra.getItens()) {
            Produto produto = item.getProduto();
            if (Boolean.TRUE.equals(produto.getControleEstoque())) {
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade().intValue());
                produtoRepository.save(produto);
            }
        }

        compraRepository.deleteById(id);
    }
}