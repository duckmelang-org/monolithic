package umc.duckmelang.global.apipayload.exception;

import umc.duckmelang.global.apipayload.code.BaseErrorCode;

public class PostException extends GeneralException {
    public PostException(BaseErrorCode errorCode) {super(errorCode);}
}