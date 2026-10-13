package br.com.abbatech.crm.clientes;

import br.com.abbatech.crm.compartilhado.EntidadeAuditavel;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "clientes")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Cliente extends EntidadeAuditavel {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 160)
    private String nome;
    @Column(length = 200)
    private String email;
    @Column(length = 30)
    private String telefone;
    @Column(length = 30)
    private String documento;
    @Column(length = 500)
    private String tags;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private Status status;
    @Column(name = "consentimento_marketing", nullable = false)
    private boolean consentimentoMarketing;

    public enum Status { ATIVO, INATIVO, BLOQUEADO }
}

