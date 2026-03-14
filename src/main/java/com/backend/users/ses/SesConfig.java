package com.backend.users.ses;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@ConditionalOnProperty(name = "AWS_REGION")
public class SesConfig {
  @Bean
  public SesClient sesClient() {
    return SesClient.create();
  }
}
