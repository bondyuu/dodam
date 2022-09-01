package com.team1.dodam.service;

import com.team1.dodam.controller.request.PostRequestDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.controller.response.PostResponseDto;
import com.team1.dodam.domain.*;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.repository.ImageRepository;
import com.team1.dodam.repository.PostRepository;
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
public class PostService {

    private final S3UploadService s3UploadService;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public ResponseDto<?> create(UserDetailsImpl userDetails, PostRequestDto requestDto, List<MultipartFile> imageFileList) throws IOException {

        User loginUser = userDetails.getUser();

        if (loginUser.getAuthority() != Authority.ROLE_GIVE) {
            return ResponseDto.fail(ErrorCode.NOT_VALID_AUTHOTIRY);
        }

        Post post = postRepository.save(Post.builder()
                                            .user(loginUser)
                                            .requestDto(requestDto)
                                            .build());
        if (imageFileList.size() > 5) {
            return ResponseDto.fail(ErrorCode.POST_IMAGE_LENGTH_EXCEEDED);
        }

        List<String> imageList = new ArrayList<>();

        if(!imageFileList.get(0).getResource().getFilename().equals("")) {
            for(MultipartFile imageFile: imageFileList) {
                Image image = imageRepository.save(Image.builder()
                                             .imageUrl(s3UploadService.s3UploadFile(imageFile, "static/post"))
                                             .user(loginUser)
                                             .post(post)
                                             .build());
                imageList.add(image.getImageUrl());
            }
        }

        return ResponseDto.success(PostResponseDto.builder()
                .post(post)
                .user(loginUser)
                .imageUrlList(imageList)
                .build());
    }
}
