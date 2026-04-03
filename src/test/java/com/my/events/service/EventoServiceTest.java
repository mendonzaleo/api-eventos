package com.my.events.service;

import com.my.events.DTO.EventoRequestDTO;
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
        evento.setName("Festa");
        evento.setScheduleDate(LocalDate.now());
        evento.setGuests(new HashSet<>());

        usuario = new Usuario();
        usuario.setId(1);
        usuario.setName("Joao");
        usuario.setUsername("Silva");
        usuario.setRoles(new ArrayList<>());
    }

    @Test
    void deveAdicionarConvidadoComSucesso() {
        when(usuarioRepository.findByUsername("Joao")).thenReturn(usuario);
        when(eventoRepository.findEventoById(1)).thenReturn(evento);

        String resultado = eventoService.adicionarConvidado(1, "Joao");

        assertEquals("Silva adicionado a lista de convidados!", resultado);
        assertTrue(evento.getGuests().contains(usuario));

        verify(usuarioRepository).save(usuario);
        verify(eventoRepository).save(evento);
    }

    @Test
    void naoDeveAdicionarSeUsuarioNaoExistir() {
        when(usuarioRepository.findByUsername("Silva")).thenReturn(null);

        String resultado = eventoService.adicionarConvidado(1, "Silva");

        assertEquals("Usuário Silva não encontrado!", resultado);
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void naoDeveAdicionarSeEventoNaoExistir() {
        when(usuarioRepository.findByUsername("Joao")).thenReturn(usuario);
        when(eventoRepository.findEventoById(1)).thenReturn(null);

        String resultado = eventoService.adicionarConvidado(1, "Joao");

        assertEquals("Evento com ID 1 não existe!", resultado);
    }

    @Test
    void deveRemoverConvidadoComSucesso() {
        evento.getGuests().add(usuario);
        when(eventoRepository.findEventoById(1)).thenReturn(evento);

        boolean resultado = eventoService.removerConvidado(1, "Silva");

        assertTrue(resultado);
        assertFalse(evento.getGuests().contains(usuario));
    }

    @Test
    void naoDeveRemoverSeEventoNaoExistir() {
        when(eventoRepository.findEventoById(1)).thenReturn(null);

        boolean resultado = eventoService.removerConvidado(1, "Joao");

        assertFalse(resultado);
    }

    @Test
    void naoDeveRemoverSeUsuarioNaoEstiverNaLista() {
        when(eventoRepository.findEventoById(1)).thenReturn(evento);

        boolean resultado = eventoService.removerConvidado(1, "Maria");

        assertFalse(resultado);
    }

    @Test
    void deveListarConvidadosOrdenados() {
        Usuario u1 = new Usuario();
        u1.setName("Carlos");
        u1.setId(1);

        Usuario u2 = new Usuario();
        u2.setName("Ana");
        u2.setId(2);

        evento.setGuests(Set.of(u1, u2));

        when(eventoRepository.findEventoById(1)).thenReturn(evento);

        List<String> nomes = eventoService.listarConvidados(1);

        assertEquals(List.of("Ana", "Carlos"), nomes);
    }

    @Test
    void deveRetornarNullSeEventoNaoExistir() {
        when(eventoRepository.findEventoById(1)).thenReturn(null);

        List<String> resultado = eventoService.listarConvidados(1);

        assertNull(resultado);
    }

    @Test
    void deveCriarEvento() {
        EventoRequestDTO dto = new EventoRequestDTO();
        dto.setNome("Show");
        dto.setLocalizacao("SP");
        dto.setDataAgendamento(LocalDate.now());

        when(eventoRepository.save(any(Evento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Evento criado = eventoService.criarEvento(dto);

        assertEquals("Show", criado.getName());
        assertEquals("SP", criado.getLocation());
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
