package br.com.abbatech.crm.comercial;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/comercial")
public class ComercialController {
    private final LeadRepository leads;
    private final OportunidadeRepository oportunidades;
    public ComercialController(LeadRepository leads, OportunidadeRepository oportunidades) { this.leads = leads; this.oportunidades = oportunidades; }

    @GetMapping("/leads")
    public List<LeadResposta> listarLeads() { return leads.findAll().stream().map(LeadResposta::de).toList(); }

    @PostMapping("/leads") @ResponseStatus(HttpStatus.CREATED)
    public LeadResposta criarLead(@Valid @RequestBody LeadEntrada e) {
        return LeadResposta.de(leads.save(Lead.builder().nome(e.nome()).email(e.email()).telefone(e.telefone()).origem(e.origem())
            .status(Lead.Status.NOVO).responsavel(e.responsavel()).valorEstimado(e.valorEstimado()).build()));
    }

    @GetMapping("/pipeline")
    public List<OportunidadeResposta> pipeline() { return oportunidades.findAllByOrderByAtualizadoEmDesc().stream().map(OportunidadeResposta::de).toList(); }

    @PatchMapping("/pipeline/{id}/etapa")
    public OportunidadeResposta mover(@PathVariable UUID id, @RequestBody Map<String, String> corpo) {
        var oportunidade = oportunidades.findById(id).orElseThrow(() -> new EntityNotFoundException("Oportunidade não encontrada."));
        oportunidade.setEtapa(Oportunidade.Etapa.valueOf(Objects.requireNonNull(corpo.get("etapa")).toUpperCase(Locale.ROOT)));
        return OportunidadeResposta.de(oportunidades.save(oportunidade));
    }

    public record LeadEntrada(@NotBlank String nome, @Email String email, String telefone, @NotBlank String origem,
                              String responsavel, @NotNull @PositiveOrZero BigDecimal valorEstimado) {}
    public record LeadResposta(UUID id, String nome, String origem, String status, String responsavel, BigDecimal valorEstimado) {
        static LeadResposta de(Lead l) { return new LeadResposta(l.getId(), l.getNome(), l.getOrigem(), l.getStatus().name(), l.getResponsavel(), l.getValorEstimado()); }
    }
    public record OportunidadeResposta(UUID id, String titulo, String cliente, String etapa, BigDecimal valor,
                                       int probabilidade, String responsavel, LocalDate previsaoFechamento) {
        static OportunidadeResposta de(Oportunidade o) { return new OportunidadeResposta(o.getId(), o.getTitulo(), o.getCliente() == null ? null : o.getCliente().getNome(), o.getEtapa().name(), o.getValor(), o.getProbabilidade(), o.getResponsavel(), o.getPrevisaoFechamento()); }
    }
}

