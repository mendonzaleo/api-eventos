package com.my.events.service;

import com.my.events.DTO.EventoCreateDTO;
import com.my.events.DTO.EventoDTO;
import com.my.events.DTO.UsuarioDTO;
import com.my.events.exception.EventoNaoEncontradoException;
import com.my.events.exception.UsuarioNaoEncontradoException;
import com.my.events.model.Evento;
import com.my.events.model.Usuario;
import com.my.events.repository.EventoRepository;
import com.my.events.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    EventoRepository eventoRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @InjectMocks
    EventoService eventoService;

    private Evento evento;
    private Usuario usuario;

    @BeforeEach
    void setup() {
        evento = new Evento();
        evento.setId(1);
        evento.setNome("Festa");
        evento.setDataAgendamento(LocalDate.now());
        evento.setConvidados(new HashSet<>());

        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNome("Joao");
        usuario.setUsername("Silva");
        usuario.setPerfis(new ArrayList<>());


    }

    @Test
    void deveAdicionarConvidadoComSucesso() {
        when(usuarioRepository.findByUsername("Joao")).thenReturn(usuario);
        when(eventoRepository.findEventoById(1)).thenReturn(evento);

        boolean resultado = eventoService.adicionarConvidado(1, "Joao");

        assertTrue(resultado, "O método deveria retornar true quando o convidado é adicionado");

        assertTrue(evento.getConvidados().contains(usuario), "O convidado deve estar na lista do evento");

        verify(eventoRepository).save(evento);
    }

    @Test
    void naoDeveAdicionarSeUsuarioNaoExistir() {
        when(usuarioRepository.findByUsername("Silva")).thenReturn(null);

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> eventoService.adicionarConvidado(1, "Silva"));
    }

    @Test
    void naoDeveAdicionarSeEventoNaoExistir() {
        when(usuarioRepository.findByUsername("Silva")).thenReturn(usuario);
        when(eventoRepository.findEventoById(1)).thenReturn(null);

        assertThrows(EventoNaoEncontradoException.class,
                () -> eventoService.adicionarConvidado(1, "Silva"));
    }

    @Test
    void deveRemoverConvidadoComSucesso() {
        evento.getConvidados().add(usuario);
        when(eventoRepository.findEventoById(1)).thenReturn(evento);

        boolean resultado = eventoService.removerConvidado(1, "Silva");

        assertTrue(resultado);
        assertFalse(evento.getConvidados().contains(usuario));
    }

    @Test
    void naoDeveRemoverSeEventoNaoExistir() {
        when(eventoRepository.findEventoById(1)).thenReturn(null);

        assertThrows(EventoNaoEncontradoException.class,
                () -> eventoService.removerConvidado(1, "Joao"));
    }

    @Test
    void naoDeveRemoverSeUsuarioNaoEstiverNaLista() {
        when(eventoRepository.findEventoById(1)).thenReturn(evento);

        boolean resultado = eventoService.removerConvidado(1, "Maria");

        assertFalse(resultado);
    }

    @Test
    void deveListarConvidadosQuandoEventoExiste() {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1);
        usuario1.setNome("Alice");
        usuario1.setUsername("alice123");

        Usuario usuario2 = new Usuario();
        usuario2.setId(2);
        usuario2.setNome("Bob");
        usuario2.setUsername("bob123");

        Set<Usuario> convidados = new HashSet<>();
        convidados.add(usuario1);
        convidados.add(usuario2);
        evento.setConvidados(convidados);

        when(eventoRepository.findEventoById(1)).thenReturn(evento);

        List<UsuarioDTO> resultado = eventoService.listarConvidados(1);

        assertNotNull(resultado, "A lista de convidados não deve ser nula");
        assertEquals(2, resultado.size(), "O evento deve ter 2 convidados");

        List<String> nomes = resultado.stream()
                .map(UsuarioDTO::getNome)
                .toList();
        assertTrue(nomes.contains("Alice"), "Deve conter Alice na lista");
        assertTrue(nomes.contains("Bob"), "Deve conter Bob na lista");

        List<String> nomesOrdenados = resultado.stream()
                .map(UsuarioDTO::getNome)
                .sorted()
                .toList();
        assertEquals(nomesOrdenados, nomes.stream().sorted().toList(), "A lista deve estar ordenada");
    }

    @Test
    void deveRetornarListaVaziaQuandoEventoNaoExiste() {
        when(eventoRepository.findEventoById(2)).thenReturn(null);

        List<UsuarioDTO> resultado = eventoService.listarConvidados(2);

        assertNotNull(resultado, "A lista de convidados não deve ser nula mesmo que o evento não exista");
        assertTrue(resultado.isEmpty(), "A lista deve estar vazia quando o evento não existe");
    }

    @Test
    void deveListarConvidadosVaziaQuandoEventoExisteMasNaoTemConvidados() {
        evento.setConvidados(new HashSet<>());

        when(eventoRepository.findEventoById(1)).thenReturn(evento);

        List<UsuarioDTO> resultado = eventoService.listarConvidados(1);

        assertNotNull(resultado, "A lista de convidados não deve ser nula");
        assertTrue(resultado.isEmpty(), "A lista deve estar vazia quando o evento não tem convidados");
    }

    @Test
    void deveCriarEvento() {
        EventoCreateDTO dto = new EventoCreateDTO();
        dto.setNome("Show");
        dto.setLocalizacao("SP");
        dto.setDataAgendamento(LocalDate.now());

        when(eventoRepository.save(any(Evento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EventoDTO criado = eventoService.criarEvento(dto);

        assertEquals("Show", criado.getNome());
        assertEquals("SP", criado.getLocalizacao());
    }

    @Test
    void deveRemoverEventoComSucesso() {
        when(eventoRepository.findEventoById(1)).thenReturn(evento);

        boolean resultado = eventoService.removerEvento(1);

        assertTrue(resultado);
        verify(eventoRepository).deleteById(1);
    }

    @Test
    void naoDeveRemoverEventoSeNaoExistir() {
        when(eventoRepository.findEventoById(1)).thenReturn(null);

        boolean resultado = eventoService.removerEvento(1);

        assertFalse(resultado);
    }
}
