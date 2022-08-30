package com.team1.dodam.service;

import com.amazonaws.Response;
import com.team1.dodam.controller.request.TalentRequestDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.controller.response.TalentResponseDto;
import com.team1.dodam.domain.Talent;
import com.team1.dodam.domain.TalentTag;
import com.team1.dodam.domain.User;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.repository.TalentRepository;
import com.team1.dodam.repository.TalentTagRepository;
import com.team1.dodam.s3.S3UploadService;
import com.team1.dodam.shared.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TalentService {

    private final S3UploadService s3UploadService;
    private final TalentRepository talentRepository;
    private final TalentTagRepository talentTagRepository;

    public ResponseDto<?> posting(UserDetailsImpl userDetails, TalentRequestDto requestDto, MultipartFile imageFile) throws IOException {

        //로그인 유저의 authority확인하기
        User loginUser = userDetails.getUser();
        if (loginUser.getAuthority() != Authority.ROLE_GIVE) {
            return ResponseDto.fail(ErrorCode.NOT_VALID_AUTHOTIRY);
        }

        String imageUrl = s3UploadService.upload(imageFile,"static");

        Talent talent = talentRepository.save(Talent.builder()
                                                    .user(loginUser)
                                                    .imageUrl(imageUrl)
                                                    .requestDto(requestDto)
                                                    .build());

        List<String> tagList = requestDto.getTagList();

        for(String tag:tagList){
            talentTagRepository.save(TalentTag.builder().talent(talent).tag(tag).build());
        }

        return ResponseDto.success(TalentResponseDto.builder().talent(talent).user(loginUser).build());
    }
}
