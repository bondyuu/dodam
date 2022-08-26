package com.team1.dodam.s3;//package com.sparta.clone.s3;
//
//import com.amazonaws.AmazonServiceException;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import com.amazonaws.services.s3.model.*;
//import com.sparta.clone.global.error.ErrorCode;
//import com.sparta.clone.exception.BusinessException;
//import com.sparta.clone.exception.InvalidValueException;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//
//@Slf4j
//@Service
//@NoArgsConstructor
//public class S3Service {
//    private AmazonS3 s3Client;
//
//    @Value("${cloud.aws.credentials.accessKey}")
//    private String accessKey;
//
//    @Value("${cloud.aws.credentials.secretKey}")
//    private String secretKey;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    @Value("${cloud.aws.region.static}")
//    private String region;
//
//    @PostConstruct
//    public void setS3Client() {
//        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
//
//        s3Client = AmazonS3ClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(this.region)
//                .build();
//    }
//
//    public String uploadImage(MultipartFile file) {
//
//        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename().toLowerCase();
//        try {
//            // 확장자 점검
//            if (!(fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png"))) {
//                throw new InvalidValueException(ErrorCode.INVALID_IMAGE_FILE_EXTENSION);
//            }
//            // 파일 업로드
//            s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
//                    .withCannedAcl(CannedAccessControlList.PublicRead));
//        } catch (IOException e) {
//            throw new BusinessException(ErrorCode.UPLOAD_FAILED);
//        }
//        ///url string 리턴
//        return s3Client.getUrl(bucket, fileName).toString();
//    }
//
//    public List<DeleteObjectsRequest.KeyVersion> getImageKeys() {
//        ListObjectsV2Result result = s3Client.listObjectsV2(bucket);
//        List<S3ObjectSummary> objects = result.getObjectSummaries();
//        List<DeleteObjectsRequest.KeyVersion> imageNameList = new CopyOnWriteArrayList<>();
//        for (S3ObjectSummary os : objects) {
//            imageNameList.add(new DeleteObjectsRequest.KeyVersion(os.getKey()));
//        }
//        return imageNameList;
//    }
//
//    public void deleteObject(String sourceKey) {
//        s3Client.deleteObject(bucket, sourceKey);
//    }
//
//    //DB에 있는 값을 그대로 사용하기 위하여 새로운 메소드 생성
//    public void deleteObjectByImageUrl(String imageUrl) {
//        //split을 통해 나누고 나눈 length에서 1을 빼서 마지막 값(파일명)을 사용함.
//        String sourceKey = imageUrl.split("/")[imageUrl.split("/").length - 1];
//        //소스키로 s3에서 삭제
//        s3Client.deleteObject(bucket, sourceKey);
//    }
//
//    public void deleteObjects(List<DeleteObjectsRequest.KeyVersion> object_keys) {
//        DeleteObjectsRequest dor = new DeleteObjectsRequest(bucket)
//                .withKeys(object_keys);
//        try {
//            s3Client.deleteObjects(dor);    // exception 처리.
//        } catch (AmazonServiceException e) {
//            log.error("s3 Objects 삭제 도중 AmazonServiceException 발생");
//            System.err.println(e.getErrorMessage());
//            throw new BusinessException(ErrorCode.DELETE_FAILED);
//        }
//    }
//}
