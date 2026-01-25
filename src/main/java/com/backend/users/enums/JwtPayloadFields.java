package com.backend.users.enums;

import lombok.Getter;

@Getter
public enum JwtPayloadFields {
  EMAIL("email"),
  ID("id");

  private final String name;

  JwtPayloadFields(String name) {
    this.name = name;
  }
}
