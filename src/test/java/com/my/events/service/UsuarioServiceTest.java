package com.my.events.service;

import com.my.events.DTO.UsuarioCreateDTO;
import com.my.events.DTO.UsuarioDTO;
import com.my.events.DTO.UsuarioUpdateDTO;
import com.my.events.model.Usuario;
import com.my.events.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

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

        UsuarioCreateDTO dto = new UsuarioCreateDTO();
        dto.setNome("Leo");
        dto.setUsername("Mendonza");
        dto.setSenha("123456");

        when(encoder.encode("123456")).thenReturn("senhaCriptografada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO resultado = usuarioService.criarUsuario(dto);

        assertNotNull(resultado);
        assertEquals("Leo", resultado.getNome());
        assertEquals("Mendonza", resultado.getUsername());

        verify(encoder).encode("123456");

        verify(usuarioRepository).save(argThat(usuario ->
                usuario.getNome().equals("Leo") &&
                        usuario.getUsername().equals("Mendonza") &&
                        usuario.getSenha().equals("senhaCriptografada")
        ));
    }

    @Test
    void deveListarTodosUsuarios() {

        when(usuarioRepository.findAll())
                .thenReturn(List.of(new Usuario(), new Usuario()));

        List<UsuarioDTO> usuarios = usuarioService.listarTodos();

        assertEquals(2, usuarios.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarUsuarioPorId() {

        Usuario usuario = new Usuario();
        usuario.setId(1);

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(usuario));

        UsuarioDTO resultado = usuarioService.buscarPorId(1);

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
        existente.setNome("Antigo");
        existente.setUsername("antigoUser");

        UsuarioUpdateDTO dto = new UsuarioUpdateDTO();
        dto.setNome("Novo");
        dto.setUsername("novoUser");
        dto.setSenha("123");

        when(usuarioRepository.findById(1))
                .thenReturn(Optional.of(existente));

        when(encoder.encode("123"))
                .thenReturn("senhaCriptografada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO resultado = usuarioService.atualizar(1, dto);

        assertEquals("Novo", resultado.getNome());
        assertEquals("novoUser", resultado.getUsername());

        verify(encoder).encode("123");

        verify(usuarioRepository).save(argThat(usuario ->
                usuario.getNome().equals("Novo") &&
                        usuario.getUsername().equals("novoUser") &&
                        usuario.getSenha().equals("senhaCriptografada")
        ));
    }
    @Test
    void deveDeletarUsuario() {

        when(usuarioRepository.existsById(1)).thenReturn(true);

        boolean resultado = usuarioService.deletar(1);

        assertTrue(resultado);
        verify(usuarioRepository).deleteById(1);
    }

    @Test
    void deveRetornarNotFoundAoDeletarUsuarioInexistente() {

        when(usuarioRepository.existsById(2)).thenReturn(false);

        boolean resultado = usuarioService.deletar(2);

        assertFalse(resultado);
        verify(usuarioRepository, never()).deleteById(any());
    }
}
