package com.team1.dodam.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RefreshToken extends Timestamped {

  @Id
  @Column(nullable = false)
  private Long id;

  @JoinColumn(name = "userId", nullable = false)
  @OneToOne(fetch = FetchType.LAZY)
  private User user;

  @Column(nullable = false)
  private String refreshTokenValue;

  public void updateValue(String token) {
    this.refreshTokenValue = token;
  }
}
