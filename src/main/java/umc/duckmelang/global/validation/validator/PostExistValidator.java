package umc.duckmelang.global.validation.validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.post.domain.Post;
import umc.duckmelang.domain.post.service.post.PostQueryService;
import umc.duckmelang.global.validation.annotation.ExistPost;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostExistValidator implements ConstraintValidator<ExistPost, Long> {
    private final PostQueryService postQueryService;

    @Override
    public void initialize(ExistPost constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long postId, ConstraintValidatorContext context) {
        Optional<Post> target = postQueryService.findById(postId);

        if (target.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.POST_NOT_FOUND.toString()).addConstraintViolation();
            return false;
        }

        return true;
    }
}
