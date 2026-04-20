package com.enviotxt.sftp.sender.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.sftp.sender")
public record SftpSenderProperties(
        @NotBlank String outboxDir,
        @NotBlank String host,
        @Min(1) int port,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String remoteDir
) {
}
