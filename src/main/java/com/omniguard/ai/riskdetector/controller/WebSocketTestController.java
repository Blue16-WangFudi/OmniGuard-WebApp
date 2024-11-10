package com.omniguard.ai.riskdetector.controller;


import com.omniguard.ai.riskdetector.config.enumeration.capability.CapabilityLocation;
import com.omniguard.ai.riskdetector.config.enumeration.capability.CapabilityType;
import com.omniguard.ai.riskdetector.model.chat.ChatMessage;
import com.omniguard.ai.riskdetector.service.task.ServerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/test")
public class WebSocketTestController {
    @Autowired
    private ServerTaskService serverTaskService;

    @PostMapping()
    public Map<String, Object> sendTask() throws IOException, InterruptedException {
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("model","qwen-max");
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user","你好"));
        parameters.put("messages",messages);

        // 备注：能力类型和执行位置用于筛选出符合条件的
        return serverTaskService.executeTask("TestTask","This is test task.", CapabilityType.LLM_CHAT, CapabilityLocation.REMOTE,parameters);
    }

    @PostMapping("/video")
    public Map<String, Object> video() throws IOException, InterruptedException {
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("object_video","exampledir/long.mp4");
        parameters.put("folder_frames","video/frames/"+ UUID.randomUUID()+"/");
        parameters.put("threshold",10);

        // 备注：能力类型和执行位置用于筛选出符合条件的
        return serverTaskService.executeTask("TestTask","This is test task.", CapabilityType.VIDEO_FRAME, CapabilityLocation.LOCAL,parameters);
    }

}
