package com.cefetmg.boinabrasa.service;

import com.cefetmg.boinabrasa.dto.VendaProdutoRequestDTO;
import com.cefetmg.boinabrasa.dto.VendaProdutoResponseDTO;
import com.cefetmg.boinabrasa.dto.VendaRequestDTO;
import com.cefetmg.boinabrasa.dto.VendaResponseDTO;
import com.cefetmg.boinabrasa.entity.Pessoa;
import com.cefetmg.boinabrasa.entity.Produto;
import com.cefetmg.boinabrasa.entity.Venda;
import com.cefetmg.boinabrasa.entity.VendaProduto;
import com.cefetmg.boinabrasa.repository.PessoaRepository;
import com.cefetmg.boinabrasa.repository.ProdutoRepository;
import com.cefetmg.boinabrasa.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final PessoaRepository pessoaRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository, PessoaRepository pessoaRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.pessoaRepository = pessoaRepository;
        this.produtoRepository = produtoRepository;
    }

    // lista as vendas  
    public List<VendaResponseDTO> listarTodos() {
        List<Venda> vendas = vendaRepository.findAll();
        List<VendaResponseDTO> resultado = new ArrayList<>();
        
        // percorre os registros      
        for (int i = 0; i < vendas.size(); i++) {
            Venda venda = vendas.get(i);
            resultado.add(paraResponseDTO(venda));
        }
        return resultado;
    }

    // busca uma unica venda pelo id 
    public VendaResponseDTO buscarPorId(Long id) {
        Venda venda = vendaRepository.findById(id).orElse(null);
        if (venda == null) throw new RuntimeException("Venda não encontrada");
        return paraResponseDTO(venda);
    }

    // registra um novo cupom de venda      
    @Transactional
    public VendaResponseDTO criar(VendaRequestDTO request) {
        Pessoa pessoa = pessoaRepository.findById(request.getIdPessoa()).orElse(null);
        if (pessoa == null) throw new RuntimeException("Pessoa não encontrada");

        Venda venda = new Venda();
        venda.setData(LocalDateTime.now());
        venda.setPessoa(pessoa);

        BigDecimal valorTotal = BigDecimal.ZERO;

        // loops tradicionais para processar cada item do pedido
        for (int i = 0; i < request.getItens().size(); i++) {
            VendaProdutoRequestDTO itemRequest = request.getItens().get(i);
            Produto produto = produtoRepository.findById(itemRequest.getIdProduto()).orElse(null);
            if (produto == null) throw new RuntimeException("Produto não encontrado");

            // trava seguranca impedindo fracionamento de peca ou un
            String un = produto.getUnidade().toLowerCase();
            if (("un".equals(un) || "peça".equals(un) || "pc".equals(un)) 
                    && itemRequest.getQuantidade().remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
                throw new RuntimeException("Produtos vendidos por unidade ou peca não aceitam quantidades fracionadas");
            }

            // comparacao de estoque suficiente  
            if (Boolean.TRUE.equals(produto.getControleEstoque()) 
                    && produto.getQuantidadeEstoque().compareTo(itemRequest.getQuantidade()) < 0) {
                throw new RuntimeException("Estoque insuficiente para o produto '" + produto.getDescricao() + "'.");
            }

            VendaProduto item = new VendaProduto();
            item.setVenda(venda);
            item.setProduto(produto);
            item.setQuantidade(itemRequest.getQuantidade());
            item.setValor(produto.getValorUni());
            venda.getItens().add(item);

            BigDecimal subtotal = produto.getValorUni().multiply(itemRequest.getQuantidade());
            valorTotal = valorTotal.add(subtotal);

            if (Boolean.TRUE.equals(produto.getControleEstoque())) {
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque().subtract(itemRequest.getQuantidade()));
                produtoRepository.save(produto);
            }
        }

        venda.setValorTotal(valorTotal);
        Venda vendaSalva = vendaRepository.save(venda);
        return paraResponseDTO(vendaSalva);
    }

    // remove o registro do cupom e realiza o estorno das quantidades vendidas
    @Transactional
    public void excluir(Long id) {
        Venda venda = vendaRepository.findById(id).orElse(null);
        if (venda == null) throw new RuntimeException("Venda não encontrada");

        // estorno de estoque    
        for (int i = 0; i < venda.getItens().size(); i++) {
            VendaProduto item = venda.getItens().get(i);
            Produto produto = item.getProduto();
            if (Boolean.TRUE.equals(produto.getControleEstoque())) {
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque().add(item.getQuantidade()));
                produtoRepository.save(produto);
            }
        }
        vendaRepository.deleteById(id);
    }

    // converte entidade para dto 
    private VendaResponseDTO paraResponseDTO(Venda venda) {
        List<VendaProdutoResponseDTO> itensDTO = new ArrayList<>();
        
        for (int i = 0; i < venda.getItens().size(); i++) {
            VendaProduto item = venda.getItens().get(i);
            VendaProdutoResponseDTO itemDTO = new VendaProdutoResponseDTO(
                item.getProduto().getId(), 
                item.getProduto().getDescricao(), 
                item.getQuantidade(), 
                item.getValor()
            );
            itensDTO.add(itemDTO);
        }
        
        return new VendaResponseDTO(
            venda.getId(), 
            venda.getData(), 
            venda.getValorTotal(), 
            venda.getPessoa().getId(), 
            venda.getPessoa().getNome(), 
            itensDTO
        );
    }
}