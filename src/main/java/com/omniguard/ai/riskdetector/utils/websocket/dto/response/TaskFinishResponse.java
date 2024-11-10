package com.omniguard.ai.riskdetector.utils.websocket.dto.response;


import com.omniguard.ai.riskdetector.config.enumeration.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskFinishResponse {
    private String taskId;
    private TaskStatus taskStatus;
    private Map<String,Object> data;
}
