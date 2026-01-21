package com.my.events.service;

import com.my.events.DTO.EventoRequestDTO;
import com.my.events.exception.EventoDadosInvalidosException;
import com.my.events.model.Evento;
import com.my.events.model.Usuario;
import com.my.events.repository.EventoRepository;
import com.my.events.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Service
public class EventoService {

    Evento evento =  new Evento();
    @Autowired
    EventoRepository repository;
    @Autowired
    UsuarioRepository usuarioRepository;

    public String adicionarConvidado(Integer idEvento, String nomeConvidado){
        Usuario usuario = usuarioRepository.findByUsername(nomeConvidado);

        if(usuario == null) {
            return String.format("Usuário %s não encontrado.", nomeConvidado);
        }
        Evento evento = repository.findEventoById(idEvento);
        if (evento == null){
            return String.format("Evento não encontrado com o id %s", idEvento);
        }else{
            evento.guests.add(usuario);
            repository.save(evento);
            return String.format("%s adicionado a lista de convidados!", usuario.getUsername());
        }
    }

    public boolean removerConvidado(Integer idEvento, String nomeUsuario){
        Evento eventoSelecionado = repository.findEventoById(idEvento);
        Usuario removido = eventoSelecionado.guests.stream()
                .filter(u -> u.getName().equalsIgnoreCase(nomeUsuario))
                .findFirst()
                .orElse(null);

                if(removido == null){
                    return false;
                }else{
                    evento.guests.remove(removido);
                    return true;
                }
    }

    public List<String> listarConvidados(Integer id){
        Evento eventoConvidados = repository.findEventoById(id);
        if(evento == null){
            return null;
        }else {
            return eventoConvidados.guests.stream()
                    .map(Usuario::getName)
                    .sorted()
                    .toList();
        }
    }

    public List<Evento> listarEventos(){
        List<Evento> eventosListados = repository.findAll().stream()
                .sorted(Comparator.comparing(Evento::getScheduleDate).reversed())
                .toList();
        return eventosListados;
    }
    public List<Evento> listarPorAgendamento(LocalDate data){
        return repository.findByDataAgendamento(data);
    }
    public Evento criarEvento(EventoRequestDTO dto){
        Evento evento = new Evento();
        evento.setName(dto.getNome());
        evento.setLocation(dto.getLocalizacao());
        evento.setScheduleDate(dto.getDataAgendamento());

        return repository.save(evento);
    }
    public boolean removerEvento(Integer id){
        if(repository.findEventoById(id) == null){
            return false;
        }else{
            repository.deleteById(id);
            return true;
        }
    }
}
