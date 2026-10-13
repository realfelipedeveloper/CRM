package br.com.abbatech.crm.bi;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MetricasService {
    private final JdbcTemplate jdbc;
    public MetricasService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public DashboardResumo dashboard() {
        var hoje = LocalDate.now();
        var inicioMes = hoje.withDayOfMonth(1);
        BigDecimal receita = decimal("select coalesce(sum(valor),0) from lancamentos_financeiros where tipo='RECEITA' and status='PAGO' and pagamento between ? and ?", inicioMes, hoje);
        BigDecimal despesas = decimal("select coalesce(sum(valor),0) from lancamentos_financeiros where tipo='DESPESA' and status='PAGO' and pagamento between ? and ?", inicioMes, hoje);
        BigDecimal previsto = decimal("select coalesce(sum(valor),0) from lancamentos_financeiros where tipo='RECEITA' and status='PENDENTE' and vencimento >= ?", hoje);
        Long clientesAtivos = numero("select count(*) from clientes where status='ATIVO'");
        Long leadsAbertos = numero("select count(*) from leads where status not in ('CONVERTIDO','PERDIDO')");
        Long agendamentosHoje = numero("select count(*) from agendamentos where inicio::date=? and status not in ('CANCELADO','FALTOU')", hoje);
        BigDecimal pipeline = decimal("select coalesce(sum(valor),0) from oportunidades where etapa not in ('GANHO','PERDIDO')");
        BigDecimal taxaConversao = decimal("select case when count(*)=0 then 0 else round(100.0*count(*) filter(where status='CONVERTIDO')/count(*),1) end from leads");
        var serie = jdbc.queryForList("""
            select d::date as data, coalesce(sum(l.valor) filter(where l.tipo='RECEITA' and l.status='PAGO'),0) as receita
            from generate_series(current_date - interval '6 days', current_date, interval '1 day') d
            left join lancamentos_financeiros l on l.pagamento=d::date group by d order by d
            """);
        var etapas = jdbc.queryForList("select etapa, count(*) as quantidade, coalesce(sum(valor),0) as valor from oportunidades group by etapa order by etapa");
        var proximos = jdbc.queryForList("""
            select a.inicio, c.nome as cliente, a.servico, a.profissional, a.status
            from agendamentos a join clientes c on c.id=a.cliente_id
            where a.inicio >= now() and a.status in ('AGENDADO','CONFIRMADO') order by a.inicio limit 5
            """);
        return new DashboardResumo(receita, despesas, receita.subtract(despesas), previsto, clientesAtivos, leadsAbertos,
            agendamentosHoje, pipeline, taxaConversao, serie, etapas, proximos);
    }

    private BigDecimal decimal(String sql, Object... args) { return jdbc.queryForObject(sql, BigDecimal.class, args); }
    private Long numero(String sql, Object... args) { return jdbc.queryForObject(sql, Long.class, args); }

    public record DashboardResumo(BigDecimal receitaRecebida, BigDecimal despesasPagas, BigDecimal resultadoOperacional,
        BigDecimal receitaPrevista, long clientesAtivos, long leadsAbertos, long agendamentosHoje,
        BigDecimal valorPipeline, BigDecimal taxaConversao, List<Map<String, Object>> receitaUltimosSeteDias,
        List<Map<String, Object>> pipelinePorEtapa, List<Map<String, Object>> proximosAgendamentos) {}
}

