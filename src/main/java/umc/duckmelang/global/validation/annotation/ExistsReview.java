package umc.duckmelang.global.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import umc.duckmelang.global.validation.validator.ReviewExistsValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReviewExistsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsReview {
    String message() default "해당하는 동행후기가 존재하지 않습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
