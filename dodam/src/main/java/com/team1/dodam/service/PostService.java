package com.team1.dodam.service;

import com.team1.dodam.controller.request.CreateRequestDto;
import com.team1.dodam.controller.request.PostRequestDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.controller.response.PostResponseDto;
import com.team1.dodam.domain.*;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.repository.ImageRepository;
import com.team1.dodam.repository.PostPickRepository;
import com.team1.dodam.repository.PostRepository;
import com.team1.dodam.s3.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class PostService {

    private final S3UploadService s3UploadService;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final PostPickRepository postPickRepository;

    @Transactional
    public ResponseDto<?> create(UserDetailsImpl userDetails, PostRequestDto requestDto, List<MultipartFile> imageFileList) throws IOException {

        User loginUser = userDetails.getUser();

//        if (loginUser.getAuthority() != Authority.ROLE_GIVE) {
//            return ResponseDto.fail(ErrorCode.NOT_VALID_AUTHOTIRY);
//        }

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

    @Transactional
    public ResponseDto<?> postPick(Long postId, UserDetailsImpl userDetails) throws IOException {

        AtomicReference<String> message = new AtomicReference<>("게시글 찜하기 실패했습니다.");

        User loginUser = userDetails.getUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NullPointerException("해당 게시글은 존재하지 않습니다."));

        if(loginUser == null) { return ResponseDto.fail(ErrorCode.USER_NOT_FOUND); }

        Optional<PostPick> postPickFound = postPickRepository.findByUserAndPost(loginUser, post);

        postPickFound.ifPresentOrElse(
                postPickVar -> {
                    post.discountPostPickCount(postPickVar);
                    post.updatePostPickCount();
                    postPickRepository.delete(postPickVar);
                    message.set("게시글 찜하기 취소했습니다.");
                },
                () -> {
                    PostPick postPick = PostPick.builder().build();
                    postPick.mapToPost(loginUser, post);
                    post.updatePostPickCount();
                    postPickRepository.save(postPick);
                    message.set("게시글 찜하기 성공했습니다.");
                }
        );

        return ResponseDto.success(message);
    }

    public ResponseDto<?> post(CreateRequestDto requestDto, UserDetailsImpl userDetails) throws IOException {
        User loginUser = userDetails.getUser();
        System.out.println(requestDto);
        Post post = postRepository.save(new Post(requestDto, loginUser));

        if (requestDto.getImageFileList().size() > 5) {
            return ResponseDto.fail(ErrorCode.POST_IMAGE_LENGTH_EXCEEDED);
        }

        List<String> imageList = new ArrayList<>();

        if (!requestDto.getImageFileList().get(0).getResource().getFilename().equals("")) {
            for (MultipartFile imageFile : requestDto.getImageFileList()) {
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
