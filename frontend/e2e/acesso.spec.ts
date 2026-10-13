import { expect, test } from '@playwright/test';

test('autentica e apresenta indicadores reais do dashboard', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByRole('heading', { name: 'Acesse sua conta' })).toBeVisible();
  await page.getByRole('button', { name: 'Entrar no Áurea' }).click();
  await expect(page.getByRole('heading', { name: /Bom dia, Marina/ })).toBeVisible();
  await expect(page.getByText('RECEITA RECEBIDA', { exact: true })).toBeVisible();
  await expect(page.getByLabel('Indicadores principais').getByText('R$ 25.110', { exact: true })).toBeVisible();
});

test('navega para clientes e exibe dados persistidos', async ({ page }) => {
  await page.goto('/');
  await page.getByRole('button', { name: 'Entrar no Áurea' }).click();
  await page.getByRole('button', { name: /Clientes/ }).click();
  await expect(page.getByRole('heading', { name: 'Clientes' })).toBeVisible();
  await expect(page.getByText('Ana Beatriz Costa')).toBeVisible();
});

test('abre os submenus de inteligência', async ({ page }) => {
  await page.goto('/');
  await page.getByRole('button', { name: 'Entrar no Áurea' }).click();

  await page.getByRole('button', { name: /Relatórios/ }).click();
  await expect(page.getByRole('heading', { name: 'Relatórios' })).toBeVisible();
  await expect(page.getByText('Resumo executivo')).toBeVisible();

  await page.getByRole('button', { name: /Insights/ }).click();
  await expect(page.getByRole('heading', { name: 'Insights' })).toBeVisible();
  await expect(page.getByText('Proteja a previsibilidade do fluxo de caixa')).toBeVisible();
});
