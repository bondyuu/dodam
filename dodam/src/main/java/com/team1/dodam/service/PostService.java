package com.team1.dodam.service;

import com.team1.dodam.dto.PostDto;
import com.team1.dodam.dto.request.PostRequestDto;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.dto.response.PostResponseDto;
import com.team1.dodam.domain.*;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.repository.ImageRepository;
import com.team1.dodam.repository.PostPickRepository;
import com.team1.dodam.repository.PostRepository;
import com.team1.dodam.s3.S3UploadService;
import com.team1.dodam.shared.Category;
import com.team1.dodam.shared.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Transactional
@Service
@RequiredArgsConstructor
public class PostService {

    private final S3UploadService s3UploadService;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final PostPickRepository postPickRepository;

    @Transactional(readOnly = true)
    public Slice<PostDto> searchPosts(String searchValue, String category, Pageable pageable) {

        String categoryToUse = "";

        for (Category categoryValue : Category.values()) {
            if(categoryValue.name().equalsIgnoreCase(category)) { categoryToUse = category; break; }
        }

        if ((searchValue.equals("") || searchValue.isBlank()) && categoryToUse.equals("")) {
            return postRepository.findAllByPostStatus(PostStatus.ACTIVATED, pageable).map(PostDto::from); }

        if (categoryToUse.equals("")) {
            return postRepository.findTop6ByTitleContainingAndPostStatus(searchValue, PostStatus.ACTIVATED, pageable).map(PostDto::from); }

        switch (Category.valueOf(categoryToUse)) {
            case DEVICE: return postRepository.findTop6ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.DEVICE, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case APPLIANCE: return postRepository.findTop6ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.APPLIANCE, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case KITCHEN: return postRepository.findTop6ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.KITCHEN, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case WOMEN: return postRepository.findTop6ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.WOMEN, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case MEN: return postRepository.findTop6ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.MEN, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case BEAUTY: return postRepository.findTop6ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.BEAUTY, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case GAME: return postRepository.findTop6ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.GAME, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case BOOK: return postRepository.findTop6ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.BOOK, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case TICKET: return postRepository.findTop6ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.TICKET, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            default: break;
        }

        return postRepository.findAllByPostStatus(PostStatus.ACTIVATED, pageable).map(PostDto::from);
    }

    public ResponseDto<?> create(UserDetailsImpl userDetails, PostRequestDto requestDto, List<MultipartFile> imageFileList) throws IOException {

        User loginUser = userDetails.getUser();

//        if (loginUser.getAuthority() != Authority.ROLE_GIVE) {
//            return ResponseDto.fail(ErrorCode.NOT_VALID_AUTHOTIRY);
//        }

        Post post = postRepository.save(Post.builder()
                .user(loginUser)
                .requestDto(requestDto)
                .build());

        List<String> imageList = new ArrayList<>();

        if (imageFileList.get(0).getResource().getFilename().equals("")) {
            Image image = imageRepository.save(Image.builder()
                                                    .imageUrl("https://inno-final-s3.s3.ap-northeast-2.amazonaws.com/static/post/default.png")
                                                    .user(loginUser)
                                                    .post(post)
                                                    .build());
            post.mapToImage(image);
            imageList.add(image.getImageUrl());

            return ResponseDto.success(PostResponseDto.builder()
                                                      .post(post)
                                                      .user(loginUser)
                                                      .imageUrlList(imageList)
                                                      .build());
        }

        if (imageFileList.size() > 5) {
            return ResponseDto.fail(ErrorCode.POST_IMAGE_LENGTH_EXCEEDED);
        }

        if(!imageFileList.get(0).getResource().getFilename().equals("")) {
            for(MultipartFile imageFile: imageFileList) {
                Image image = imageRepository.save(Image.builder()
                                                        .imageUrl(s3UploadService.s3UploadFile(imageFile, "static/post"))
                                                        .user(loginUser)
                                                        .post(post)
                                                        .build());
                post.mapToImage(image);
                imageList.add(image.getImageUrl());
            }
        }

        return ResponseDto.success(PostResponseDto.builder()
                                                  .post(post)
                                                  .user(loginUser)
                                                  .imageUrlList(imageList)
                                                  .build());
    }

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
}
