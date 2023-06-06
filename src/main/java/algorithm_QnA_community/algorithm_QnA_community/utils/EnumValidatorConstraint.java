package algorithm_QnA_community.algorithm_QnA_community.utils;

import algorithm_QnA_community.algorithm_QnA_community.utils.annotation.EnumValidator;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.utils
 * fileName       : EnumValidatorConstraint
 * author         : solmin
 * date           : 2023/05/09
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/09        solmin       최초 생성
 *                                EnumValidator 구현체
 */
@Slf4j
public class EnumValidatorConstraint implements ConstraintValidator<EnumValidator, String> {

    Set<String> values;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        values = Stream.of(constraintAnnotation.target().getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return values.contains(value);
    }
}
