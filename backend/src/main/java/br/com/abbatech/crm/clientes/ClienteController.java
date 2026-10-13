package br.com.abbatech.crm.clientes;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteRepository repositorio;
    public ClienteController(ClienteRepository repositorio) { this.repositorio = repositorio; }

    @GetMapping
    public Page<ClienteResposta> listar(@RequestParam(defaultValue = "") String busca,
                                        @RequestParam(defaultValue = "0") int pagina,
                                        @RequestParam(defaultValue = "20") int tamanho) {
        var paginacao = PageRequest.of(Math.max(pagina, 0), Math.min(Math.max(tamanho, 1), 100), Sort.by("criadoEm").descending());
        return repositorio.findByNomeContainingIgnoreCase(busca, paginacao).map(ClienteResposta::de);
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public ClienteResposta criar(@Valid @RequestBody ClienteEntrada entrada) {
        var cliente = Cliente.builder().nome(entrada.nome()).email(entrada.email()).telefone(entrada.telefone())
            .documento(entrada.documento()).tags(entrada.tags()).status(Cliente.Status.ATIVO)
            .consentimentoMarketing(entrada.consentimentoMarketing()).build();
        return ClienteResposta.de(repositorio.save(cliente));
    }

    @PutMapping("/{id}")
    public ClienteResposta atualizar(@PathVariable UUID id, @Valid @RequestBody ClienteEntrada entrada) {
        var cliente = repositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
        cliente.setNome(entrada.nome()); cliente.setEmail(entrada.email()); cliente.setTelefone(entrada.telefone());
        cliente.setDocumento(entrada.documento()); cliente.setTags(entrada.tags()); cliente.setConsentimentoMarketing(entrada.consentimentoMarketing());
        return ClienteResposta.de(repositorio.save(cliente));
    }

    public record ClienteEntrada(@NotBlank(message = "Informe o nome.") String nome,
                                  @Email(message = "Informe um e-mail válido.") String email,
                                  String telefone, String documento, String tags, boolean consentimentoMarketing) {}
    public record ClienteResposta(UUID id, String nome, String email, String telefone, String documento, String tags,
                                  String status, boolean consentimentoMarketing) {
        static ClienteResposta de(Cliente c) { return new ClienteResposta(c.getId(), c.getNome(), c.getEmail(), c.getTelefone(), c.getDocumento(), c.getTags(), c.getStatus().name(), c.isConsentimentoMarketing()); }
    }
}

