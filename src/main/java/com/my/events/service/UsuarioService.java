package com.my.events.service;

import com.my.events.DTO.UsuarioCreateDTO;
import com.my.events.DTO.UsuarioDTO;
import com.my.events.DTO.UsuarioUpdateDTO;
import com.my.events.exception.EventoDadosInvalidosException;
import com.my.events.model.Usuario;
import com.my.events.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder encoder;

    private UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername()
        );
    }

    public UsuarioDTO criarUsuario(UsuarioCreateDTO dto) {
        Usuario usuarioCriado = new Usuario();
        if(dto.getNome() == null || dto.getNome().isBlank()){
            throw new EventoDadosInvalidosException("O nome do usuário é obrigatório!");
        }
        usuarioCriado.setNome(dto.getNome());
        if (dto.getUsername() == null || dto.getUsername().isBlank()){
            throw new EventoDadosInvalidosException("O username é obrigatório!");
        }
        usuarioCriado.setUsername(dto.getUsername());
        if (dto.getSenha() == null || dto.getSenha().isBlank()){
            throw new EventoDadosInvalidosException("A senha é obrigatória!");
        }
        usuarioCriado.setSenha(encoder.encode(dto.getSenha()));

        if (usuarioCriado.getPerfis() == null || usuarioCriado.getPerfis().isEmpty()) {
            usuarioCriado.setPerfis(new ArrayList<>(List.of("ROLE_USER")));
        }
        Usuario savedUsuario = usuarioRepository.save(usuarioCriado);
        return new UsuarioDTO(
                savedUsuario.getId(),
                savedUsuario.getNome(),
                savedUsuario.getUsername()
        );
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public UsuarioDTO buscarPorId(Integer id) {
        Usuario usuarioProcurado = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return toDTO(usuarioProcurado);
    }

    public UsuarioDTO atualizar(Integer id, UsuarioUpdateDTO dto) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (dto.getNome() != null) {
            existente.setNome(dto.getNome());
        }

        if (dto.getUsername() != null) {
            existente.setUsername(dto.getUsername());
        }

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            existente.setSenha(encoder.encode(dto.getSenha()));
        }

        return toDTO(usuarioRepository.save(existente));
    }

    public Boolean deletar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            return false;
        }else {
            usuarioRepository.deleteById(id);
            return true;
        }


    }
}
