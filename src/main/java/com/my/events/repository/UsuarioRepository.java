package com.my.events.repository;

import com.my.events.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {
    @Query("SELECT e FROM Usuario e JOIN FETCH e.perfis WHERE e.nome= (:nome)")
    public Usuario findByUsername(@Param("nome") String nome);
    boolean existsByNome(String nome);

}
