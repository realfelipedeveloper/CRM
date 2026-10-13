export function formatarMoeda(valor: number | string): string {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL', maximumFractionDigits: 0 }).format(Number(valor));
}

export function obterIniciais(nome: string): string {
  return nome.trim().split(/\s+/).slice(0, 2).map(parte => parte[0]).join('').toUpperCase();
}

export function obterRotulo(valor: string): string {
  return valor.replaceAll('_', ' ').toLowerCase().replace(/^./, caractere => caractere.toUpperCase());
}

