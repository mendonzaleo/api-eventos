package com.my.events.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.events.DTO.UsuarioCreateDTO;
import com.my.events.DTO.UsuarioDTO;
import com.my.events.DTO.UsuarioUpdateDTO;
import com.my.events.config.TestSecurityConfig;
import com.my.events.exception.GlobalExceptionHandler;
import com.my.events.exception.UsuarioDadosInvalidosException;
import com.my.events.exception.UsuarioNaoEncontradoException;
import com.my.events.security.JwtAuthFilter;
import com.my.events.security.SecurityDatabaseService;
import com.my.events.service.JwtService;
import com.my.events.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = {UsuarioController.class, GlobalExceptionHandler.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        )
)
@Import(TestSecurityConfig.class)
class UsuarioControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UsuarioService service;

    @MockitoBean
    SecurityDatabaseService securityDatabaseService;

    private UsuarioDTO usuarioDTO;
    private UsuarioCreateDTO usuarioCreateDTO;

    @BeforeEach
    void setup() {
        usuarioDTO = new UsuarioDTO(1, "Leo", "lmendonza");

        usuarioCreateDTO = new UsuarioCreateDTO();
        usuarioCreateDTO.setNome("Leo");
        usuarioCreateDTO.setUsername("lmendonza");
        usuarioCreateDTO.setSenha("123456");
    }

    @TestConfiguration
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestSecurityConfig {}

    // -------------------------------------------------------------------------
    // GET /usuarios
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser
    void deveListarTodosOsUsuariosComSucesso() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(usuarioDTO));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Leo"))
                .andExpect(jsonPath("$[0].username").value("lmendonza"));

        verify(service).listarTodos();
    }

    @Test
    @WithMockUser
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void deveRetornar401QuandoNaoAutenticadoAoListarUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void deveBuscarUsuarioPorIdComSucesso() throws Exception {
        when(service.buscarPorId(1)).thenReturn(usuarioDTO);

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Leo"))
                .andExpect(jsonPath("$.username").value("lmendonza"));

        verify(service).buscarPorId(1);
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoUsuarioNaoEncontrado() throws Exception {
        when(service.buscarPorId(99))
                .thenThrow(new UsuarioNaoEncontradoException("Usuário não encontrado"));

        mockMvc.perform(get("/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuário não encontrado"));
    }

    // -------------------------------------------------------------------------
    // POST /usuarios
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser
    void deveCriarUsuarioComSucesso() throws Exception {
        when(service.criarUsuario(any(UsuarioCreateDTO.class))).thenReturn(usuarioDTO);

        mockMvc.perform(post("/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Leo"))
                .andExpect(jsonPath("$.username").value("lmendonza"));

        verify(service).criarUsuario(any(UsuarioCreateDTO.class));
    }

    @Test
    @WithMockUser
    void deveRetornar400QuandoDadosDoUsuarioSaoInvalidos() throws Exception {
        when(service.criarUsuario(any(UsuarioCreateDTO.class)))
                .thenThrow(new UsuarioDadosInvalidosException("O nome do usuário é obrigatório!"));

        mockMvc.perform(post("/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("O nome do usuário é obrigatório!"));
    }

    // -------------------------------------------------------------------------
    // PUT /usuarios/{id}
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser
    void deveAtualizarUsuarioComSucesso() throws Exception {
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO();
        updateDTO.setNome("Leo Atualizado");
        updateDTO.setUsername("lmendonza_v2");

        UsuarioDTO atualizado = new UsuarioDTO(1, "Leo Atualizado", "lmendonza_v2");

        when(service.atualizar(eq(1), any(UsuarioUpdateDTO.class))).thenReturn(atualizado);

        mockMvc.perform(put("/usuarios/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Leo Atualizado"))
                .andExpect(jsonPath("$.username").value("lmendonza_v2"));

        verify(service).atualizar(eq(1), any(UsuarioUpdateDTO.class));
    }

    @Test
    @WithMockUser
    void deveRetornar404QuandoUsuarioNaoEncontradoAoAtualizar() throws Exception {
        UsuarioUpdateDTO updateDTO = new UsuarioUpdateDTO();
        updateDTO.setNome("Novo Nome");

        when(service.atualizar(eq(99), any(UsuarioUpdateDTO.class)))
                .thenThrow(new UsuarioNaoEncontradoException("Usuário não encontrado"));

        mockMvc.perform(put("/usuarios/99")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuário não encontrado"));
    }

    // -------------------------------------------------------------------------
    // DELETE /usuarios/{id}
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "MANAGERS")
    void deveRemoverUsuarioComSucesso() throws Exception {
        when(service.deletar(1)).thenReturn(true);

        mockMvc.perform(delete("/usuarios/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário removido com sucesso!"));

        verify(service).deletar(1);
    }

    @Test
    @WithMockUser(roles = "MANAGERS")
    void deveRetornar404QuandoUsuarioNaoEncontradoAoRemover() throws Exception {
        when(service.deletar(99)).thenReturn(false);

        mockMvc.perform(delete("/usuarios/99").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Usuário não encontrado!"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveRetornar403QuandoUsuarioSemPerfilManagersTentaRemover() throws Exception {
        mockMvc.perform(delete("/usuarios/1").with(csrf()))
                .andExpect(status().isForbidden());

        verify(service, never()).deletar(any());
    }
}
