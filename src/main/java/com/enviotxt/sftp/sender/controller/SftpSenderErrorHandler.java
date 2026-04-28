package com.enviotxt.sftp.sender.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = SftpSenderController.class)
public class SftpSenderErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(SftpSenderErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Solicitud invalida para envio SFTP: {}", ex.getMessage());
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Solicitud invalida");
        detail.setDetail("Debes enviar invoiceLine o los campos estructurados de la factura.");
        return detail;
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleTransferErrors(Exception ex) {
        log.error("Fallo la operacion de envio SFTP", ex);
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        detail.setTitle("Error durante la transferencia SFTP");
        detail.setDetail(ex.getMessage());
        return detail;
    }
}
