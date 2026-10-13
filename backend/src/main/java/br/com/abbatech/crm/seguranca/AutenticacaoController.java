package br.com.abbatech.crm.seguranca;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {
    private final AuthenticationManager autenticador;
    private final JwtEncoder encoder;
    private final UsuarioRepository usuarios;
    private final long expiracao;

    public AutenticacaoController(AuthenticationManager autenticador, JwtEncoder encoder, UsuarioRepository usuarios,
                                  @Value("${crm.jwt.expiracao-minutos}") long expiracao) {
        this.autenticador = autenticador;
        this.encoder = encoder;
        this.usuarios = usuarios;
        this.expiracao = expiracao;
    }

    @PostMapping("/entrar")
    public TokenResposta entrar(@Valid @RequestBody LoginEntrada entrada) {
        autenticador.authenticate(new UsernamePasswordAuthenticationToken(entrada.email(), entrada.senha()));
        var usuario = usuarios.findByEmailIgnoreCaseAndAtivoTrue(entrada.email()).orElseThrow();
        Instant agora = Instant.now();
        var claims = JwtClaimsSet.builder().issuer("crm-aurea").issuedAt(agora)
            .expiresAt(agora.plus(expiracao, ChronoUnit.MINUTES)).subject(usuario.getEmail())
            .claim("nome", usuario.getNome()).claim("perfil", usuario.getPerfil().name()).build();
        var cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();
        return new TokenResposta(token, "Bearer", expiracao * 60, usuario.getNome(), usuario.getPerfil().name());
    }

    public record LoginEntrada(@NotBlank @Email String email, @NotBlank String senha) {}
    public record TokenResposta(String token, String tipo, long expiraEmSegundos, String nome, String perfil) {}
}
