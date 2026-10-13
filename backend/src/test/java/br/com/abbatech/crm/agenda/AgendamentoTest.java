package br.com.abbatech.crm.agenda;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgendamentoTest {
    @Test
    void devePreservarPrecisaoMonetariaEDuracao() {
        var inicio = OffsetDateTime.parse("2026-10-13T10:00:00-03:00");
        var agendamento = Agendamento.builder().inicio(inicio).fim(inicio.plusMinutes(50))
            .valor(new BigDecimal("189.90")).status(Agendamento.Status.AGENDADO).build();
        assertTrue(agendamento.getFim().isAfter(agendamento.getInicio()));
        assertEquals(new BigDecimal("189.90"), agendamento.getValor());
    }
}

