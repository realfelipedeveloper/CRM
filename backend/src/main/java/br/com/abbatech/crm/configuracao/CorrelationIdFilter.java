package br.com.abbatech.crm.configuracao;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
    private static final String CABECALHO = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String id = request.getHeader(CABECALHO);
        if (id == null || !id.matches("[a-zA-Z0-9._-]{8,80}")) id = UUID.randomUUID().toString();
        MDC.put("correlationId", id);
        response.setHeader(CABECALHO, id);
        try { chain.doFilter(request, response); }
        finally { MDC.remove("correlationId"); }
    }
}

