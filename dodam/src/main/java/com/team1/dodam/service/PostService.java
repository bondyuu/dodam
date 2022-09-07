package com.team1.dodam.service;

import com.team1.dodam.dto.PostDto;
import com.team1.dodam.dto.request.CreateRequestDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
        }

        return postRepository.findAllByPostStatus(PostStatus.ACTIVATED, pageable).map(PostDto::from);
    }

    @Transactional
    public ResponseDto<?> createPosts(UserDetailsImpl userDetails, PostRequestDto requestDto, List<MultipartFile> imageFileList) throws IOException {

        User loginUser = userDetails.getUser();

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

    @Transactional
    public ResponseDto<?> readDetailPosts(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NullPointerException("해당 게시글은 존재하지 않습니다."));

        return ResponseDto.success(PostResponseDto.builder()
                                                  .post(post)
                                                  .user(post.getUser())
                                                  .imageUrlList(Collections.singletonList(String.valueOf(post.getImageList())))
                                                  .build());
    }

    @Transactional
    public ResponseDto<?> alterPostStatusToDelete(Long postId, UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();
    

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NullPointerException("해당 게시글은 존재하지 않습니다."));

        if(!loginUser.equals(userDetails.getUser())) { return ResponseDto.fail(ErrorCode.USER_NOT_FOUND); }

        post.updatePostStatusToDeleted();
        
        return ResponseDto.success(PostResponseDto.builder()
                                                  .post(post)
                                                  .user(post.getUser())
                                                  .imageUrlList(Collections.singletonList(String.valueOf(post.getImageList())))
                                                  .build());
    }

    @Transactional
    public void deletePosts(Long postId) { postRepository.deleteById(postId); }

    @Transactional
    public ResponseDto<?> pickPosts(Long postId, UserDetailsImpl userDetails) {

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

    @Transactional
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
