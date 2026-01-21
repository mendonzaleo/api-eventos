package com.my.events.controller;

import com.my.events.DTO.EventoRequestDTO;
import com.my.events.model.Evento;
import com.my.events.repository.EventoRepository;
import com.my.events.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    EventoRepository repository;
    @Autowired
    EventoService service;

    @GetMapping("/convidados/{idEvento}")
    public ResponseEntity<?> listarConvidados(@PathVariable("idEvento") Integer idEvento){
        if(service.listarConvidados(idEvento).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento sem convidados!");
        }else{
            return ResponseEntity.ok(service.listarConvidados(idEvento));
        }
    }
    @DeleteMapping("/convidados/{id}")
    public ResponseEntity<?> removerConvidados(@PathVariable("id")Integer idEvento,@RequestParam String nome){
        boolean removido = service.removerConvidado(idEvento, nome);
        if(removido == false){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Convidado com nome %s não localizado!", nome));
        }else{
            return ResponseEntity.ok(String.format("Convidado %s removido com sucesso!", nome));
        }
    }
    @PostMapping("/convidados/{id}")
    public ResponseEntity<?> adicionarConvidados(@PathVariable("id")Integer idEvento, @RequestParam String nomeUsuario) {
        String resposta = service.adicionarConvidado(idEvento, nomeUsuario);
        return ResponseEntity.ok(resposta);
    }
    @GetMapping
    public ResponseEntity<?> listarEventos(){
        if(service.listarEventos().isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não há eventos existentes!");
        }else{
            return ResponseEntity.ok(service.listarEventos());
        }
    }
    @GetMapping("/{data}")
    public ResponseEntity<?> listaPorAgendamento(@PathVariable LocalDate data){
        List<Evento> eventosAgendados = service.listarPorAgendamento(data);
        if(eventosAgendados.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não existem eventos agendados para essa data!");
        }else{
            return ResponseEntity.ok(eventosAgendados);
        }
    }
    @PostMapping
    public ResponseEntity<?> criarEvento(@RequestBody EventoRequestDTO evento){
        service.criarEvento(evento);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Evento salvo com sucesso!");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerEvento(@PathVariable Integer id){
        Boolean removido = service.removerEvento(id);
        if(removido){
            return ResponseEntity.ok("Evento removido com sucesso!");
        }else{
            return ResponseEntity.badRequest().body("Não foi possível localizar esse evento!");
        }
    }
}
