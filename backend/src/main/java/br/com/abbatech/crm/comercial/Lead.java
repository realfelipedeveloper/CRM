package br.com.abbatech.crm.comercial;

import br.com.abbatech.crm.compartilhado.EntidadeAuditavel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "leads")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Lead extends EntidadeAuditavel {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 160) private String nome;
    @Column(length = 200) private String email;
    @Column(length = 30) private String telefone;
    @Column(nullable = false, length = 80) private String origem;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private Status status;
    @Column(length = 160) private String responsavel;
    @Column(name = "valor_estimado", nullable = false, precision = 15, scale = 2) private BigDecimal valorEstimado;
    public enum Status { NOVO, CONTATO, QUALIFICADO, CONVERTIDO, PERDIDO }
}

