package com.my.events.controller;

import com.my.events.DTO.UsuarioResponse;
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
    public Usuario update(@PathVariable Integer id, @RequestBody Usuario usuario) {
    return service.atualizar(id, usuario);
    }
    @GetMapping
    public List<Usuario> getAll(){
        return service.listarTodos();
    }
    @GetMapping("/{id}")
    public Usuario getOne(@PathVariable("id") Integer id){
        return service.buscarPorId(id);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse> delete(@PathVariable("id") Integer id){
        return service.deletar(id);
    }
    @PostMapping
    public ResponseEntity<UsuarioResponse> criarUsuario(@RequestBody Usuario usuario) {
        ResponseEntity<UsuarioResponse> salvo = service.createUser(usuario);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UsuarioResponse("Usuário criado com sucesso!", usuario));
    }
}
