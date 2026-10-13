import { describe, expect, it } from 'vitest';
import { formatarMoeda, obterIniciais, obterRotulo } from './formatacao';

describe('formatação em português brasileiro', () => {
  it('formata valores monetários em reais', () => {
    expect(formatarMoeda(1250)).toContain('1.250');
  });

  it('obtém iniciais sem expor o nome completo', () => {
    expect(obterIniciais('Ana Beatriz Costa')).toBe('AB');
  });

  it('converte enumerações em rótulos legíveis', () => {
    expect(obterRotulo('RECEITA_PREVISTA')).toBe('Receita prevista');
  });
});

