package br.com.abbatech.crm.financeiro;

import br.com.abbatech.crm.compartilhado.EntidadeAuditavel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "lancamentos_financeiros")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class LancamentoFinanceiro extends EntidadeAuditavel {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, length = 200) private String descricao;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Tipo tipo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Status status;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal valor;
    @Column(nullable = false) private LocalDate vencimento;
    private LocalDate pagamento;
    @Column(nullable = false, length = 100) private String categoria;
    public enum Tipo { RECEITA, DESPESA }
    public enum Status { PENDENTE, PAGO, VENCIDO, CANCELADO }
}
