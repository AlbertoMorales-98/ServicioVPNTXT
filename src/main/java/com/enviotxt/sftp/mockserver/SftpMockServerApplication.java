package com.enviotxt.sftp.mockserver;

import com.enviotxt.sftp.mockserver.config.MockSftpServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MockSftpServerProperties.class)
public class SftpMockServerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SftpMockServerApplication.class);
        application.setAdditionalProfiles("mock-sftp");
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setKeepAlive(true);
        application.run(args);
    }
}
