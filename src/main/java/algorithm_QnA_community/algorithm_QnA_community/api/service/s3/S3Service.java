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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
 * 2023/06/02        solmin       스케줄러 적용, 하루가 지난 업로드 데이터 삭제
 * 2023/06/20        solmin       replaceTarget @Value로 받기
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

    @Value("${cloud.aws.s3.url}")
    public String replaceTarget;

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
                }
            }
            // 2. S3에 /loginMember/{fileName} 형태로 업로드 후 memberRepository에 저장
            String fileName = getString(format, MEMBER_DIR);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
            String savedUrl = amazonS3Client.getUrl(bucket, fileName).toString();

            loginMember.updateProfile(savedUrl);
            return new MemberProfileDto(savedUrl);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED, "서버 내부 오류로 인해 프로필 변경에 실패했습니다.");
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
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED, "서버 내부 오류로 인해 이미지 업로드에 실패했습니다.");
        }
    }

    private String getString(String format, String dir) {
        String fileId = MultipartUtils.createFileId();
        String fileName = MultipartUtils.createFileName(dir, fileId, format);
        return fileName;
    }

    public void moveImage(String oldSource, String newSource) {
        try {
            oldSource = getDecodedUrl(oldSource);
            newSource = getDecodedUrl(newSource);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        moveS3(oldSource, newSource);
        deleteS3(oldSource);

    }

    private static String getDecodedUrl(String source) throws UnsupportedEncodingException {
        try {
            source = URLDecoder.decode(source, "UTF-8");
            return source;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void moveS3(String oldSource, String newSource) {
        amazonS3Client.copyObject(bucket, oldSource, bucket, newSource);
    }

    private void deleteS3(String source) {
        amazonS3Client.deleteObject(bucket, source);
    }

    /*
     * content를 넘기면, findImage url과 매칭되는 부분을 replace하는 방식으로 바꾸기
     * */
    @Transactional
    public String moveImages(Long objectId, String content, List<Long> imageIds, String dir) {

        for(Long id : imageIds){
            log.info("image id with {} will deleted", id);
            Image findImage = imageRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.DELAYED_UPLOAD, "업로드 가능 시간이 지났습니다."));
            String url = findImage.getUrl();
            if (!url.contains("/temp/")) {
                continue;
            }


            String oldSource = url.replace(replaceTarget, "");
            String newSource = dir+"/"+objectId+"/"+oldSource.split("/")[1];

            moveImage(oldSource, newSource);
            content = content.replace(url, replaceTarget + newSource);
            imageRepository.delete(findImage);
        }

        return content;
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
    @Transactional
    public void deleteFileByUrl(String url) {
        try {
            String keyName = getDecodedUrl(url.replace(replaceTarget, ""));
            boolean isObjectExist = amazonS3Client.doesObjectExist(bucket, keyName);
            if (isObjectExist) {
                log.info("{} exists", keyName);
                deleteS3(keyName);
            }
        } catch (Exception e) {
            log.debug("Delete File failed", e);
        }
    }

    @Transactional
    // TODO async 적용하기
    public void removeLogImages() {
        // 1. db에서 하루가 지난 log urls조회
        LocalDateTime stdTime = LocalDateTime.now().minusDays(1L);
        List<String> targetImageUrls = imageRepository.findByCreatedDateBefore(stdTime).stream()
            .map(image -> image.getUrl()).collect(Collectors.toList());

        // 2. logUrls들을 delete
        targetImageUrls.forEach((url)->{
            deleteFileByUrl(url);
            imageRepository.deleteByUrl(url);
        });

    }
}

