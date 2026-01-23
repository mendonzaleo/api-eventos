package com.my.events.controller;

import com.my.events.DTO.UsuarioRequestDTO;
import com.my.events.model.Usuario;
import com.my.events.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;
    @Operation(summary = "Alterar informações de usuário")
    @PutMapping("/{id}")
    public Usuario atualizarUsuario(@PathVariable Integer id, @RequestBody Usuario usuario) {
    return service.atualizar(id, usuario);
    }
    @Operation(summary = "Exibir os usuários")
    @GetMapping
    public List<Usuario> listarUsuarios(){
        return service.listarTodos();
    }
    @Operation(summary = "Buscar um usuário específico")
    @Parameter(description = "Informar o ID do usuário")
    @GetMapping("/{id}")
    public Usuario buscarUsuario(@PathVariable("id") Integer id){
        return service.buscarPorId(id);
    }
    @Operation(summary = "Excluir um usuário")
    @Parameter(description = "Informar o ID do usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerUsuario(@PathVariable("id") Integer id){
        return service.deletar(id);
    }
    @Operation(summary = "Buscar um usuário específico")
    @Parameter(description = "Parâmetros: nome, username, senha")
    @PostMapping
    public ResponseEntity<?> criarUsuario(@RequestBody UsuarioRequestDTO usuario) {
        service.criarUsuario(usuario);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Usuário criado com sucesso!");
    }
}
