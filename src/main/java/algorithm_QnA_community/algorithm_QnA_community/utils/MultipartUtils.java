package algorithm_QnA_community.algorithm_QnA_community.utils;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.utils
 * fileName       : MultipartUtils
 * author         : solmin
 * date           : 2023/05/29
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/29        solmin       최초 생성
 */
public final class MultipartUtils {


    /**
     * 로컬에서의 사용자 홈 디렉토리 경로를 반환합니다.
     */
    public static String getLocalHomeDirectory() {
        return System.getProperty("user.home");
    }

    /**
     * 새로운 파일 고유 ID를 생성합니다.
     * @return yyyyMMDDhhmmss format의 ID
     */
    public static String createFileId() {
        return new SimpleDateFormat("yyyyMMddHHmms").format(new Date());
    }

    /**
     * Multipart 의 ContentType 값에서 / 이후 확장자만 잘라냅니다.
     * @param contentType ex) image/png
     * @return ex) png
     */
    public static String getFormat(String contentType) {
        if (StringUtils.hasText(contentType) && (contentType.equals("image/png")||contentType.equals("image/jpeg"))) {
            return contentType.substring(contentType.lastIndexOf('/') + 1);
        }
        return null;
    }

    /**
     * 파일의 전체 경로를 생성합니다.
     * @param fileId 생성된 파일 고유 ID
     * @param format 확장자
     */
    public static String createFileName(String baseDir, String fileId, String format) {
        return String.format("%s/%s.%s", baseDir, fileId, format);
    }
}
