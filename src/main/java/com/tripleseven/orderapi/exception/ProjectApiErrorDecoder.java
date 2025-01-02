package com.tripleseven.orderapi.exception;


import feign.Response;
import feign.codec.ErrorDecoder;

public class ProjectApiErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
//        switch (response.status()) {
//            case 400:
//                return new CustomException(ErrorCode.BAD_REQUEST);
//            case 403:
//                return new CustomException(ErrorCode.FORBIDDEN);
//            case 404:
//                return new CustomException(ErrorCode.PROJECT_NOT_FOUND);
//            case 500:
//                return new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
//            default:
//                return defaultErrorDecoder.decode(methodKey, response);
//        }
        return null;
    }
}
