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
