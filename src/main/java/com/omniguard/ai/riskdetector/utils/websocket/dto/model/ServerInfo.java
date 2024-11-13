package com.omniguard.ai.riskdetector.utils.websocket.dto.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document(collection = "server_status")
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfo {
    // 服务器静态属性
    @Id
    private String id;
    private String serverId; // 服务器唯一标识符，建议每一个服务器为一个固定的UUID
    private String serverName; // 服务器名称，用于显示在主界面上

    // 负载均衡相关参数
    private Double network;// 网络延迟，越小越好
    private Integer performance;// 性能指数，越大越好

    // 服务器动态属性，实时情况
    private Map<String,Capability> capabilities;
    private Map<String,Object> systemInfo;
}
