package com.omniguard.ai.riskdetector.utils.websocket.dto.response;


import com.omniguard.ai.riskdetector.utils.websocket.dto.model.ServerInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResultResponse {
    private boolean success;
    private String message;
    private ServerInfo serverInfo; // 返回当前注册的服务器信息
}
