package umc.duckmelang.global.apipayload.exception;

import umc.duckmelang.global.apipayload.code.BaseErrorCode;

public class ReviewException extends GeneralException {
    public ReviewException(BaseErrorCode errorCode) {
      super(errorCode);
    }
}
