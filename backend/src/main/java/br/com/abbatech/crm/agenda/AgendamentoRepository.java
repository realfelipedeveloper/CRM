package br.com.abbatech.crm.agenda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
    @EntityGraph(attributePaths = "cliente")
    List<Agendamento> findByInicioBetweenOrderByInicio(OffsetDateTime inicio, OffsetDateTime fim);

    @Query("select count(a) > 0 from Agendamento a where a.profissional = :profissional " +
           "and a.status not in ('CANCELADO', 'FALTOU') and a.inicio < :fim and a.fim > :inicio")
    boolean existeConflito(@Param("profissional") String profissional,
                           @Param("inicio") OffsetDateTime inicio,
                           @Param("fim") OffsetDateTime fim);
}

