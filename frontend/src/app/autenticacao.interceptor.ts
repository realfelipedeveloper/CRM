import { HttpInterceptorFn } from '@angular/common/http';

export const autenticacaoInterceptor: HttpInterceptorFn = (requisicao, proximo) => {
  const token = sessionStorage.getItem('crm_token');
  return proximo(token ? requisicao.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : requisicao);
};

