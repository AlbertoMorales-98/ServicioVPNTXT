package com.enviotxt.sftp.sender.controller;

import com.enviotxt.sftp.sender.dto.InvoiceRequest;
import com.enviotxt.sftp.sender.dto.TransferResponse;
import com.enviotxt.sftp.sender.service.SftpInvoiceTransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sftp/invoices")
public class SftpSenderController {

    private final SftpInvoiceTransferService transferService;

    public SftpSenderController(SftpInvoiceTransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping("/sample")
    public InvoiceRequest sample() {
        return InvoiceRequest.sample();
    }

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse send(@Valid @RequestBody InvoiceRequest request) {
        return transferService.generateAndSend(request);
    }
}
