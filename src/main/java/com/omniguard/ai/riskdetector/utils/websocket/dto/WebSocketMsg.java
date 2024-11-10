package com.omniguard.ai.riskdetector.utils.websocket.dto;

import com.omniguard.ai.riskdetector.utils.websocket.config.enumeration.MsgType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMsg<T> {
    private MsgType type;
    private T data;
}
