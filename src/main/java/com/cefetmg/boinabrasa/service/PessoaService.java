package com.cefetmg.boinabrasa.service;

import com.cefetmg.boinabrasa.dto.PessoaRequestDTO;
import com.cefetmg.boinabrasa.dto.PessoaResponseDTO;
import com.cefetmg.boinabrasa.entity.Pessoa;
import com.cefetmg.boinabrasa.repository.PessoaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public List<PessoaResponseDTO> listarTodos() {
        List<Pessoa> pessoas = pessoaRepository.findAll();
        return pessoas.stream()
                .map(p -> new PessoaResponseDTO(
                        p.getId(),
                        p.getNome(),
                        p.getEmail(),
                        p.getCpfCnpj(),
                        p.getTipo()))
                .collect(Collectors.toList());
    }

    @Transactional
    public PessoaResponseDTO criar(PessoaRequestDTO request) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(request.getNome());
        pessoa.setEmail(request.getEmail());
        pessoa.setCpfCnpj(request.getCpfCnpj());
        pessoa.setTipo(request.getTipo());

        Pessoa p = pessoaRepository.save(pessoa);

        return new PessoaResponseDTO(p.getId(), p.getNome(), p.getEmail(), p.getCpfCnpj(), p.getTipo());
    }

    @Transactional
    public PessoaResponseDTO alterar(Long id, PessoaRequestDTO request) {
        Pessoa pessoaExistente = pessoaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Pessoa não encontrada com o ID: " + id));

        pessoaExistente.setNome(request.getNome());
        pessoaExistente.setEmail(request.getEmail());
        pessoaExistente.setCpfCnpj(request.getCpfCnpj());
        pessoaExistente.setTipo(request.getTipo());

        Pessoa p = pessoaRepository.save(pessoaExistente);

        return new PessoaResponseDTO(p.getId(), p.getNome(), p.getEmail(), p.getCpfCnpj(), p.getTipo());
    }

    @Transactional
    public void excluir(Long id) {
        if (!pessoaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Não é possível excluir. Pessoa não encontrada com o ID: " + id);
        }
        pessoaRepository.deleteById(id);
    }
}