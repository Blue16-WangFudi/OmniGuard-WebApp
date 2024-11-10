package com.omniguard.ai.riskdetector.config.enumeration;



public enum ResultCode {
    SUCCESS(200, "OK"),
    FAILURE(400, "Bad Request"),
    BAD_REQUEST(400, "Bad Request"), // 注意：BAD_REQUEST 和 FAILURE 都映射到同一个状态码
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INVALID_PARAMETER(422, "Unprocessable Entity");

    private final int code;
    private final String reasonPhrase;

    ResultCode(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public int getCode() {
        return code;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public static ResultCode fromCode(int code) {
        for (ResultCode resultCode : values()) {
            if (resultCode.getCode() == code) {
                return resultCode;
            }
        }
        throw new IllegalArgumentException("Unknown result code: " + code);
    }
}
