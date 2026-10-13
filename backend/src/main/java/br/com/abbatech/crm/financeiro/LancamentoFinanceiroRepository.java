package br.com.abbatech.crm.financeiro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface LancamentoFinanceiroRepository extends JpaRepository<LancamentoFinanceiro, UUID> {
    @Query("select coalesce(sum(l.valor), 0) from LancamentoFinanceiro l where l.tipo = :tipo and l.status = 'PAGO' and l.pagamento between :inicio and :fim")
    BigDecimal totalPagoNoPeriodo(LancamentoFinanceiro.Tipo tipo, LocalDate inicio, LocalDate fim);

    @Query("select coalesce(sum(l.valor), 0) from LancamentoFinanceiro l where l.tipo = 'RECEITA' and l.status = 'PENDENTE' and l.vencimento between :inicio and :fim")
    BigDecimal receitaPrevista(LocalDate inicio, LocalDate fim);
}

