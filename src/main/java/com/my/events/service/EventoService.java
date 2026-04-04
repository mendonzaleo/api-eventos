package com.my.events.service;

import com.my.events.DTO.EventoCreateDTO;
import com.my.events.DTO.EventoDTO;
import com.my.events.DTO.EventoUpdateDTO;
import com.my.events.DTO.UsuarioDTO;
import com.my.events.exception.EventoDadosInvalidosException;
import com.my.events.exception.EventoNaoEncontradoException;
import com.my.events.exception.UsuarioNaoEncontradoException;
import com.my.events.model.Evento;
import com.my.events.model.Usuario;
import com.my.events.repository.EventoRepository;
import com.my.events.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class EventoService {

    @Autowired
    EventoRepository repository;
    @Autowired
    UsuarioRepository usuarioRepository;

    private EventoDTO toDTO(Evento evento) {
        EventoDTO dto = new EventoDTO();
        dto.setNome(evento.getNome());
        dto.setLocalizacao(evento.getLocalizacao());
        dto.setDataAgendamento(evento.getDataAgendamento());
        return dto;
    }

    private UsuarioDTO toUsuarioDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername()
        );
    }

    public Boolean adicionarConvidado(Integer idEvento, String nomeConvidado){
        Usuario usuario = usuarioRepository.findByUsername(nomeConvidado);

        if(usuario == null) {
            throw new UsuarioNaoEncontradoException(String.format("Usuário %s não encontrado.", nomeConvidado));
        }
        Evento eventoSelecionado = repository.findEventoById(idEvento);
        if (eventoSelecionado == null){
            throw new EventoNaoEncontradoException("Evento com id " + idEvento + " não encontrado");
        }else{
            eventoSelecionado.getConvidados().add(usuario);
            repository.save(eventoSelecionado);
            return true;
        }
    }

    public boolean removerConvidado(Integer idEvento, String nomeUsuario){
        Evento eventoSelecionado = repository.findEventoById(idEvento);
        if (eventoSelecionado == null){
            throw new EventoNaoEncontradoException("Evento com id " + idEvento + " não encontrado");
        }
        Usuario removido = eventoSelecionado.getConvidados().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(nomeUsuario))
                .findFirst()
                .orElse(null);

                if(removido == null){
                    return false;
                }else{
                    eventoSelecionado.getConvidados().remove(removido);
                    return true;
                }
    }

    public List<UsuarioDTO> listarConvidados(Integer id){
        Evento eventoConvidados = repository.findEventoById(id);
        if(eventoConvidados == null){
            return Collections.emptyList();
        }else {
            return eventoConvidados.getConvidados().stream()
                    .map(this::toUsuarioDTO)
                    .sorted(Comparator.comparing(UsuarioDTO::getNome))
                    .toList();
        }
    }

    public List<EventoDTO> listarEventos(){
        List<Evento> eventosListados = repository.findAll().stream()
                .sorted(Comparator.comparing(Evento::getDataAgendamento).reversed()).toList();
        List<EventoDTO> eventosConvertidos = eventosListados.stream()
                .map(this::toDTO)
                .toList();
        return eventosConvertidos;
    }

    public List<EventoDTO> listarPorAgendamento(LocalDate data) {
        return repository.findByDataAgendamento(data).stream()
                .map(this::toDTO)
                .toList();
    }

    public EventoDTO criarEvento(EventoCreateDTO dto){
        Evento evento = new Evento();
        if(dto.getNome() == null || dto.getNome().isBlank()) {
            throw new EventoDadosInvalidosException("Nome do evento é obrigatório!");
        }
        evento.setNome(dto.getNome());

        if(dto.getLocalizacao() == null || dto.getLocalizacao().isBlank()) {
            throw new EventoDadosInvalidosException("Localização é obrigatória!");
        }
        evento.setLocalizacao(dto.getLocalizacao());

        if(dto.getDataAgendamento() == null) {
            throw new EventoDadosInvalidosException("Data de agendamento do evento é obrigatória!");
        }
        if(dto.getDataAgendamento().isBefore(LocalDate.now())) {
            throw new EventoDadosInvalidosException("A data do evento deve ser futura!");
        }
        evento.setDataAgendamento(dto.getDataAgendamento());

        Evento eventoSalvo = repository.save(evento);
        EventoDTO dtoRetorno = new EventoDTO();
        dtoRetorno.setNome(eventoSalvo.getNome());
        dtoRetorno.setLocalizacao(eventoSalvo.getLocalizacao());
        dtoRetorno.setDataAgendamento(eventoSalvo.getDataAgendamento());
        return dtoRetorno;
    }

    public EventoDTO atualizarEvento(Integer idEvento, EventoUpdateDTO dto){
        Evento evento = repository.findEventoById(idEvento);

        if(dto.getNome() != null) {
            evento.setNome(dto.getNome());
        }
        if(dto.getLocalizacao() != null) {
            evento.setLocalizacao(dto.getLocalizacao());
        }
        if(dto.getDataAgendamento() != null && dto.getDataAgendamento().isAfter(LocalDate.now())) {
            evento.setDataAgendamento(dto.getDataAgendamento());
        }
        repository.save(evento);
        EventoDTO eventoAtualizado = toDTO(evento);
        return eventoAtualizado;
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
