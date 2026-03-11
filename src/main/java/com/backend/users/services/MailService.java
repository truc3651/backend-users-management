package com.backend.users.services;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
  public Mono<Void> sendWelcomeMail() {

  }

  public Mono<Void> sendResetPasswordMail(String mail, String token) {

  }
}
