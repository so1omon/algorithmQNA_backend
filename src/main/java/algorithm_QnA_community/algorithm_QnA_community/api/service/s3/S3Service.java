package algorithm_QnA_community.algorithm_QnA_community.api.service.s3;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.member.MemberProfileDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.s3.ImageUploadRes;

import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.image.Image;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.repository.ImageRepository;
import algorithm_QnA_community.algorithm_QnA_community.utils.MultipartUtils;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.member
 * fileName       : S3Service
 * author         : solmin
 * date           : 2023/05/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/26        solmin       최초 생성
 * 2023/06/01        solmin       uploadImage, moveImage 등 이미지 업로드 및 삭제에 관련된 서비스 메소드 작성
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3Client amazonS3Client;
    private final ImageRepository imageRepository;

    public static final String POST_DIR = "post";
    public static final String COMMENT_DIR = "comment";
    public static final String MEMBER_DIR = "member";
    public static final String TEMP_DIR = "temp";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    @Transactional
    public MemberProfileDto updateMemberProfile(Member loginMember, MultipartFile file) {
        String format = MultipartUtils.getFormat(file.getContentType());
        if(format==null){
            throw new CustomException(ErrorCode.INVALID_EXTENSION,"지원하지 않는 확장자입니다.");
        }

        try {
            // 1. 유저의 프로필 정보가 null이 아니라면 우선 삭제 (예외처리)
            String prevProfileImgUrl = loginMember.getProfileImgUrl();
            if(prevProfileImgUrl!=null){
                if (amazonS3Client.doesObjectExist(bucket, prevProfileImgUrl)) {
                    amazonS3Client.deleteObject(bucket, prevProfileImgUrl);
                    log.info("기존 멤버 프로필 url에 해당하는 이미지 발견 후 삭제");
                }
            }
            // 2. S3에 /loginMember/{fileName} 형태로 업로드 후 memberRepository에 저장
            log.info("content type : {}", file.getContentType());


            String fileName = getString(format, MEMBER_DIR);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
            String savedUrl = amazonS3Client.getUrl(bucket, fileName).toString();
            log.info("새 멤버 프로필 등록");
            loginMember.updateProfile(savedUrl);
            return new MemberProfileDto(savedUrl);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED, "에러로 인해 프로필 변경에 실패했습니다.");
        }
    }


    @Transactional
    public ImageUploadRes uploadImage(MultipartFile file) {
        // 1. 이미지 업로드
        String format = MultipartUtils.getFormat(file.getContentType());
        if(format==null){
            throw new CustomException(ErrorCode.INVALID_EXTENSION,"지원하지 않는 확장자입니다.");
        }

        try {
            // 1. 유저의 프로필 정보가 null이 아니라면 우선 삭제 (예외처리)

            String fileName = getString(format, TEMP_DIR);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
            String savedUrl = amazonS3Client.getUrl(bucket, fileName).toString();

            // 2. imageRepository에 저장
            Image savedImage = imageRepository.save(new Image(savedUrl));

            // 3. ImageUploadRes에 url, id, createdAt 담아서 저장
            return new ImageUploadRes(savedImage);

        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED, "에러로 인해 프로필 변경에 실패했습니다.");
        }
    }

    private String getString(String format, String dir) {
        String fileId = MultipartUtils.createFileId();
        String fileName = MultipartUtils.createFileName(dir, fileId, format);
        return fileName;
    }

    public void moveImage(String oldSource, String newSource) {
        try {
            oldSource = URLDecoder.decode(oldSource, "UTF-8");
            newSource = URLDecoder.decode(newSource, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        moveS3(oldSource, newSource);
        deleteS3(oldSource);

    }

    private void moveS3(String oldSource, String newSource) {
        amazonS3Client.copyObject(bucket, oldSource, bucket, newSource);
    }

    private void deleteS3(String source) {
        amazonS3Client.deleteObject(bucket, source);
    }

    @Transactional
    public void moveImages(Long objectId, List<Long> imageIds, String dir) {

        for(Long id : imageIds){
            Image findImage = imageRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.DELAYED_UPLOAD, "업로드 가능 시간이 지났습니다."));
            String url = findImage.getUrl();
            if (!url.contains("/temp/")) {
                continue;
            }
            String oldSource = url.replace("https://algoqnabucket.s3.ap-northeast-2.amazonaws.com/", "");
            String newSource = dir+"/"+objectId+"/"+oldSource.split("/")[1];
            moveImage(oldSource, newSource);

            imageRepository.delete(findImage);
        }
    }

    @Transactional
    public void deleteImagesByPrefix(String prefix){
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
            .withBucketName(bucket)
            .withPrefix(prefix);

        ObjectListing objectListing = amazonS3Client.listObjects(listObjectsRequest);

        while (true) {
            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                amazonS3Client.deleteObject(bucket, objectSummary.getKey());
            }
            if (objectListing.isTruncated()) {
                objectListing = amazonS3Client.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }

    }
}
