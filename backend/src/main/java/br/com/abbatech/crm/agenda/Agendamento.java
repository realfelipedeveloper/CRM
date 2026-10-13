package br.com.abbatech.crm.agenda;

import br.com.abbatech.crm.clientes.Cliente;
import br.com.abbatech.crm.compartilhado.EntidadeAuditavel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "agendamentos")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Agendamento extends EntidadeAuditavel {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "cliente_id") private Cliente cliente;
    @Column(nullable = false, length = 160) private String profissional;
    @Column(nullable = false, length = 180) private String servico;
    @Column(nullable = false) private OffsetDateTime inicio;
    @Column(nullable = false) private OffsetDateTime fim;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private Status status;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal valor;
    @Version private long versao;
    public enum Status { AGENDADO, CONFIRMADO, CONCLUIDO, CANCELADO, FALTOU }
}

