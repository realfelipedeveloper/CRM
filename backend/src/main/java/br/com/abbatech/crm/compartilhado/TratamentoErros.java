package br.com.abbatech.crm.compartilhado;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.LinkedHashMap;

@RestControllerAdvice
public class TratamentoErros {
    @ExceptionHandler(EntityNotFoundException.class)
    ProblemDetail naoEncontrado(EntityNotFoundException excecao) {
        var problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, excecao.getMessage());
        problema.setTitle("Recurso não encontrado");
        problema.setType(URI.create("urn:crm:recurso-nao-encontrado"));
        return problema;
    }

    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail conflito(IllegalStateException excecao) {
        var problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, excecao.getMessage());
        problema.setTitle("Conflito de operação");
        problema.setType(URI.create("urn:crm:conflito"));
        return problema;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validacao(MethodArgumentNotValidException excecao) {
        var campos = new LinkedHashMap<String, String>();
        for (FieldError erro : excecao.getBindingResult().getFieldErrors()) campos.put(erro.getField(), erro.getDefaultMessage());
        var problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Revise os campos informados.");
        problema.setTitle("Dados inválidos");
        problema.setProperty("campos", campos);
        return problema;
    }
}

