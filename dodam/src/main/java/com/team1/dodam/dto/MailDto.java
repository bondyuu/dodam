package com.team1.dodam.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class MailDto {

    @Email
    private String address;

    private String title;

    private String content;
}
