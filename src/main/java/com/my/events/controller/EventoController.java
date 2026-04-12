package com.my.events.controller;

import com.my.events.DTO.EventoCreateDTO;
import com.my.events.DTO.EventoDTO;
import com.my.events.DTO.UsuarioDTO;
import com.my.events.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/eventos")
@Tag(name = "Eventos", description = "Gerenciamento de eventos e convidados")
public class EventoController {

    @Autowired
    EventoService service;
    @Operation(summary = "Listar convidados de um evento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de convidados retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento sem convidados")
    })
    @GetMapping("/convidados/{idEvento}")
    public ResponseEntity<?> listarConvidados(
            @Parameter(description = "ID do evento", required = true)
            @PathVariable("idEvento") Integer idEvento) {
        List<UsuarioDTO> listados = service.listarConvidados(idEvento);
        if (listados.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento sem convidados!");
        } else {
            return ResponseEntity.ok(listados);
        }
    }
    @Operation(summary = "Remover convidado de um evento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Convidado removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Convidado não encontrado")
    })
    @DeleteMapping("/convidados/{id}")
    public ResponseEntity<?> removerConvidados(
            @Parameter(description = "ID do evento", required = true)
            @PathVariable("id") Integer idEvento,
            @Parameter(description = "Nome do convidado a ser removido", required = true)
            @RequestParam String nome) {
        boolean removido = service.removerConvidado(idEvento, nome);
        if (!removido) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Convidado com nome %s não localizado!", nome));
        } else {
            return ResponseEntity.ok(String.format("Convidado %s removido com sucesso!", nome));
        }
    }
    @Operation(summary = "Adicionar convidado a um evento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Convidado adicionado com sucesso")
    })
    @PostMapping("/convidados/{id}")
    public ResponseEntity<?> adicionarConvidados(@Parameter(description = "ID do evento", required = true)
                                                     @PathVariable("id") Integer idEvento,
                                                 @Parameter(description = "Username do usuário a ser convidado", required = true)
                                                     @RequestParam String nomeUsuario) {
        service.adicionarConvidado(idEvento, nomeUsuario);
        return ResponseEntity
                .status(HttpStatus.OK).body(String.format("Convidado %s adicionado a lista de convidados do evento!", nomeUsuario));
    }
    @Operation(summary = "Listar todos os eventos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de eventos retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum evento encontrado")
    })
    @GetMapping
    public ResponseEntity<?> listarEventos(){
        List<EventoDTO> listados = service.listarEventos();
        if(listados.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não há eventos existentes!");
        }else{
            return ResponseEntity.ok(listados);
        }
    }
    @Operation(summary = "Listar eventos por data de agendamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eventos encontrados para a data informada"),
            @ApiResponse(responseCode = "404", description = "Nenhum evento encontrado para essa data")
    })
    @GetMapping("/{data}")
    public ResponseEntity<?> listaPorAgendamento(
            @Parameter(description = "Data de agendamento no formato yyyy-MM-dd", required = true)
            @PathVariable LocalDate data) {
        List<EventoDTO> eventosAgendados = service.listarPorAgendamento(data);
        if (eventosAgendados.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não existem eventos agendados para essa data!");
        } else {
            return ResponseEntity.ok(eventosAgendados);
        }
    }
    @Operation(summary = "Criar um novo evento")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Evento criado com sucesso")})
    @PostMapping
    public ResponseEntity<?> criarEvento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do evento. Campos obrigatórios: nome, localizacao e dataAgendamento")
            @Valid
            @RequestBody EventoCreateDTO evento){
        EventoDTO dtoRetornado = service.criarEvento(evento);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dtoRetornado);
    }
    @Operation(summary = "Excluir evento")
    @Parameter(description = "Informar ID do evento")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerEvento(
            @Parameter(description = "ID do evento", required = true)
            @PathVariable Integer id) {
        Boolean removido = service.removerEvento(id);
        if (removido) {
            return ResponseEntity.ok("Evento removido com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Não foi possível localizar esse evento!");
        }
    }
}
