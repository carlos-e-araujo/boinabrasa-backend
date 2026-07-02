package com.cefetmg.boinabrasa.config;

import com.cefetmg.boinabrasa.entity.Pessoa;
import com.cefetmg.boinabrasa.entity.Role;
import com.cefetmg.boinabrasa.entity.TipoPessoa;
import com.cefetmg.boinabrasa.entity.Usuario;
import com.cefetmg.boinabrasa.repository.PessoaRepository;
import com.cefetmg.boinabrasa.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Pessoa pessoa = new Pessoa();
            pessoa.setNome("Administrador Base");
            pessoa.setEmail("admin@admin.admin");
            pessoa.setCpfCnpj("00000000000");
            pessoa.setTipo(TipoPessoa.Gerente);

            Usuario admin = Usuario.builder()
                    .login("admin@admin.admin")
                    .senha(passwordEncoder.encode("@Admin1234"))
                    .role(Role.GERENTE)
                    .pessoa(pessoa)
                    .build();

            usuarioRepository.save(admin);
            System.out.println("Usuário administrador criado: login=admin@admin.admin, senha=@Admin1234");
        }
    }
}
