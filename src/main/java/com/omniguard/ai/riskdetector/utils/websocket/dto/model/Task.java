package com.omniguard.ai.riskdetector.utils.websocket.dto.model;

import com.omniguard.ai.riskdetector.config.enumeration.TaskStatus;
import com.omniguard.ai.riskdetector.config.enumeration.capability.CapabilityLocation;
import com.omniguard.ai.riskdetector.config.enumeration.capability.CapabilityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private String taskId; // 任务id
    private String taskName; // 任务名称
    private String taskDescription; // 任务描述 // TODO 任务记录还要记录capcaityName
    private TaskStatus taskStatus; // 任务状态
    private CapabilityType taskType; // 任务的类型，与能力类型的可选项是一致的
    private CapabilityLocation taskLocation; // 任务的执行位置，与能力的位置的可选项是一致的
    private float taskProgress; //(0,1)表示当前任务进度
    private float taskDuration; //任务持续时间
    private Map<String,Object> taskParameters; // 任务执行参数
}
