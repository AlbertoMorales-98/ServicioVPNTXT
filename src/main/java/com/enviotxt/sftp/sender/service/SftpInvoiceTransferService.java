package com.enviotxt.sftp.sender.service;

import com.enviotxt.sftp.sender.config.SftpSenderProperties;
import com.enviotxt.sftp.sender.dto.InvoiceRequest;
import com.enviotxt.sftp.sender.dto.TransferResponse;
import com.enviotxt.sftp.sender.integration.SftpUploadClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(SftpInvoiceTransferService.class);

    private final SftpSenderProperties properties;
    private final SftpUploadClient uploadClient;

    public SftpInvoiceTransferService(SftpSenderProperties properties, SftpUploadClient uploadClient) {
        this.properties = properties;
        this.uploadClient = uploadClient;
    }

    public TransferResponse generateAndSend(InvoiceRequest request) {
        try {
            Path outboxDir = Path.of(properties.outboxDir());
            log.info("Preparando directorio local de salida: {}", outboxDir.toAbsolutePath());
            Files.createDirectories(outboxDir);

            String fileName = sanitizeFileName(request.requestedFileName());
            Path localFile = outboxDir.resolve(fileName);
            String txtContent = request.resolveTxtContent();
            Files.writeString(localFile, txtContent, StandardCharsets.UTF_8);
            log.info(
                    "Archivo TXT generado localmente. localPath={}, fileName={}, bytes={}, remoteDir={}",
                    localFile.toAbsolutePath(),
                    fileName,
                    txtContent.getBytes(StandardCharsets.UTF_8).length,
                    properties.remoteDir()
            );

            String remotePath = uploadClient.upload(localFile);
            log.info(
                    "Transferencia SFTP completada. localPath={}, remotePath={}",
                    localFile.toAbsolutePath(),
                    remotePath
            );
            return new TransferResponse(
                    localFile.toAbsolutePath().toString(),
                    remotePath,
                    "Archivo transferido correctamente por SFTP"
            );
        } catch (IOException ex) {
            log.error("Fallo al generar o preparar el archivo TXT para envio SFTP", ex);
            throw new IllegalStateException("No fue posible generar o transferir el archivo TXT", ex);
        }
    }

    private String sanitizeFileName(String requestedFileName) {
        if (requestedFileName == null || requestedFileName.isBlank()) {
            String generated = "factura-sftp-" + FILE_TS.format(LocalDateTime.now()) + ".txt";
            log.info("No se recibio requestedFileName. Se generara uno automatico: {}", generated);
            return generated;
        }

        String normalized = requestedFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (!normalized.equals(requestedFileName)) {
            log.info("requestedFileName normalizado de {} a {}", requestedFileName, normalized);
        }
        if (normalized.endsWith(".txt")) {
            return normalized;
        }
        return normalized + ".txt";
    }
}
