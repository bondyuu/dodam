package com.team1.dodam.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto implements Serializable {
    @NotBlank private String title;
    @NotBlank private String content;
    @NotBlank private String category;
    private List<String> stringImageFileList;
}
