package com.my.events.repository;

import com.my.events.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
    // Busca um evento pelo ID usando JPQL
    @Query("SELECT e FROM Evento e WHERE e.id = :id")
    Evento findEventoById(@Param("id") Integer id);

    // Consulta eventos por data de agendamento
    @Query("SELECT e FROM Evento e WHERE e.dataAgendamento = :data")
    List<Evento> findByDataAgendamento(@Param("data") LocalDate data);

    // Se quiser verificar se existe um evento nessa data
    boolean existsByDataAgendamento(LocalDate data);
}
