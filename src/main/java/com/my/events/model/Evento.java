package com.my.events.model;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Schema(hidden = true)
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, name = "name")
    private String nome;
    @Column(nullable = false, name = "location")
    private String localizacao;
    @Column(nullable = false, name = "scheduleDate")
    private LocalDate dataAgendamento;
    @Column(name = "guests")
    @ManyToMany(mappedBy = "eventos")
    public Set<Usuario> convidados = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id) && Objects.equals(nome, evento.nome) && Objects.equals(localizacao, evento.localizacao) && Objects.equals(dataAgendamento, evento.dataAgendamento) && Objects.equals(convidados, evento.convidados);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, localizacao, dataAgendamento, convidados);
    }
}
