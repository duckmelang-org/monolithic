package umc.duckmelang.global.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.review.domain.Review;
import umc.duckmelang.domain.review.service.ReviewQueryService;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.validation.annotation.ExistsReview;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewExistsValidator implements ConstraintValidator<ExistsReview, Long> {

    private final ReviewQueryService reviewQueryService;

    @Override
    public void initialize(ExistsReview constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long reviewId, ConstraintValidatorContext constraintValidatorContext) {
        Optional<Review> target = reviewQueryService.findReview(reviewId);
        if (target.isEmpty()){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorStatus.REVIEW_NOT_FOUND.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
