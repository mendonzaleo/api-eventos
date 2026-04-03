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
            return String.format("Usuário %s não encontrado!", nomeConvidado);
        }
        Evento evento = repository.findEventoById(idEvento);
        if (evento == null){
            return String.format("Evento com ID %s não existe!", idEvento);
        }else{
            evento.guests.add(usuario);
            usuarioRepository.save(usuario);
            repository.save(evento);
            return String.format("%s adicionado a lista de convidados!", usuario.getUsername());
        }
    }

    public boolean removerConvidado(Integer idEvento, String nomeUsuario){
        Evento eventoSelecionado = repository.findEventoById(idEvento);
        if (eventoSelecionado == null){
            return false;
        }
        Usuario removido = eventoSelecionado.guests.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(nomeUsuario))
                .findFirst()
                .orElse(null);

        if(removido == null){
            return false;
        }else{
            eventoSelecionado.getGuests().remove(removido);
            repository.save(eventoSelecionado);
            return true;
        }


    }

    public List<String> listarConvidados(Integer id){
        Evento eventoConvidados = repository.findEventoById(id);
        if(eventoConvidados == null){
            return null;
        }else {
            return eventoConvidados.getGuests().stream()
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
        Evento eventoRemovido = repository.findEventoById(id);
        if(eventoRemovido == null){
            return false;
        }else{
            repository.deleteById(id);
            return true;
        }
    }
}
