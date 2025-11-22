package com.my.events.service;

import com.my.events.model.Evento;
import com.my.events.model.Usuario;
import com.my.events.repository.EventoRepository;
import com.my.events.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventoService {
    @Autowired
    Evento evento;
    @Autowired
    private EventoRepository repository;
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
            return String.format("%s adicionado a lista de convidados!", usuario);
        }
    }

    public boolean removerConvidado(String nomeUsuario){
        Usuario removido = evento.guests.stream()
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

    public List<String> listarConvidados(){
        return evento.guests.stream()
                .map(Usuario::getName)
                .sorted()
                .toList();
    }

    public List<Evento> listarEventos(){
        return repository.findAll();
    }

    public List<Evento> listarPorAgendamento(LocalDate data){
        return repository.findByDataAgendamento(data);
    }
}
