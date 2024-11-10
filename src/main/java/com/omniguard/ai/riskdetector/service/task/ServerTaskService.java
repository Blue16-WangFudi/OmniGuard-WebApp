package com.omniguard.ai.riskdetector.service.task;


import com.omniguard.ai.riskdetector.config.enumeration.capability.CapabilityLocation;
import com.omniguard.ai.riskdetector.config.enumeration.capability.CapabilityType;
import com.omniguard.ai.riskdetector.utils.websocket.config.enumeration.MsgType;
import com.omniguard.ai.riskdetector.utils.websocket.dto.WebSocketMsg;
import com.omniguard.ai.riskdetector.utils.websocket.dto.model.Capability;
import com.omniguard.ai.riskdetector.utils.websocket.dto.model.ServerInfo;
import com.omniguard.ai.riskdetector.utils.websocket.dto.request.AssignTaskRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static com.omniguard.ai.riskdetector.utils.websocket.config.handler.GlobalWebSocketHandler.*;

@Service
public class ServerTaskService {

    // 自动根据任务情况、性能和网络延迟指派任务，大模型AI辅助负载均衡（根据任务描述决策），然后轮询查询任务结果，查询到了就返回
    public Map<String, Object> executeTask(String taskName, String taskDescription, CapabilityType taskType, CapabilityLocation taskLocation, Map<String, Object> taskParameters) throws IOException, InterruptedException {
        String taskId = assignTask(taskName, taskDescription, taskType, taskLocation, taskParameters);


        // 线程执行代码开始（防止轮询的时候卡死）
        // 创建一个Callable对象
        Callable<Map<String, Object>> callable = () -> {
            Map<String, Object> taskResult = null;
            // 执行一些操作
            while (taskResult == null) {
                taskResult = getTaskResult(taskId);
                Thread.sleep(100);
            }
            return taskResult;
        };

        // 使用FutureTask来包装Callable对象
        FutureTask<Map<String, Object>> futureTask = new FutureTask<>(callable);

        // 创建并启动线程
        Thread thread = new Thread(futureTask);
        thread.start();

        try {
            // 等待线程结束并获取返回值
            return futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        // 线程执行代码结束
        return null;
    }

    // 指派任务（异步调用，需要手动通过getTaskResult进行查询任务执行结果）
    public String assignTask(String taskName, String taskDescription, CapabilityType taskType, CapabilityLocation taskLocation, Map<String, Object> taskParameters) throws IOException {
        // 最优（当前相对最小负载服务器信息）
        double minLoadScore = Double.MAX_VALUE;
        Capability minLoadCapability = null;
        ServerInfo minLoadServerInfo = null;

        // 从可用服务器列表中遍历查找所有可用capabilities
        for(Map.Entry<String, ServerInfo> entry : availableServers_Info.entrySet()) {
            Map<String,Capability> capabilities = entry.getValue().getCapabilities();
            //List<Capability> availableCapabilities = new ArrayList<>();
            for(Map.Entry<String,Capability> capability_entry : capabilities.entrySet()) {
                Capability capability = capability_entry.getValue();
                // 找到某个服务器合适的能力，并添加到列表
                // 解释一下，这里是指的是任务类型必须符合、执行位置的话如果指定就必须符合，如果是OPTIMUM则是自动选择
                if(capability.getCapabilityType().equals(taskType) && (capability.getLocation().equals(taskLocation)||capability.getLocation().equals(CapabilityLocation.OPTIMUM))) {
                    // 获取对应服务器信息
                    String serverId = capability.getServerId();
                    ServerInfo serverInfo = availableServers_Info.get(serverId);
                    // 加权参数，网络和性能延迟，暂时不做
                    // TODO 这里实现算法：先暂时以任务队列长度为例，有时间就把其他加权
                    int taskCount = capability.getTaskQueue().size();
                    double network = serverInfo.getNetwork();
                    int performance = serverInfo.getPerformance();
                    // 计算负载得分（越小越好）
                    double loadScore = taskCount+0*performance+0*network;
                    if(loadScore < minLoadScore) {
                        // 找到更优的，更新目标参数
                        minLoadCapability = capability;
                        minLoadServerInfo = serverInfo;
                    }
                }
            }

        }
        // 找到了，创建一个新的任务
        String taskId = UUID.randomUUID().toString();
        AssignTaskRequest assignTaskRequest = new AssignTaskRequest(taskId, taskName, taskDescription,minLoadCapability.getName(), taskParameters);
        // 找到对应session
        WebSocketSession webSocketSession = availableServers_Session.get(minLoadCapability.getServerId());
        webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(new WebSocketMsg<>(MsgType.TASK_ASSIGNMENT,assignTaskRequest))));
        return taskId;
    }

    // 轮询的方式进行查询，无论是成功还是失败均会返回任务执行结果(失败返回null)
    public Map<String,Object> getTaskResult(String taskId) throws IOException {
        return taskResults.get(taskId);
    }

    public void broadcastMessage(String message) {
        for (WebSocketSession session : webSocketSessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    // 处理异常
                    e.printStackTrace();
                }
            }
        }
    }



}
