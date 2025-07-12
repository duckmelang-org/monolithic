package umc.duckmelang.global.validation.annotation;

import jakarta.validation.Constraint;
import umc.duckmelang.global.validation.validator.ChatRoomExistsValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ChatRoomExistsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsChatRoom {
}
