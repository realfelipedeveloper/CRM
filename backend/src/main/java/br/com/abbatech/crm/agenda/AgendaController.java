package br.com.abbatech.crm.agenda;

import br.com.abbatech.crm.clientes.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agenda")
public class AgendaController {
    private final AgendamentoRepository agendamentos;
    private final ClienteRepository clientes;
    public AgendaController(AgendamentoRepository agendamentos, ClienteRepository clientes) { this.agendamentos = agendamentos; this.clientes = clientes; }

    @GetMapping
    public List<AgendamentoResposta> listar(@RequestParam OffsetDateTime inicio, @RequestParam OffsetDateTime fim) {
        return agendamentos.findByInicioBetweenOrderByInicio(inicio, fim).stream().map(AgendamentoResposta::de).toList();
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) @Transactional
    public AgendamentoResposta criar(@Valid @RequestBody AgendamentoEntrada e) {
        if (!e.fim().isAfter(e.inicio())) throw new IllegalStateException("O término deve ser posterior ao início.");
        if (agendamentos.existeConflito(e.profissional(), e.inicio(), e.fim())) throw new IllegalStateException("O profissional já possui um compromisso nesse horário.");
        var cliente = clientes.findById(e.clienteId()).orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
        var item = Agendamento.builder().cliente(cliente).profissional(e.profissional()).servico(e.servico()).inicio(e.inicio())
            .fim(e.fim()).status(Agendamento.Status.AGENDADO).valor(e.valor()).build();
        return AgendamentoResposta.de(agendamentos.save(item));
    }

    public record AgendamentoEntrada(@NotNull UUID clienteId, @NotBlank String profissional, @NotBlank String servico,
                                      @NotNull OffsetDateTime inicio, @NotNull OffsetDateTime fim,
                                      @NotNull @PositiveOrZero BigDecimal valor) {}
    public record AgendamentoResposta(UUID id, String cliente, String profissional, String servico, OffsetDateTime inicio,
                                      OffsetDateTime fim, String status, BigDecimal valor) {
        static AgendamentoResposta de(Agendamento a) { return new AgendamentoResposta(a.getId(), a.getCliente().getNome(), a.getProfissional(), a.getServico(), a.getInicio(), a.getFim(), a.getStatus().name(), a.getValor()); }
    }
}

