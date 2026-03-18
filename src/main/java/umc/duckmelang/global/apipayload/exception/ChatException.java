package umc.duckmelang.global.apipayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.duckmelang.global.apipayload.code.BaseErrorCode;
import umc.duckmelang.global.apipayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class ChatException extends RuntimeException {
    private BaseErrorCode errorCode;

    public ErrorReasonDTO getReason() {
        return this.errorCode.getReason();
    }
    public ErrorReasonDTO getReasonHttpStatus() {
        return this.errorCode.getReasonHttpStatus();
    }
}
