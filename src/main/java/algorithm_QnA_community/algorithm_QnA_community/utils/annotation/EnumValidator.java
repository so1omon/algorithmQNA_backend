package algorithm_QnA_community.algorithm_QnA_community.utils.annotation;

import algorithm_QnA_community.algorithm_QnA_community.utils.EnumValidatorConstraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.utils.annotation
 * fileName       : EnumValidator
 * author         : solmin
 * date           : 2023/05/09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/09        solmin       최초 생성
 *                                Enum validate를 위한 custom annotation
 */
@Documented
@Constraint(validatedBy = EnumValidatorConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NotNull
public @interface EnumValidator {

    String message() default "올바른 ENUM값을 입력하세요.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    Class<? extends Enum<?>> target();
}
