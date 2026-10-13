import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';
import { ApiService, Cliente, Dashboard, Lancamento, Oportunidade } from './api.service';
import { formatarMoeda, obterIniciais, obterRotulo } from './formatacao';

type Pagina = 'Visão geral' | 'Clientes' | 'Pipeline' | 'Agenda' | 'Financeiro' | 'Relatórios' | 'Insights';

@Component({
  selector: 'crm-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  readonly dataAtualLabel = new Intl.DateTimeFormat('pt-BR', { weekday: 'long', day: '2-digit', month: 'long' })
    .format(new Date()).toLocaleUpperCase('pt-BR');
  autenticado = signal(!!sessionStorage.getItem('crm_token'));
  pagina = signal<Pagina>('Visão geral');
  temaEscuro = signal(localStorage.getItem('crm_tema') === 'dark');
  menuAberto = signal(false);
  carregando = signal(false);
  erro = signal('');
  usuario = signal(sessionStorage.getItem('crm_nome') ?? 'Marina Andrade');
  perfil = signal(sessionStorage.getItem('crm_perfil') ?? 'ADMINISTRADOR');
  dashboard = signal<Dashboard | null>(null);
  clientes = signal<Cliente[]>([]);
  oportunidades = signal<Oportunidade[]>([]);
  agendamentos = signal<any[]>([]);
  lancamentos = signal<Lancamento[]>([]);
  busca = signal('');

  readonly paginas: { nome: Pagina; icone: string }[] = [
    { nome: 'Visão geral', icone: '⌂' }, { nome: 'Clientes', icone: '♙' },
    { nome: 'Pipeline', icone: '◇' }, { nome: 'Agenda', icone: '□' }, { nome: 'Financeiro', icone: '↗' }
  ];
  readonly etapas = ['NOVO', 'QUALIFICACAO', 'PROPOSTA', 'NEGOCIACAO'];
  readonly relatorios = [
    { titulo: 'Desempenho financeiro', descricao: 'Receitas, despesas, resultado operacional e valores previstos.', icone: '↗', cor: 'vinho' },
    { titulo: 'Conversão comercial', descricao: 'Pipeline, oportunidades e taxa histórica de conversão.', icone: '◇', cor: 'roxo' },
    { titulo: 'Carteira de clientes', descricao: 'Clientes ativos, relacionamento e oportunidades de recorrência.', icone: '♙', cor: 'dourado' },
    { titulo: 'Operação e agenda', descricao: 'Atendimentos futuros, ocupação e compromissos por profissional.', icone: '□', cor: 'verde' }
  ];
  readonly loginForm;

  constructor(private readonly api: ApiService, fb: FormBuilder) {
    this.loginForm = fb.nonNullable.group({
      email: ['admin@clinica.local', [Validators.required, Validators.email]],
      senha: ['Admin@123', Validators.required]
    });
    document.documentElement.dataset['theme'] = this.temaEscuro() ? 'dark' : 'light';
  }

  ngOnInit(): void { if (this.autenticado()) this.carregarTudo(); }

  entrar(): void {
    if (this.loginForm.invalid) return;
    this.carregando.set(true); this.erro.set('');
    const { email, senha } = this.loginForm.getRawValue();
    this.api.entrar(email, senha).pipe(finalize(() => this.carregando.set(false))).subscribe({
      next: resposta => {
        sessionStorage.setItem('crm_token', resposta.token); sessionStorage.setItem('crm_nome', resposta.nome); sessionStorage.setItem('crm_perfil', resposta.perfil);
        this.usuario.set(resposta.nome); this.perfil.set(resposta.perfil); this.autenticado.set(true); this.carregarTudo();
      },
      error: () => this.erro.set('Não foi possível entrar. Confira o e-mail e a senha.')
    });
  }

  carregarTudo(): void {
    this.carregando.set(true); this.erro.set('');
    const inicio = new Date(); inicio.setHours(0, 0, 0, 0);
    const fim = new Date(inicio); fim.setDate(fim.getDate() + 14);
    forkJoin({
      dashboard: this.api.dashboard(), clientes: this.api.clientes(), pipeline: this.api.pipeline(),
      financeiro: this.api.financeiro(), agenda: this.api.agenda(inicio.toISOString(), fim.toISOString())
    }).pipe(finalize(() => this.carregando.set(false))).subscribe({
      next: r => { this.dashboard.set(r.dashboard); this.clientes.set(r.clientes.content); this.oportunidades.set(r.pipeline); this.lancamentos.set(r.financeiro.content); this.agendamentos.set(r.agenda); },
      error: erro => { if (erro.status === 401) this.sair(); else this.erro.set('Não foi possível atualizar os dados. Tente novamente.'); }
    });
  }

  navegar(pagina: Pagina): void { this.pagina.set(pagina); this.menuAberto.set(false); }
  exportarResumoFinanceiro(): void {
    const d = this.dashboard();
    if (!d) return;
    const linhas: (string | number)[][] = [
      ['Indicador', 'Valor'],
      ['Receita recebida', d.receitaRecebida],
      ['Despesas pagas', d.despesasPagas],
      ['Resultado operacional', d.resultadoOperacional],
      ['Receita prevista', d.receitaPrevista],
      ['Valor do pipeline', d.valorPipeline],
      ['Taxa de conversão (%)', d.taxaConversao]
    ];
    const conteudo = '\uFEFF' + linhas.map(linha => linha.join(';')).join('\n');
    const url = URL.createObjectURL(new Blob([conteudo], { type: 'text/csv;charset=utf-8' }));
    const link = document.createElement('a');
    link.href = url;
    link.download = `resumo-executivo-${new Date().toISOString().slice(0, 10)}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  }
  alternarTema(): void { const escuro = !this.temaEscuro(); this.temaEscuro.set(escuro); localStorage.setItem('crm_tema', escuro ? 'dark' : 'light'); document.documentElement.dataset['theme'] = escuro ? 'dark' : 'light'; }
  sair(): void { sessionStorage.clear(); this.autenticado.set(false); }
  filtrarClientes(valor: string): void { this.busca.set(valor); this.api.clientes(valor).subscribe(r => this.clientes.set(r.content)); }
  oportunidadesDaEtapa(etapa: string): Oportunidade[] { return this.oportunidades().filter(o => o.etapa === etapa); }
  totalEtapa(etapa: string): number { return this.oportunidadesDaEtapa(etapa).reduce((s, o) => s + Number(o.valor), 0); }
  alturaBarra(valor: number): number { const dados = this.dashboard()?.receitaUltimosSeteDias ?? []; const max = Math.max(...dados.map(i => Number(i.receita)), 1); return Math.max((Number(valor) / max) * 100, 5); }
  moeda(valor: number | string): string { return formatarMoeda(valor); }
  data(valor: string): string { return new Intl.DateTimeFormat('pt-BR', { day: '2-digit', month: 'short' }).format(new Date(valor)); }
  hora(valor: string): string { return new Intl.DateTimeFormat('pt-BR', { hour: '2-digit', minute: '2-digit' }).format(new Date(valor)); }
  iniciais(nome: string): string { return obterIniciais(nome); }
  rotulo(valor: string): string { return obterRotulo(valor); }
}

