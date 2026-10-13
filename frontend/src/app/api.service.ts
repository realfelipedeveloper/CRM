import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Dashboard {
  receitaRecebida: number; despesasPagas: number; resultadoOperacional: number; receitaPrevista: number;
  clientesAtivos: number; leadsAbertos: number; agendamentosHoje: number; valorPipeline: number; taxaConversao: number;
  receitaUltimosSeteDias: { data: string; receita: number }[];
  pipelinePorEtapa: { etapa: string; quantidade: number; valor: number }[];
  proximosAgendamentos: { inicio: string; cliente: string; servico: string; profissional: string; status: string }[];
}
export interface Cliente { id: string; nome: string; email: string; telefone: string; tags: string; status: string; }
export interface Oportunidade { id: string; titulo: string; cliente: string; etapa: string; valor: number; probabilidade: number; responsavel: string; previsaoFechamento: string; }
export interface Lancamento { id: string; descricao: string; tipo: string; status: string; valor: number; vencimento: string; pagamento?: string; categoria: string; }
export interface Pagina<T> { content: T[]; totalElements: number; totalPages: number; number: number; }

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private readonly http: HttpClient) {}
  entrar(email: string, senha: string): Observable<{ token: string; nome: string; perfil: string }> {
    return this.http.post<{ token: string; nome: string; perfil: string }>('/api/autenticacao/entrar', { email, senha });
  }
  dashboard(): Observable<Dashboard> { return this.http.get<Dashboard>('/api/bi/dashboard'); }
  clientes(busca = ''): Observable<Pagina<Cliente>> { return this.http.get<Pagina<Cliente>>('/api/clientes', { params: { busca } }); }
  pipeline(): Observable<Oportunidade[]> { return this.http.get<Oportunidade[]>('/api/comercial/pipeline'); }
  financeiro(): Observable<Pagina<Lancamento>> { return this.http.get<Pagina<Lancamento>>('/api/financeiro/lancamentos'); }
  agenda(inicio: string, fim: string): Observable<any[]> { return this.http.get<any[]>('/api/agenda', { params: { inicio, fim } }); }
}
