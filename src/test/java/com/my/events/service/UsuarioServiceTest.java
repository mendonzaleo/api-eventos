package com.my.events.service;

import com.my.events.DTO.UsuarioRequestDTO;
import com.my.events.model.Usuario;
import com.my.events.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarUsuario() {

        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setName("Leo");
        dto.setUsername("Mendonza");
        dto.setPassword("123456");
        dto.setRoles(List.of("ROLE_USER"));

        when(encoder.encode("123456")).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.criarUsuario(dto);

        assertNotNull(resultado);
        assertEquals("Leo", resultado.getName());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void deveListarTodosUsuarios() {

        when(usuarioRepository.findAll())
                .thenReturn(List.of(new Usuario(), new Usuario()));

        List<Usuario> usuarios = usuarioService.listarTodos();

        assertEquals(2, usuarios.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarUsuarioPorId() {

        Usuario usuario = new Usuario();
        usuario.setId(1);

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1);

        assertEquals(1, resultado.getId());
    }

    @Test
    void deveLancarExcecaoSeUsuarioNaoEncontrado() {

        when(usuarioRepository.findById(99))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> usuarioService.buscarPorId(99));

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    void deveAtualizarUsuario() {

        Usuario existente = new Usuario();
        existente.setId(1);
        existente.setName("Antigo");

        Usuario atualizado = new Usuario();
        atualizado.setName("Novo");
        atualizado.setPassword("123");

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(existente));
        when(encoder.encode("123"))
                .thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.atualizar(1, atualizado);

        assertEquals("Novo", resultado.getName());
        verify(usuarioRepository).save(existente);
    }

    @Test
    void deveDeletarUsuario() {

        when(usuarioRepository.existsById(1)).thenReturn(true);

        ResponseEntity<?> response = usuarioService.deletar(1);

        assertEquals(200, response.getStatusCode().value());
        verify(usuarioRepository).deleteById(1);
    }

    @Test
    void deveRetornarNotFoundAoDeletarUsuarioInexistente() {

        when(usuarioRepository.existsById(2)).thenReturn(false);

        ResponseEntity<?> response = usuarioService.deletar(2);

        assertEquals(404, response.getStatusCode().value());
        verify(usuarioRepository, never()).deleteById(any());
    }
}
