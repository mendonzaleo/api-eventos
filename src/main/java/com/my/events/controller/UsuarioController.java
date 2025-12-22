package com.my.events.controller;

import com.my.events.DTO.UsuarioRequestDTO;
import com.my.events.model.Usuario;
import com.my.events.service.UsuarioService;
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
    @PutMapping("/{id}")
    public Usuario atualizarUsuario(@PathVariable Integer id, @RequestBody Usuario usuario) {
    return service.atualizar(id, usuario);
    }
    @GetMapping
    public List<Usuario> listarUsuarios(){
        return service.listarTodos();
    }
    @GetMapping("/{id}")
    public Usuario buscarUsuario(@PathVariable("id") Integer id){
        return service.buscarPorId(id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerUsuario(@PathVariable("id") Integer id){
        return service.deletar(id);
    }
    @PostMapping
    public ResponseEntity<?> criarUsuario(@RequestBody UsuarioRequestDTO usuario) {
        service.criarUsuario(usuario);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Usuário criado com sucesso!");
    }
}
