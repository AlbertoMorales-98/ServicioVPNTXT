package com.enviotxt.sftp.sender.service;

import com.enviotxt.sftp.sender.config.SftpSenderProperties;
import com.enviotxt.sftp.sender.dto.InvoiceRequest;
import com.enviotxt.sftp.sender.dto.TransferResponse;
import com.enviotxt.sftp.sender.integration.SftpUploadClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SftpInvoiceTransferService {

    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final SftpSenderProperties properties;
    private final SftpUploadClient uploadClient;

    public SftpInvoiceTransferService(SftpSenderProperties properties, SftpUploadClient uploadClient) {
        this.properties = properties;
        this.uploadClient = uploadClient;
    }

    public TransferResponse generateAndSend(InvoiceRequest request) {
        try {
            Path outboxDir = Path.of(properties.outboxDir());
            Files.createDirectories(outboxDir);

            String fileName = sanitizeFileName(request.requestedFileName());
            Path localFile = outboxDir.resolve(fileName);
            Files.writeString(localFile, request.resolveTxtContent(), StandardCharsets.UTF_8);

            String remotePath = uploadClient.upload(localFile);
            return new TransferResponse(
                    localFile.toAbsolutePath().toString(),
                    remotePath,
                    "Archivo transferido correctamente por SFTP"
            );
        } catch (IOException ex) {
            throw new IllegalStateException("No fue posible generar o transferir el archivo TXT", ex);
        }
    }

    private String sanitizeFileName(String requestedFileName) {
        if (requestedFileName == null || requestedFileName.isBlank()) {
            return "factura-sftp-" + FILE_TS.format(LocalDateTime.now()) + ".txt";
        }

        String normalized = requestedFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (normalized.endsWith(".txt")) {
            return normalized;
        }
        return normalized + ".txt";
    }
}
