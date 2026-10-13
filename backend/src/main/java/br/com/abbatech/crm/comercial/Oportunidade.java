package br.com.abbatech.crm.comercial;

import br.com.abbatech.crm.clientes.Cliente;
import br.com.abbatech.crm.compartilhado.EntidadeAuditavel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "oportunidades")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Oportunidade extends EntidadeAuditavel {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(nullable = false, length = 180) private String titulo;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cliente_id") private Cliente cliente;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private Etapa etapa;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal valor;
    @Column(nullable = false) private int probabilidade;
    @Column(nullable = false, length = 160) private String responsavel;
    @Column(name = "previsao_fechamento") private LocalDate previsaoFechamento;
    public enum Etapa { NOVO, QUALIFICACAO, PROPOSTA, NEGOCIACAO, GANHO, PERDIDO }
}

