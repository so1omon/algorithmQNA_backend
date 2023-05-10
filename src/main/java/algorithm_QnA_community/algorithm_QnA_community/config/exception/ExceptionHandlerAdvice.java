package algorithm_QnA_community.algorithm_QnA_community.config.exception;

import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatusWithBadRequest;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.config.Exception
 * fileName       : ErrorRestControllerAdvice
 * author         : solmin
 * date           : 2023/05/05
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/05        solmin       최초 생성
 *                                EntityNotFoundException (특정 id 질의결과 없음) NotFound 처리
 *                                ErrorCode를 담고 있는 CustomException 처리
 *                                MethodArgumentNotValidException (검증오류) 처리
 *
 *
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler(value = EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected Res EntityNotFoundExceptionHandler(EntityNotFoundException e) {
        DefStatus defStatus = new DefStatus(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return Res.res(defStatus);
    }

    @ExceptionHandler(value = CustomException.class)
    protected ResponseEntity<Res> CustomExceptionHandler(CustomException e) {
        DefStatus defStatus;

        if(e.getErrorCode().getStatus()==HttpStatus.BAD_REQUEST){
            defStatus = new DefStatusWithBadRequest(e.getErrorCode().getCode(), e.getMessage(), false);
        }else{
            defStatus = new DefStatus(e.getErrorCode().getCode(), e.getMessage());
        }
        return new ResponseEntity<>(Res.res(defStatus), e.getErrorCode().getStatus());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Res<List<ValidationErr>> validException(MethodArgumentNotValidException e) {
        List<ValidationErr> validationErrs = new ArrayList<>();

        e.getBindingResult().getAllErrors().forEach(error->{
            validationErrs.add(new ValidationErr(((FieldError) error).getField(), error.getDefaultMessage()));
        });

        DefStatus defStatus = new DefStatusWithBadRequest(HttpStatus.BAD_REQUEST.value(),
            "입력값 중 검증에 실패한 값이 있습니다.",true);

        return Res.res(defStatus, validationErrs);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Res invalidRequestBody(Exception e) {
        log.info(e.getMessage());
        DefStatus defStatus = new DefStatusWithBadRequest(HttpStatus.BAD_REQUEST.value(),
            "Request Body를 올바른 형식으로 보내주세요.",false);

        return Res.res(defStatus);
    }
    @Data
    private static class ValidationErr{
        private String field;
        private String message;

        public ValidationErr(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
