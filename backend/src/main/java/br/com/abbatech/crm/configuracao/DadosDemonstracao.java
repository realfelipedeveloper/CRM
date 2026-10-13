package br.com.abbatech.crm.configuracao;

import br.com.abbatech.crm.agenda.*;
import br.com.abbatech.crm.clientes.*;
import br.com.abbatech.crm.comercial.*;
import br.com.abbatech.crm.financeiro.*;
import br.com.abbatech.crm.seguranca.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

@Configuration
@Profile("demo")
public class DadosDemonstracao {
    @Bean
    CommandLineRunner carregar(UsuarioRepository usuarios, ClienteRepository clientes, LeadRepository leads,
        OportunidadeRepository oportunidades, AgendamentoRepository agenda,
        LancamentoFinanceiroRepository financeiro, PasswordEncoder encoder) {
        return args -> {
            if (usuarios.count() == 0) usuarios.save(Usuario.builder().nome("Marina Andrade").email("admin@clinica.local")
                .senhaHash(encoder.encode("Admin@123")).perfil(Usuario.Perfil.ADMINISTRADOR).ativo(true).build());
            if (clientes.count() > 0) return;

            var lista = clientes.saveAll(List.of(
                cliente("Ana Beatriz Costa", "ana@exemplo.com", "(11) 99811-2030", "VIP, recorrente"),
                cliente("Juliana Martins", "juliana@exemplo.com", "(11) 99118-8440", "Facial"),
                cliente("Renata Oliveira", "renata@exemplo.com", "(11) 98870-3321", "Corporal"),
                cliente("Camila Nunes", "camila@exemplo.com", "(11) 99776-9102", "Novo"),
                cliente("Patrícia Lima", "patricia@exemplo.com", "(11) 98445-1142", "Recorrente"),
                cliente("Fernanda Alves", "fernanda@exemplo.com", "(11) 99220-7634", "Indicação")
            ));

            leads.saveAll(List.of(
                lead("Beatriz Rocha", "Instagram", Lead.Status.NOVO, "Mariana", "850"),
                lead("Larissa Melo", "Google", Lead.Status.QUALIFICADO, "Mariana", "1400"),
                lead("Sofia Cardoso", "Indicação", Lead.Status.CONTATO, "Paulo", "620"),
                lead("Isabela Reis", "Instagram", Lead.Status.CONVERTIDO, "Paulo", "2200"),
                lead("Maria Clara Dias", "Evento", Lead.Status.PERDIDO, "Mariana", "980")
            ));

            oportunidades.saveAll(List.of(
                oportunidade("Protocolo facial premium", lista.get(0), Oportunidade.Etapa.PROPOSTA, "2400", 65, "Mariana", 8),
                oportunidade("Pacote de drenagem", lista.get(1), Oportunidade.Etapa.NEGOCIACAO, "1680", 80, "Paulo", 4),
                oportunidade("Tratamento corporal", lista.get(2), Oportunidade.Etapa.QUALIFICACAO, "3200", 45, "Mariana", 15),
                oportunidade("Revitalização facial", lista.get(3), Oportunidade.Etapa.NOVO, "890", 25, "Paulo", 10),
                oportunidade("Pacote pós-operatório", lista.get(4), Oportunidade.Etapa.GANHO, "2850", 100, "Mariana", -2)
            ));

            var agora = OffsetDateTime.now().withMinute(0).withSecond(0).withNano(0);
            agenda.saveAll(List.of(
                agendamento(lista.get(0), "Dra. Helena", "Harmonização facial", proximo(agora, 1, 9), 60, "780", Agendamento.Status.CONFIRMADO),
                agendamento(lista.get(1), "Carla Mendes", "Limpeza de pele", proximo(agora, 1, 11), 60, "260", Agendamento.Status.AGENDADO),
                agendamento(lista.get(2), "Carla Mendes", "Drenagem linfática", proximo(agora, 1, 14), 50, "190", Agendamento.Status.CONFIRMADO),
                agendamento(lista.get(3), "Dra. Helena", "Avaliação", proximo(agora, 2, 10), 40, "120", Agendamento.Status.AGENDADO),
                agendamento(lista.get(4), "Bianca Souza", "Massagem modeladora", proximo(agora, 2, 15), 50, "220", Agendamento.Status.AGENDADO)
            ));

            var hoje = LocalDate.now();
            financeiro.saveAll(List.of(
                lancamento("Sessões e procedimentos", LancamentoFinanceiro.Tipo.RECEITA, LancamentoFinanceiro.Status.PAGO, "12840", hoje.minusDays(5), hoje.minusDays(5), "Atendimentos"),
                lancamento("Venda de pacotes", LancamentoFinanceiro.Tipo.RECEITA, LancamentoFinanceiro.Status.PAGO, "7350", hoje.minusDays(3), hoje.minusDays(3), "Pacotes"),
                lancamento("Procedimentos avulsos", LancamentoFinanceiro.Tipo.RECEITA, LancamentoFinanceiro.Status.PAGO, "4920", hoje.minusDays(1), hoje.minusDays(1), "Atendimentos"),
                lancamento("Aluguel da unidade", LancamentoFinanceiro.Tipo.DESPESA, LancamentoFinanceiro.Status.PAGO, "6800", hoje.minusDays(7), hoje.minusDays(7), "Estrutura"),
                lancamento("Insumos clínicos", LancamentoFinanceiro.Tipo.DESPESA, LancamentoFinanceiro.Status.PAGO, "3260", hoje.minusDays(2), hoje.minusDays(2), "Insumos"),
                lancamento("Pacotes a receber", LancamentoFinanceiro.Tipo.RECEITA, LancamentoFinanceiro.Status.PENDENTE, "9840", hoje.plusDays(10), null, "Pacotes")
            ));
        };
    }

    private Cliente cliente(String nome, String email, String telefone, String tags) { return Cliente.builder().nome(nome).email(email).telefone(telefone).tags(tags).status(Cliente.Status.ATIVO).consentimentoMarketing(true).build(); }
    private Lead lead(String nome, String origem, Lead.Status status, String responsavel, String valor) { return Lead.builder().nome(nome).email(nome.toLowerCase().replace(" ", ".") + "@exemplo.com").origem(origem).status(status).responsavel(responsavel).valorEstimado(new BigDecimal(valor)).build(); }
    private Oportunidade oportunidade(String titulo, Cliente cliente, Oportunidade.Etapa etapa, String valor, int chance, String responsavel, int dias) { return Oportunidade.builder().titulo(titulo).cliente(cliente).etapa(etapa).valor(new BigDecimal(valor)).probabilidade(chance).responsavel(responsavel).previsaoFechamento(LocalDate.now().plusDays(dias)).build(); }
    private Agendamento agendamento(Cliente c, String profissional, String servico, OffsetDateTime inicio, int minutos, String valor, Agendamento.Status status) { return Agendamento.builder().cliente(c).profissional(profissional).servico(servico).inicio(inicio).fim(inicio.plusMinutes(minutos)).valor(new BigDecimal(valor)).status(status).build(); }
    private LancamentoFinanceiro lancamento(String descricao, LancamentoFinanceiro.Tipo tipo, LancamentoFinanceiro.Status status, String valor, LocalDate vencimento, LocalDate pagamento, String categoria) { return LancamentoFinanceiro.builder().descricao(descricao).tipo(tipo).status(status).valor(new BigDecimal(valor)).vencimento(vencimento).pagamento(pagamento).categoria(categoria).build(); }
    private OffsetDateTime proximo(OffsetDateTime agora, int dias, int hora) { return agora.plusDays(dias).withHour(hora); }
}
