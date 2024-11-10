package com.omniguard.ai.riskdetector.utils.websocket.dto.model;

import com.omniguard.ai.riskdetector.config.enumeration.capability.CapabilityLocation;
import com.omniguard.ai.riskdetector.config.enumeration.capability.CapabilityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Capability{
    private String serverId; //能力所属服务器id，注册服务时无需提交会自动添加
    private CapabilityType capabilityType; // 注册的能力类型
    private CapabilityLocation location; // 能力所在地点（本地还是云端，本地的话，耗时更长;云端速度更快）
    private String name; // 注册的能力名称，注意不能重复
    private Map<String,Task> taskQueue; // 当前能力的任务队列
}
