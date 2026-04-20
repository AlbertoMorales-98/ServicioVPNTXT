package com.enviotxt.sftp.mockserver.config;

import com.enviotxt.sftp.mockserver.server.EmbeddedSftpServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedSftpServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public EmbeddedSftpServer embeddedSftpServer(MockSftpServerProperties properties) {
        return new EmbeddedSftpServer(properties);
    }
}
