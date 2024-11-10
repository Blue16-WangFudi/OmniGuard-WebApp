package com.omniguard.ai.riskdetector.utils.websocket.dto.request;

import com.omniguard.ai.riskdetector.config.enumeration.capability.CapabilityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignTaskRequest {
    private String taskId; // 任务Id
    private String taskName; // 任务名称
    private String taskDescription; // 任务描述
    private String capabilityName; // 注册的能力名称，注意不能重复
    private Map<String,Object> taskParameters; // 任务执行参数
}
