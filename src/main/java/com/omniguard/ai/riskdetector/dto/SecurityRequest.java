// SecurityRequest.java
package com.omniguard.ai.riskdetector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用的API请求包装类
 * 用于封装数据以及身份验证令牌，以便进行安全验证。
 *
 * @param <T> 请求中包含的数据对象的类型.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityRequest<T> {
    /**
     * 身份验证token.
     */
    private String token;

    /**
     * 请求数据对象.
     */
    private T data;
}
