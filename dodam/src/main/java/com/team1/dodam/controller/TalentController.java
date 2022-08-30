package com.team1.dodam.controller;

import com.amazonaws.Response;
import com.team1.dodam.controller.request.TalentRequestDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.service.TalentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class TalentController {

    private final TalentService talentService;

    @PostMapping("/talent")
    public ResponseDto<?> talentPosting(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                        @RequestPart TalentRequestDto requestDto,
                                        @RequestPart(required = false)MultipartFile imageFile) throws IOException {
        System.out.println(requestDto);
        System.out.println(imageFile);
        return talentService.posting(userDetails, requestDto, imageFile);
    }
}
