package com.enviotxt.sftp.sender.integration;

import com.enviotxt.sftp.sender.config.SftpSenderProperties;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class SftpUploadClient {

    private static final Logger log = LoggerFactory.getLogger(SftpUploadClient.class);

    private final SftpSenderProperties properties;

    public SftpUploadClient(SftpSenderProperties properties) {
        this.properties = properties;
    }

    public String upload(Path localFile) {
        try (SSHClient sshClient = new SSHClient()) {
            log.info(
                    "Iniciando conexion SFTP. host={}, port={}, username={}, remoteDir={}, localFile={}",
                    properties.host(),
                    properties.port(),
                    properties.username(),
                    properties.remoteDir(),
                    localFile.toAbsolutePath()
            );
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect(properties.host(), properties.port());
            log.info("Conexion TCP/SFTP establecida con {}:{}", properties.host(), properties.port());
            sshClient.authPassword(properties.username(), properties.password());
            log.info("Autenticacion SFTP exitosa para usuario {}", properties.username());

            try (SFTPClient sftpClient = sshClient.newSFTPClient()) {
                ensureRemoteDirectories(sftpClient, properties.remoteDir());
                String remotePath = properties.remoteDir() + "/" + localFile.getFileName();
                log.info("Subiendo archivo por SFTP a {}", remotePath);
                sftpClient.put(localFile.toString(), remotePath);
                log.info("Archivo subido exitosamente a {}", remotePath);
                return remotePath;
            } finally {
                log.info("Cerrando conexion SFTP con {}:{}", properties.host(), properties.port());
                sshClient.disconnect();
            }
        } catch (IOException ex) {
            log.error(
                    "Error durante la transferencia SFTP. host={}, port={}, username={}, remoteDir={}, localFile={}",
                    properties.host(),
                    properties.port(),
                    properties.username(),
                    properties.remoteDir(),
                    localFile.toAbsolutePath(),
                    ex
            );
            throw new IllegalStateException("No fue posible subir el archivo por SFTP: " + ex.getMessage(), ex);
        }
    }

    private void ensureRemoteDirectories(SFTPClient client, String remoteDir) throws IOException {
        String[] parts = remoteDir.split("/");
        StringBuilder current = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                continue;
            }
            current.append("/").append(part);
            String path = current.toString();
            try {
                log.info("Verificando/creando directorio remoto {}", path);
                client.mkdir(path);
                log.info("Directorio remoto creado {}", path);
            } catch (IOException ignored) {
                log.debug("Directorio remoto ya existe o no pudo crearse directamente: {}", path);
            }
        }
    }
}
