package algorithm_QnA_community.algorithm_QnA_community.api.service.s3;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.member.MemberProfileDto;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.repository.MemberRepository;
import algorithm_QnA_community.algorithm_QnA_community.utils.MultipartUtils;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3Client amazonS3Client;
    private final MemberRepository memberRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final String BASE_DIR = "member";


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


            String fileId = MultipartUtils.createFileId();

            String fileName = MultipartUtils.createFileName(BASE_DIR, fileId, format);
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


}
