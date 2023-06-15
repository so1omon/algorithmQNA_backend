package algorithm_QnA_community.algorithm_QnA_community.api.controller.s3;

import algorithm_QnA_community.algorithm_QnA_community.api.service.s3.S3Service;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.s3
 * fileName       : S3ApiController
 * author         : solmin
 * date           : 2023/06/01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/06/01        solmin       최초 생성
 * 2023/06/15        solmin       entrypoint 수정
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/upload")
@Slf4j
public class S3ApiController {
    private final S3Service s3Service;


    @PostMapping
    public Res<ImageUploadRes> uploadPostImage(@RequestParam("file") MultipartFile file){

        ImageUploadRes result = s3Service.uploadImage(file);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "이미지 업로드 완료"), result);
    }

}
