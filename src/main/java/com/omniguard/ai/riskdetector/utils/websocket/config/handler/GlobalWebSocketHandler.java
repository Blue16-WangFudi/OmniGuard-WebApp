package com.omniguard.ai.riskdetector.utils.websocket.config.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omniguard.ai.riskdetector.repository.ServerStatusRepository;
import com.omniguard.ai.riskdetector.utils.websocket.config.enumeration.MsgType;
import com.omniguard.ai.riskdetector.utils.websocket.dto.WebSocketMsg;
import com.omniguard.ai.riskdetector.utils.websocket.dto.model.ServerInfo;
import com.omniguard.ai.riskdetector.utils.websocket.dto.response.RegisterResultResponse;
import com.omniguard.ai.riskdetector.utils.websocket.dto.response.TaskFinishResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GlobalWebSocketHandler extends TextWebSocketHandler {
    // 存储当前已经产生的会话
    public static final Set<WebSocketSession> webSocketSessions = new HashSet<>();
    // 存储当前可用的服务器信息+服务器会话，可通过serverId查询d4c051c3-c130-4408-941a-9bc8da1e8e39
    public static final Map<String, ServerInfo> availableServers_Info = new ConcurrentHashMap<>();
    public static final Map<String, WebSocketSession> availableServers_Session = new ConcurrentHashMap<>();
    // 方便通过session查询serverId
    public static final Map<WebSocketSession, String> availableServers_Session_rev = new ConcurrentHashMap<>();
    // 存储当前收到的响应（任务完成）
    public static final Map<String, Map<String,Object>> taskResults = new ConcurrentHashMap<>();
    // json转换相关
    public static final ObjectMapper objectMapper = new ObjectMapper();

    private ServerStatusRepository serverStatusRepository;

    @Autowired
    public GlobalWebSocketHandler(ServerStatusRepository serverStatusRepository) {
        this.serverStatusRepository = serverStatusRepository;
    }


    // 当WebSocket连接建立时调用
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        session.setTextMessageSizeLimit(10 * 1024 * 1024); // 例如设置为10MB
        webSocketSessions.add(session);
    }
    // 当WebSocket连接关闭时调用
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        // 可以在这里做一些清理工作
        webSocketSessions.remove(session); // 移除已关闭的连接
        String serverId = availableServers_Session_rev.get(session);
        availableServers_Session.remove(session);
        availableServers_Info.remove(serverId);
        availableServers_Session_rev.remove(session);
    }

    // 当出现异常时调用
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    // 当接收到客户端的消息时调用（通讯的核心）
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获取消息信息
        String payload = message.getPayload();
        // 将这个请求反序列化为一个java对象
        WebSocketMsg<?> webSocketMsg = objectMapper.readValue(payload, WebSocketMsg.class);
        switch (webSocketMsg.getType()) {
            case HANDSHAKE_REQUEST:
                registerServer(payload,session);
                break;
            case TASK_COMPLETED:
                getResult(payload,session);
                break;
            case HANDSHAKE_REMOVE:
                removeServer(payload,session);
                break;
            case null, default:
                // 未知命令
                sendMsg(session,MsgType.OTHER,"Unknown Command.");
        }
    }
    public static void sendMsg(WebSocketSession session, MsgType msgType,Object data) throws IOException {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new WebSocketMsg<>(msgType,data))));
    }
    public void getResult(String payload,WebSocketSession session) throws IOException {
        // TODO 要获取taskId，然后通过instances of 判断是什么类型最后转型，然后添加到结果Map中
        // 使用 TypeReference 指定具体的泛型类型
        WebSocketMsg<TaskFinishResponse> webSocketMsg = objectMapper.readValue(payload, new TypeReference<>() {});
        TaskFinishResponse result = webSocketMsg.getData();
        taskResults.put(result.getTaskId(),result.getData());

    }
    public void registerServer(String payload, WebSocketSession session) throws IOException {
        // 使用 TypeReference 指定具体的泛型类型
        WebSocketMsg<ServerInfo> webSocketMsg = objectMapper.readValue(payload, new TypeReference<>() {});
        // 获取 serverInfo
        ServerInfo serverInfo = webSocketMsg.getData();
        // 获取serverInfo和当前serverStatus还有能力列表
        String serverId = serverInfo.getServerId();

        // 注册到两个map中
        availableServers_Info.put(serverId, serverInfo);
        availableServers_Session.put(serverId, session);
        availableServers_Session_rev.put(session, serverId);

        // 保存到数据库
        serverStatusRepository.save(serverInfo);

        // 默认是注册成功
        sendMsg(session,MsgType.RESPONSE,new RegisterResultResponse(true,"Registered and update client info successfully",serverInfo));


    }

    public void removeServer(String payload,WebSocketSession session) throws IOException {
        // 使用 TypeReference 指定具体的泛型类型
        WebSocketMsg<String> webSocketMsg = objectMapper.readValue(payload, new TypeReference<>() {});
        String serverId = webSocketMsg.getData();
        // 移除这个服务
        availableServers_Info.remove(serverId);
        availableServers_Session.remove(serverId);
        sendMsg(session,MsgType.RESPONSE,new RegisterResultResponse(true,"Remove client successfully.",null));
    }


}
