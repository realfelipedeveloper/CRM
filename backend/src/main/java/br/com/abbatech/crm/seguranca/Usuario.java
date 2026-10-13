package br.com.abbatech.crm.seguranca;

import br.com.abbatech.crm.compartilhado.EntidadeAuditavel;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Usuario extends EntidadeAuditavel {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 160)
    private String nome;
    @Column(nullable = false, unique = true, length = 200)
    private String email;
    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40)
    private Perfil perfil;
    @Column(nullable = false)
    private boolean ativo;

    public enum Perfil { ADMINISTRADOR, GESTOR, COMERCIAL, RECEPCAO, FINANCEIRO, PROFISSIONAL }
}

