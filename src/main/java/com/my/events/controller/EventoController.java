package com.my.events.controller;

import com.my.events.model.Usuario;
import com.my.events.repository.EventoRepository;
import com.my.events.service.EventoService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    EventoRepository repository;
    @Autowired
    EventoService service;
    @Autowired
    Usuario usuario;

    @GetMapping
    public ResponseEntity<?> listarConvidados(){
        if(service.listarConvidados().isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Evento sem convidados!");
        }else{
            return ResponseEntity.ok(service.listarConvidados());
        }
    }
    @DeleteMapping
    public ResponseEntity<?> removerConvidados(String nome){
        boolean removido = service.removerConvidado(nome);
        if(removido == false){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(String.format("Convidado com nome %s não localizado!", nome));
        }else{
            return ResponseEntity
                    .ok(String.format("Convidado %s removido com sucesso!", nome));
        }
    }
    @PostMapping
    public ResponseEntity<?> adicionarConvidados(Integer idEvento, String nomeConvidado) {
        service.adicionarConvidado(idEvento, nomeConvidado);
        return ResponseEntity.ok("Convidado adicionado a lista com sucesso!");
    }
}
