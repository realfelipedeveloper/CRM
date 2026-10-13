import 'zone.js';
import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { AppComponent } from './app/app.component';
import { autenticacaoInterceptor } from './app/autenticacao.interceptor';

bootstrapApplication(AppComponent, {
  providers: [provideHttpClient(withInterceptors([autenticacaoInterceptor]))]
}).catch(erro => console.error('Não foi possível iniciar o CRM.', erro));

