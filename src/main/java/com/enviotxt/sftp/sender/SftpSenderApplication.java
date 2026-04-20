package com.enviotxt.sftp.sender;

import com.enviotxt.sftp.sender.config.SftpSenderProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SftpSenderProperties.class)
public class SftpSenderApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SftpSenderApplication.class);
        application.setAdditionalProfiles("sender-sftp");
        application.run(args);
    }
}
