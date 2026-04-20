package com.enviotxt.sftp.mockserver.server;

import com.enviotxt.sftp.mockserver.config.MockSftpServerProperties;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class EmbeddedSftpServer {

    private final MockSftpServerProperties properties;
    private SshServer sshServer;

    public EmbeddedSftpServer(MockSftpServerProperties properties) {
        this.properties = properties;
    }

    public void start() throws IOException {
        Path storagePath = Path.of(properties.storageDir());
        Files.createDirectories(storagePath);
        Files.createDirectories(Path.of(properties.hostKeyPath()).getParent());

        sshServer = SshServer.setUpDefaultServer();
        sshServer.setPort(properties.port());
        sshServer.setKeyPairProvider(hostKeyProvider());
        sshServer.setSubsystemFactories(List.of(new SftpSubsystemFactory.Builder().build()));
        sshServer.setPasswordAuthenticator(passwordAuthenticator());
        sshServer.setFileSystemFactory(new VirtualFileSystemFactory(storagePath));
        sshServer.start();
    }

    public void stop() throws IOException {
        if (sshServer != null && !sshServer.isClosed()) {
            sshServer.stop();
        }
    }

    private KeyPairProvider hostKeyProvider() {
        return new SimpleGeneratorHostKeyProvider(Path.of(properties.hostKeyPath()));
    }

    private PasswordAuthenticator passwordAuthenticator() {
        return (username, password, session) ->
                properties.username().equals(username) && properties.password().equals(password);
    }
}
