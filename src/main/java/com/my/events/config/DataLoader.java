package com.my.events.config;

import com.my.events.model.Usuario;
import com.my.events.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private final UsuarioRepository usuarioRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setName("Leonardo");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456")); // senha criptografada com BCrypt
            admin.setRoles(Collections.singletonList("MANAGERS"));

            usuarioRepository.save(admin);

            System.out.println("✅ Usuário admin criado com sucesso!");
        }
    }
}