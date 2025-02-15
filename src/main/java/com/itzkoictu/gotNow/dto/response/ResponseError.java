package com.itzkoictu.gotNow.dto.response;

public class ResponseError extends ApiResponse {
    public ResponseError(int status, String message) {
        super(status,message);
    }
}
