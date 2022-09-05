package com.team1.dodam.controller.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CertificationRequestDto {

    @NotBlank
    private String email;

    @NotBlank
    private Long certificationNum;
}