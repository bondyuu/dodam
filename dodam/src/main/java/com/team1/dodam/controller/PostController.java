package com.team1.dodam.controller;

import com.team1.dodam.controller.request.CreateRequestDto;
import com.team1.dodam.controller.request.PostRequestDto;
import com.team1.dodam.controller.response.ResponseDto;
import com.team1.dodam.domain.UserDetailsImpl;
import com.team1.dodam.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Api(tags = {"게시글 전체조회 및 검색/생성/상세 조회/수정/삭제/찜하기"})
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 게시글 생성
    @ApiOperation(value = "게시글 생성 메소드")
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto<?> sharingPosting(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @Valid @RequestPart(value = "requestDto") PostRequestDto requestDto,
                                         @RequestPart(value = "imageFileList", required = false) List<MultipartFile> imageFileList) throws IOException {
        return postService.create(userDetails, requestDto, imageFileList);
    }

    // 게시글 찜하기
    @ApiOperation(value = "게시글 찜하기 메소드")
    @PostMapping("/{postId}/pick")
    public ResponseDto<?> postPick(@PathVariable(name = "postId") Long postId,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return postService.postPick(postId, userDetails);
    }

    @PostMapping("/posting")
    public ResponseDto<?> post(CreateRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return postService.post(requestDto, userDetails);
    }
}
