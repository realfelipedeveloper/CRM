package br.com.abbatech.crm.comercial;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.UUID;

public interface OportunidadeRepository extends JpaRepository<Oportunidade, UUID> {
    @EntityGraph(attributePaths = "cliente")
    List<Oportunidade> findAllByOrderByAtualizadoEmDesc();
}

