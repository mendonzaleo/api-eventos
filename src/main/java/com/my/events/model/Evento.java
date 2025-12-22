package com.my.events.model;

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
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private LocalDate scheduleDate;
    @OneToMany(mappedBy = "evento")
    public Set<Usuario> guests = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return Objects.equals(id, evento.id) && Objects.equals(name, evento.name) && Objects.equals(location, evento.location) && Objects.equals(scheduleDate, evento.scheduleDate) && Objects.equals(guests, evento.guests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, scheduleDate, guests);
    }
}
