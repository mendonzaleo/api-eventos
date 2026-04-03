package com.my.events.controller;

import com.my.events.DTO.EventoRequestDTO;
import com.my.events.model.Evento;
import com.my.events.repository.EventoRepository;
import com.my.events.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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
    EventoService service;
    @Operation(summary = "Exibir lista de convidados de um evento")
    @Parameter(description = "Informar ID do evento")
    @GetMapping("/convidados/{idEvento}")
    public ResponseEntity<?> listarConvidados(@PathVariable("idEvento") Integer idEvento){
        if(service.listarConvidados(idEvento).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento sem convidados!");
        }else{
            return ResponseEntity.ok(service.listarConvidados(idEvento));
        }
    }
    @Operation(summary = "Excluir pessoa da lista de convidados")
    @Parameter(description = "Informar ID do evento e nome da pessoa a ser removido da lista de convidados")
    @DeleteMapping("/convidados/{id}")
    public ResponseEntity<?> removerConvidados(@PathVariable("id")Integer idEvento,@RequestParam String nome){
        boolean removido = service.removerConvidado(idEvento, nome);
        if(removido == false){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Convidado com nome %s não localizado!", nome));
        }else{
            return ResponseEntity.ok(String.format("Convidado %s removido com sucesso!", nome));
        }
    }
    @Operation(summary = "Endpoint para a adicionar uma pessoa a lista de convidados do evento")
    @Parameter(description="Informar ID do evento e nome da pessoa a ser convidada")
    @PostMapping("/convidados/{id}")
    public ResponseEntity<?> adicionarConvidados(@PathVariable("id")Integer idEvento, @RequestParam String nomeUsuario) {
        String resposta = service.adicionarConvidado(idEvento, nomeUsuario);
        return ResponseEntity.ok(resposta);
    }
    @Operation(summary = "Lista eventos")
    @GetMapping
    public ResponseEntity<?> listarEventos(){
        if(service.listarEventos().isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não há eventos existentes!");
        }else{
            return ResponseEntity.ok(service.listarEventos());
        }
    }
    @Operation(summary = "Listar eventos consultando por data de agendamento")
    @Parameter(description = "Informar a data de consulta, formato 'dd-MM-yyyy'")
    @GetMapping("/{data}")
    public ResponseEntity<?> listaPorAgendamento(@PathVariable LocalDate data){
        List<Evento> eventosAgendados = service.listarPorAgendamento(data);
        if(eventosAgendados.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não existem eventos agendados para essa data!");
        }else{
            return ResponseEntity.ok(eventosAgendados);
        }
    }
    @Operation(summary = "Criar evento")
    @Parameter(description = "Parâmetros obrigatórios: nome, localizacao e dataAgendamento")
    @PostMapping
    public ResponseEntity<?> criarEvento(@RequestBody EventoRequestDTO evento){
        service.criarEvento(evento);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Evento salvo com sucesso!");
    }
    @Operation(summary = "Excluir evento")
    @Parameter(description = "Informar ID do evento")
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
