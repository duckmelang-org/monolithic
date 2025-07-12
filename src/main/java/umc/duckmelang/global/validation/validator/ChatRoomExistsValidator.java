package umc.duckmelang.global.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import umc.duckmelang.domain.chatroom.domain.ChatRoom;
import umc.duckmelang.domain.chatroom.service.ChatRoomQueryService;
import umc.duckmelang.global.apipayload.code.status.ErrorStatus;
import umc.duckmelang.global.validation.annotation.ExistsChatRoom;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatRoomExistsValidator implements ConstraintValidator<ExistsChatRoom, Long> {

    private final ChatRoomQueryService chatRoomQueryService;

    @Override
    public void initialize(ExistsChatRoom constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long chatRoomId, ConstraintValidatorContext constraintValidatorContext) {
        Optional<ChatRoom> target = chatRoomQueryService.findChatRoom(chatRoomId);

        if (target.isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorStatus.CHATROOM_NOT_FOUND.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
