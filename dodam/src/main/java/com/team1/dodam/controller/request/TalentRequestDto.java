package com.team1.dodam.controller.request;


import com.team1.dodam.domain.Talent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TalentRequestDto {
    private String title;
    private String content;
    private List<String> tagList;


}
