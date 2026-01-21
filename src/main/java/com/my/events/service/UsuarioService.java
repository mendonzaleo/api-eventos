package com.my.events.service;

import com.my.events.DTO.UsuarioRequestDTO;
import com.my.events.model.Evento;
import com.my.events.model.Usuario;
import com.my.events.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder encoder;

    public Usuario criarUsuario(UsuarioRequestDTO usuario) {

        Usuario usuarioCriado = new Usuario();
        usuarioCriado.setName(usuario.getName());
        usuarioCriado.setPassword(usuario.getPassword());
        usuarioCriado.setUsername(usuario.getUsername());
        String pass = usuario.getPassword();
        //criptografando antes de salvar no banco
        usuario.setPassword(encoder.encode(pass));

        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            usuario.getRoles().add("ROLE_USER");
        }
        Usuario savedUsuario = usuarioRepository.save(usuarioCriado);
        return usuarioCriado;
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

    public ResponseEntity<?> deletar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Usuário não encontrado!");
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.ok("Usuário deletado com sucesso!");


    }
}
