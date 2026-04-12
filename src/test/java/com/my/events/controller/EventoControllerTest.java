package com.my.events.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.events.DTO.EventoCreateDTO;
import com.my.events.DTO.EventoDTO;
import com.my.events.DTO.UsuarioDTO;
import com.my.events.config.TestSecurityConfig;
import com.my.events.exception.EventoDadosInvalidosException;
import com.my.events.exception.EventoNaoEncontradoException;
import com.my.events.exception.GlobalExceptionHandler;
import com.my.events.exception.UsuarioNaoEncontradoException;
import com.my.events.security.JwtAuthFilter;
import com.my.events.security.SecurityDatabaseService;
import com.my.events.service.EventoService;
import com.my.events.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = {EventoController.class, GlobalExceptionHandler.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        )
)
@Import(TestSecurityConfig.class)
class EventoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    EventoService service;

    @MockitoBean
    SecurityDatabaseService securityDatabaseService;

    private EventoDTO eventoDTO;
    private EventoCreateDTO eventoCreateDTO;

    @BeforeEach
    void setup() {
        eventoDTO = new EventoDTO();
        eventoDTO.setNome("Show");
        eventoDTO.setLocalizacao("SP");
        eventoDTO.setDataAgendamento(LocalDate.of(2025, 10, 20));

        eventoCreateDTO = new EventoCreateDTO();
        eventoCreateDTO.setNome("Show");
        eventoCreateDTO.setLocalizacao("SP");
        eventoCreateDTO.setDataAgendamento(LocalDate.of(2026, 10, 20));
    }

    // -------------------------------------------------------------------------
    // GET /eventos
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser
    void deveListarEventosComSucesso() throws Exception {
        when(service.listarEventos()).thenReturn(List.of(eventoDTO));

        mockMvc.perform(get("/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Show"))
                .andExpect(jsonPath("$[0].localizacao").value("SP"));

        verify(service).listarEventos();
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoNaoHaEventos() throws Exception {
        when(service.listarEventos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/eventos"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Não há eventos existentes!"));
    }

    @Test
    void deveRetornar401QuandoNaoAutenticadoAoListarEventos() throws Exception {
        mockMvc.perform(get("/eventos"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // GET /eventos/{data}
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser
    void deveListarEventosPorDataComSucesso() throws Exception {
        when(service.listarPorAgendamento(LocalDate.of(2025, 10, 20)))
                .thenReturn(List.of(eventoDTO));

        mockMvc.perform(get("/eventos/2025-10-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Show"));
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoNaoHaEventosNaData() throws Exception {
        when(service.listarPorAgendamento(LocalDate.of(2025, 10, 20)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/eventos/2025-10-20"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Não existem eventos agendados para essa data!"));
    }

    // -------------------------------------------------------------------------
    // POST /eventos
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser
    void deveCriarEventoComSucesso() throws Exception {
        when(service.criarEvento(any(EventoCreateDTO.class))).thenReturn(eventoDTO);

        mockMvc.perform(post("/eventos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventoCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Show"))
                .andExpect(jsonPath("$.localizacao").value("SP"));

        verify(service).criarEvento(any(EventoCreateDTO.class));
    }

    @Test
    @WithMockUser
    void deveRetornar400QuandoDadosDoEventoSaoInvalidos() throws Exception {
        when(service.criarEvento(any(EventoCreateDTO.class)))
                .thenThrow(new EventoDadosInvalidosException("Nome do evento é obrigatório!"));

        mockMvc.perform(post("/eventos")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventoCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Nome do evento é obrigatório!"));
    }

    // -------------------------------------------------------------------------
    // DELETE /eventos/{id}
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser
    void deveRemoverEventoComSucesso() throws Exception {
        when(service.removerEvento(1)).thenReturn(true);

        mockMvc.perform(delete("/eventos/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Evento removido com sucesso!"));

        verify(service).removerEvento(1);
    }

    @Test
    @WithMockUser
    void deveRetornar400QuandoEventoNaoEncontradoAoRemover() throws Exception {
        when(service.removerEvento(99)).thenReturn(false);

        mockMvc.perform(delete("/eventos/99").with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Não foi possível localizar esse evento!"));
    }

    // -------------------------------------------------------------------------
    // GET /eventos/convidados/{idEvento}
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser
    void deveListarConvidadosComSucesso() throws Exception {
        UsuarioDTO convidado = new UsuarioDTO(1, "Alice", "alice123");

        when(service.listarConvidados(1)).thenReturn(List.of(convidado));

        mockMvc.perform(get("/eventos/convidados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Alice"))
                .andExpect(jsonPath("$[0].username").value("alice123"));
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoEventoNaoTemConvidados() throws Exception {
        when(service.listarConvidados(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/eventos/convidados/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Evento sem convidados!"));
    }

    // -------------------------------------------------------------------------
    // POST /eventos/convidados/{id}
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser
    void deveAdicionarConvidadoComSucesso() throws Exception {
        when(service.adicionarConvidado(1, "alice123")).thenReturn(true);

        mockMvc.perform(post("/eventos/convidados/1")
                        .with(csrf())
                        .param("nomeUsuario", "alice123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Convidado alice123 adicionado a lista de convidados do evento!"));

        verify(service).adicionarConvidado(1, "alice123");
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoUsuarioNaoExisteAoAdicionarConvidado() throws Exception {
        doThrow(new UsuarioNaoEncontradoException("Usuário inexistente não encontrado."))
                .when(service).adicionarConvidado(1, "inexistente");

        mockMvc.perform(post("/eventos/convidados/1")
                        .with(csrf())
                        .param("nomeUsuario", "inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoEventoNaoExisteAoAdicionarConvidado() throws Exception {
        doThrow(new EventoNaoEncontradoException("Evento com id 99 não encontrado"))
                .when(service).adicionarConvidado(99, "alice123");

        mockMvc.perform(post("/eventos/convidados/99")
                        .with(csrf())
                        .param("nomeUsuario", "alice123"))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // DELETE /eventos/convidados/{id}
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser
    void deveRemoverConvidadoComSucesso() throws Exception {
        when(service.removerConvidado(1, "alice123")).thenReturn(true);

        mockMvc.perform(delete("/eventos/convidados/1")
                        .with(csrf())
                        .param("nome", "alice123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Convidado alice123 removido com sucesso!"));
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoConvidadoNaoEncontradoAoRemover() throws Exception {
        when(service.removerConvidado(1, "inexistente")).thenReturn(false);

        mockMvc.perform(delete("/eventos/convidados/1")
                        .with(csrf())
                        .param("nome", "inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Convidado com nome inexistente não localizado!"));
    }
}
