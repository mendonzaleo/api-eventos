package com.my.events.service;

import com.my.events.DTO.UsuarioResponse;
import com.my.events.model.Usuario;
import com.my.events.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder encoder;
    public ResponseEntity<UsuarioResponse> createUser(Usuario usuario){
        String pass = usuario.getPassword();
        //criptografando antes de salvar no banco
        usuario.setPassword(encoder.encode(pass));

        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            usuario.getRoles().add("ROLE_USER");
        }
        Usuario savedUsuario = usuarioRepository.save(usuario);

        UsuarioResponse response = new UsuarioResponse("Usuário criado com sucesso!", savedUsuario);

        return ResponseEntity.ok(response);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public Usuario atualizar(Integer id, Usuario usuario) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // atualiza os campos necessários
        existente.setName(usuario.getName());
        existente.setUsername(usuario.getUsername());

        // se senha foi enviada, reencoda
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            existente.setPassword(encoder.encode(usuario.getPassword()));
        }

        // se roles vieram, atualiza; senão mantém
        if (usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
            existente.setRoles(usuario.getRoles());
        }

        return usuarioRepository.save(existente);
    }
    public ResponseEntity<UsuarioResponse> deletar(Integer id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuarioRepository.deleteById(id);
                    UsuarioResponse response = new UsuarioResponse("Usuário deletado com sucesso!", usuario);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    UsuarioResponse response = new UsuarioResponse("Usuário não encontrado!", null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }
}
