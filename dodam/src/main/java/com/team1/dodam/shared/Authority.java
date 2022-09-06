package com.team1.dodam.shared;

import lombok.Getter;

public enum Authority {
  ROLE_USER("일반 사용자"),
  ROLE_MEMBER("판매자"),
  ROLE_ADMIN("관리자");

  @Getter
  private final String description;

  Authority(String description) {
    this.description = description;
  }
}
