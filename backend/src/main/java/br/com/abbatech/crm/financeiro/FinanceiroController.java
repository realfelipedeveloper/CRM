package br.com.abbatech.crm.financeiro;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/financeiro")
public class FinanceiroController {
    private final LancamentoFinanceiroRepository repositorio;
    public FinanceiroController(LancamentoFinanceiroRepository repositorio) { this.repositorio = repositorio; }

    @GetMapping("/lancamentos")
    public Page<LancamentoResposta> listar(@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamanho) {
        return repositorio.findAll(PageRequest.of(pagina, Math.min(tamanho, 100), Sort.by("vencimento").descending())).map(LancamentoResposta::de);
    }

    public record LancamentoResposta(UUID id, String descricao, String tipo, String status, BigDecimal valor,
                                     LocalDate vencimento, LocalDate pagamento, String categoria) {
        static LancamentoResposta de(LancamentoFinanceiro l) { return new LancamentoResposta(l.getId(), l.getDescricao(), l.getTipo().name(), l.getStatus().name(), l.getValor(), l.getVencimento(), l.getPagamento(), l.getCategoria()); }
    }
}
