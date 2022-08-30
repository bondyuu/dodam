package com.team1.dodam.service;

import com.team1.dodam.controller.request.TalentRequestDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.controller.response.TalentResponseDto;
import com.team1.dodam.domain.*;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.repository.ImageRepository;
import com.team1.dodam.repository.TalentRepository;
import com.team1.dodam.repository.TagRepository;
import com.team1.dodam.s3.S3UploadService;
import com.team1.dodam.shared.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TalentService {

    private final S3UploadService s3UploadService;
    private final TalentRepository talentRepository;
    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public ResponseDto<?> posting(UserDetailsImpl userDetails, TalentRequestDto requestDto, MultipartFile imageFile) throws IOException {

        //로그인 유저의 authority확인하기
        User loginUser = userDetails.getUser();
        if (loginUser.getAuthority() != Authority.ROLE_GIVE) {
            return ResponseDto.fail(ErrorCode.NOT_VALID_AUTHOTIRY);
        }
        Talent talent = talentRepository.save(Talent.builder()
                .user(loginUser)
                .requestDto(requestDto)
                .build());

//        String imageUrl = s3UploadService.upload(imageFile,"static");
        Image image = imageRepository.save(Image.builder().imageUrl(s3UploadService.upload(imageFile,"static")).talent(talent).build());

        List<String> tagListStr = requestDto.getTagList();
        List<String> tagList = new ArrayList<>();
        for(String tagStr:tagListStr){
            Tag tag = tagRepository.save(Tag.builder().talent(talent).tag(tagStr).build());
            tagList.add(tag.getTag());
        }


        return ResponseDto.success(TalentResponseDto.builder()
                                                    .talent(talent)
                                                    .imageUrl(image.getImageUrl())
                                                    .user(loginUser)
                                                    .tagList(tagList)
                                                    .build());
    }
}
