package com.omniguard.ai.riskdetector.dto;

import com.omniguard.ai.riskdetector.config.enumeration.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用的API响应包装类
 * 这个类封装了API响应的状态码、响应消息和相关数据。
 *
 * @param <T> 响应中包含的对象的数据类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultResponse<T> {
    /**
     * 响应码（参见ResultCode）.
     */
    private ResultCode code;

    /**
     * 响应消息.
     */
    private String msg;

    /**
     * 响应数据对象.
     */
    private T data;
}
