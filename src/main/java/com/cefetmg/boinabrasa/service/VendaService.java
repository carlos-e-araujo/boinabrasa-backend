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

    public VendaService(VendaRepository vendaRepository,
                         PessoaRepository pessoaRepository,
                         ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.pessoaRepository = pessoaRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<VendaResponseDTO> listarTodos() {
        List<Venda> vendas = vendaRepository.findAll();

        List<VendaResponseDTO> resultado = new ArrayList<>();
        for (Venda venda : vendas) {
            resultado.add(paraResponseDTO(venda));
        }

        return resultado;
    }

    public VendaResponseDTO buscarPorId(Long id) {
        Venda venda = vendaRepository.findById(id).orElse(null);

        if (venda == null) {
            throw new RuntimeException("Venda não encontrada com o ID: " + id);
        }

        return paraResponseDTO(venda);
    }

    // se falhar no meio desfaz tudo que ja foi gravado por conta do transactional (igual operacao de banco, faz ou nao faz)
    @Transactional
    public VendaResponseDTO criar(VendaRequestDTO request) {
        Pessoa pessoa = pessoaRepository.findById(request.getIdPessoa()).orElse(null);

        if (pessoa == null) {
            throw new RuntimeException("Pessoa não encontrada com o ID: " + request.getIdPessoa());
        }

        Venda venda = new Venda();
        venda.setData(LocalDateTime.now());
        venda.setPessoa(pessoa);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (VendaProdutoRequestDTO itemRequest : request.getItens()) {

            Produto produto = produtoRepository.findById(itemRequest.getIdProduto()).orElse(null);

            if (produto == null) {
                throw new RuntimeException("Produto não encontrado com o ID: " + itemRequest.getIdProduto());
            }

            // so valida estoque se controleEstoque = true (unidade)
            // produto por kg nao tem saldo de unidade pra checar
            if (Boolean.TRUE.equals(produto.getControleEstoque())
                    && produto.getQuantidadeEstoque() < itemRequest.getQuantidade().intValue()) {
                throw new RuntimeException(
                        "Estoque insuficiente para o produto '" + produto.getDescricao() +
                        "'. Disponível: " + produto.getQuantidadeEstoque() +
                        ", solicitado: " + itemRequest.getQuantidade());
            }

            // preco travado no momento da venda não pega o preço atual depois
            VendaProduto item = new VendaProduto();
            item.setVenda(venda);
            item.setProduto(produto);
            item.setQuantidade(itemRequest.getQuantidade());
            item.setValor(produto.getValorUni());

            venda.getItens().add(item);

            BigDecimal subtotal = produto.getValorUni().multiply(itemRequest.getQuantidade());
            valorTotal = valorTotal.add(subtotal);

            // Baixa de estoque so pra produto por unidade
            if (Boolean.TRUE.equals(produto.getControleEstoque())) {
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemRequest.getQuantidade().intValue());
                produtoRepository.save(produto);
            }
        }

        venda.setValorTotal(valorTotal);

        // cascade = ALL na Entity salva os itens junto sem precisar de save() separado
        Venda vendaSalva = vendaRepository.save(venda);

        return paraResponseDTO(vendaSalva);
    }

    @Transactional
    public void excluir(Long id) {
        Venda venda = vendaRepository.findById(id).orElse(null);

        if (venda == null) {
            throw new RuntimeException("Não é possível excluir. Venda não encontrada com o ID: " + id);
        }

        // devolve estoque so dos produtos por unidade
        for (VendaProduto item : venda.getItens()) {
            Produto produto = item.getProduto();
            if (Boolean.TRUE.equals(produto.getControleEstoque())) {
                produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade().intValue());
                produtoRepository.save(produto);
            }
        }

        vendaRepository.deleteById(id);
    }

    // metodo privado para tipo traduzir para DTO
    private VendaResponseDTO paraResponseDTO(Venda venda) {
        List<VendaProdutoResponseDTO> itensDTO = new ArrayList<>();

        for (VendaProduto item : venda.getItens()) {
            VendaProdutoResponseDTO dto = new VendaProdutoResponseDTO(
                    item.getProduto().getId(),
                    item.getProduto().getDescricao(),
                    item.getQuantidade(),
                    item.getValor()
            );
            itensDTO.add(dto);
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