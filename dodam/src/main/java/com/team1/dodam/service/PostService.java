package com.team1.dodam.service;

import com.team1.dodam.dto.PostDto;
import com.team1.dodam.dto.request.PostRequestDto;
import com.team1.dodam.dto.response.MessageResponseDto;
import com.team1.dodam.dto.response.ResponseDto;
import com.team1.dodam.dto.response.PostResponseDto;
import com.team1.dodam.domain.*;
import com.team1.dodam.global.error.ErrorCode;
import com.team1.dodam.repository.ImageRepository;
import com.team1.dodam.repository.PostPickRepository;
import com.team1.dodam.repository.PostRepository;
import com.team1.dodam.shared.Category;
import com.team1.dodam.shared.PostStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
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
            return postRepository.findTop11ByTitleContainingAndPostStatus(searchValue, PostStatus.ACTIVATED, pageable).map(PostDto::from); }

        switch (Category.valueOf(categoryToUse)) {
            case DEVICE: return postRepository.findTop11ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.DEVICE, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case APPLIANCE: return postRepository.findTop11ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.APPLIANCE, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case KITCHEN: return postRepository.findTop11ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.KITCHEN, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case WOMEN: return postRepository.findTop11ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.WOMEN, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case MEN: return postRepository.findTop11ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.MEN, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case BEAUTY: return postRepository.findTop11ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.BEAUTY, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case GAME: return postRepository.findTop11ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.GAME, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case BOOK: return postRepository.findTop11ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.BOOK, PostStatus.ACTIVATED, pageable).map(PostDto::from);
            case TICKET: return postRepository.findTop11ByTitleContainingAndCategoryAndPostStatus(searchValue, Category.TICKET, PostStatus.ACTIVATED, pageable).map(PostDto::from);
        }

        return postRepository.findAllByPostStatus(PostStatus.ACTIVATED, pageable).map(PostDto::from);
    }

    @Transactional
    public ResponseDto<?> createPosts(UserDetailsImpl userDetails, PostRequestDto requestDto, List<MultipartFile> imageFileList) throws IOException {

        User loginUser = userDetails.getUser();

        if(loginUser == null) { return ResponseDto.fail(ErrorCode.USER_NOT_FOUND); }

        Post post = postRepository.save(Post.builder()
                .user(loginUser)
                .requestDto(requestDto)
                .build());

        List<String> imageList = new ArrayList<>();

//        if (imageFileList.get(0).getResource().getFilename().equals("")) {
        if (imageFileList == null) {
            Image image = imageRepository.save(Image.builder()
                                                    .imageUrl("https://inno-final-s3.s3.ap-northeast-2.amazonaws.com/default.png")
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

//        if(!imageFileList.get(0).getResource().getFilename().equals("")) {
        if(imageFileList != null) {
            for(MultipartFile imageFile: imageFileList) {
                Image image = imageRepository.save(Image.builder()
                                                        .imageUrl(s3UploadService.s3UploadFile(imageFile, "static/post/" + loginUser.getNickname() + "/" + post.getId()))
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
    public ResponseDto<?> readDetailPosts(Long postId, Long userId) {

        Post post = postRepository.findById(postId).stream()
                .filter(p -> p.getPostStatus().equals(PostStatus.ACTIVATED))
                .findFirst()
                .orElseThrow(() -> new NullPointerException("해당 게시글은 삭제되었습니다."));
        post.visit();

        return ResponseDto.success(PostResponseDto.builder()
                                                  .post(post)
                                                  .user(post.getUser())
                                                  .postPicked(postPickRepository.existsByPostIdAndUserId(postId, userId))
                                                  .imageUrlList(post.getImageList().stream().map(Image::getImageUrl).collect(Collectors.toList()))
                                                  .build());
    }

    @Transactional
    public ResponseDto<?> updatePosts(Long postId, UserDetailsImpl userDetails, PostRequestDto requestDto, List<MultipartFile> imageFileList) throws IOException {

        int inputFileListSize = 0;

//        if(imageFileList == null ) {
//            inputFileListSize = requestDto.getStringImageFileList().size();
//        }

        //위의 코드로 정상작동하지 않으면 위의 코드를 주석처리하고 아래 코드를 사용
        if(imageFileList == null && requestDto.getStringImageFileList() == null) { inputFileListSize = 0; }
        else if(imageFileList == null) { inputFileListSize = requestDto.getStringImageFileList().size(); }
        else if(requestDto.getStringImageFileList() == null) { inputFileListSize = imageFileList.size(); }
        else { inputFileListSize = imageFileList.size() + requestDto.getStringImageFileList().size(); }

        User loginUser = userDetails.getUser();

        if(loginUser == null) { return ResponseDto.fail(ErrorCode.USER_NOT_FOUND); }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NullPointerException("해당 게시글은 존재하지 않습니다."));

        imageRepository.deleteByPostId(postId);
        post.update(requestDto);

        List<String> imageList = new ArrayList<>();

//        if (imageFileList.get(0).getResource().getFilename().equals("") && requestDto.getStringImageFileList().size() == 0) {
        if (imageFileList == null && requestDto.getStringImageFileList().size() == 0) {
            Image image = imageRepository.save(Image.builder()
                                                    .imageUrl("https://inno-final-s3.s3.ap-northeast-2.amazonaws.com/default.png")
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

//        if (imageFileList.size() > 5) {
        if (inputFileListSize > 5) {
            return ResponseDto.fail(ErrorCode.POST_IMAGE_LENGTH_EXCEEDED);
        }

        if(imageFileList != null && requestDto.getStringImageFileList() == null) {
            for(MultipartFile imageFile: imageFileList) {
                Image image = imageRepository.save(Image.builder()
                        .imageUrl(s3UploadService.s3UploadFile(imageFile, "static/post/" + loginUser.getNickname() + "/" + post.getId()))
                        .user(loginUser)
                        .post(post)
                        .build());
                post.mapToImage(image);
                imageList.add(image.getImageUrl());
            }
        }

        if(imageFileList == null && requestDto.getStringImageFileList() != null) {
            for(String stringImageFileUrl : requestDto.getStringImageFileList()) {
                Image image = imageRepository.save(Image.builder()
                        .imageUrl(stringImageFileUrl)
                        .user(loginUser)
                        .post(post)
                        .build());
                post.mapToImage(image);
                imageList.add(image.getImageUrl());
            }
        }

        if(imageFileList != null && requestDto.getStringImageFileList() != null) {
        for(String stringImageFileUrl : requestDto.getStringImageFileList()) {
            Image image = imageRepository.save(Image.builder()
                    .imageUrl(stringImageFileUrl)
                    .user(loginUser)
                    .post(post)
                    .build());
            post.mapToImage(image);
            imageList.add(image.getImageUrl());
        }

        for(MultipartFile imageFile: imageFileList) {
            Image image = imageRepository.save(Image.builder()
                                                    .imageUrl(s3UploadService.s3UploadFile(imageFile, "static/post/" + loginUser.getNickname() + "/" + post.getId()))
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
    public ResponseDto<?> alterPostStatusToDelete(Long postId, UserDetailsImpl userDetails) {

        User loginUser = userDetails.getUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NullPointerException("해당 게시글은 존재하지 않습니다."));

        if(!loginUser.equals(userDetails.getUser())) { return ResponseDto.fail(ErrorCode.USER_NOT_FOUND); }

        post.updatePostStatusToDeleted();
        
        return ResponseDto.success(MessageResponseDto.builder()
                                                     .msg("게시글의 상태가 DELETED로 변경되었습니다.")
                                                     .build());
    }

    @Transactional
    public void deletePosts(Long postId, String nickname) {

        postRepository.deleteById(postId);
        s3UploadService.removeImageFolderInS3("static/post/" + nickname + "/" + postId);
        log.info("[Scheduler]" + nickname + "님의 id " + postId + "번 게시글의 데이터 삭제 완료.");
    }

    @Transactional
    public ResponseDto<?> pickPosts(Long postId, UserDetailsImpl userDetails) {

        AtomicReference<Boolean> postPicked = new AtomicReference<>(false);

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
                    postPicked.set(false);
                },
                () -> {
                    PostPick postPick = PostPick.builder().build();
                    postPick.mapToPost(loginUser, post);
                    post.updatePostPickCount();
                    postPickRepository.save(postPick);
                    postPicked.set(true);
                }
        );
        return ResponseDto.success(postPicked);
    }

    @Transactional
    public ResponseDto<?> completeDeal(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글을 찾을 수 없습니다.")
        );

        post.updateDealState();
        return ResponseDto.success(MessageResponseDto.builder().msg("거래 완료").build());
    }
}
