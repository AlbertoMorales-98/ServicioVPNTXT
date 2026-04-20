package com.enviotxt.sftp.sender.integration;

import com.enviotxt.sftp.sender.config.SftpSenderProperties;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class SftpUploadClient {

    private final SftpSenderProperties properties;

    public SftpUploadClient(SftpSenderProperties properties) {
        this.properties = properties;
    }

    public String upload(Path localFile) {
        try (SSHClient sshClient = new SSHClient()) {
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect(properties.host(), properties.port());
            sshClient.authPassword(properties.username(), properties.password());

            try (SFTPClient sftpClient = sshClient.newSFTPClient()) {
                ensureRemoteDirectories(sftpClient, properties.remoteDir());
                String remotePath = properties.remoteDir() + "/" + localFile.getFileName();
                sftpClient.put(localFile.toString(), remotePath);
                return remotePath;
            } finally {
                sshClient.disconnect();
            }
        } catch (IOException ex) {
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
                client.mkdir(path);
            } catch (IOException ignored) {
            }
        }
    }
}
