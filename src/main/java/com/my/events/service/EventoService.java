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
    EventoRepository eventoRepository;
    @Autowired
    UsuarioRepository usuarioRepository;

    public String adicionarConvidado(Integer idEvento, String nomeUsuario){
        Usuario usuario = usuarioRepository.findByUsername(nomeUsuario);
        Evento evento = eventoRepository.findEventoById(idEvento);
        if(usuario == null) {
            return String.format("Usuário %s não encontrado!", nomeUsuario);
        }
        if (evento == null){
            return String.format("Evento com ID %s não existe!", idEvento);
        }else{
            usuario.getEventos().add(evento);
            evento.getGuests().add(usuario);
            usuarioRepository.save(usuario);
            eventoRepository.save(evento);
            return String.format("%s adicionado a lista de convidados!", usuario.getUsername());
        }
    }

    public boolean removerConvidado(Integer idEvento, String nomeUsuario){
        Evento eventoSelecionado = eventoRepository.findEventoById(idEvento);
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
        Evento eventoConvidados = eventoRepository.findEventoById(id);
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
        List<Evento> eventosListados = eventoRepository.findAll().stream()
                .sorted(Comparator.comparing(Evento::getScheduleDate).reversed())
                .toList();
        return eventosListados;
    }
    public List<Evento> listarPorAgendamento(LocalDate data){
        return eventoRepository.findByDataAgendamento(data);
    }
    public Evento criarEvento(EventoRequestDTO dto){
        Evento evento = new Evento();
        evento.setName(dto.getNome());
        evento.setLocation(dto.getLocalizacao());
        evento.setScheduleDate(dto.getDataAgendamento());

        return eventoRepository.save(evento);
    }
    public boolean removerEvento(Integer id){
        if(eventoRepository.findEventoById(id) == null){
            return false;
        }else{
            eventoRepository.deleteById(id);
            return true;
        }
    }
}
