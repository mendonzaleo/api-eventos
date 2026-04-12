package com.my.events.controller;

import com.my.events.DTO.UsuarioCreateDTO;
import com.my.events.DTO.UsuarioDTO;
import com.my.events.DTO.UsuarioUpdateDTO;
import com.my.events.exception.UsuarioNaoEncontradoException;
import com.my.events.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UsuarioController {

    @Autowired
    private UsuarioService service;
    @Operation(summary = "Atualizar informações de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PutMapping("/{id}")
    public UsuarioDTO atualizarUsuario(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados a serem atualizados: nome, username e/ou senha")
            @RequestBody UsuarioUpdateDTO usuario) {
        return service.atualizar(id, usuario);
    }

    @Operation(summary = "Listar todos os usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    @GetMapping
    public List<UsuarioDTO> listarUsuarios() {
        return service.listarTodos();
    }

    @Operation(summary = "Buscar um usuário por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuario(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable("id") Integer id) {
        UsuarioDTO retornado = service.buscarPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(retornado);
    }

    @Operation(summary = "Excluir um usuário", security = @SecurityRequirement(name = "bearer-key"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — requer role MANAGERS"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PreAuthorize("hasRole('MANAGERS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerUsuario(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable("id") Integer id) {
        if (service.deletar(id)) {
            return ResponseEntity.status(HttpStatus.OK).body("Usuário removido com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!");
        }
    }

    @Operation(summary = "Criar um novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    })
    @PostMapping
    public ResponseEntity<UsuarioDTO> criarUsuario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do novo usuário. Campos obrigatórios: nome, username e senha")
            @Valid
            @RequestBody UsuarioCreateDTO usuario) {
        UsuarioDTO criado = service.criarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }
}
