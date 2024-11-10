package com.omniguard.ai.riskdetector.service.detector;

import com.omniguard.ai.riskdetector.config.enumeration.capability.CapabilityLocation;
import com.omniguard.ai.riskdetector.config.enumeration.capability.CapabilityType;
import com.omniguard.ai.riskdetector.model.DetectorObject;
import com.omniguard.ai.riskdetector.model.chat.ChatMessage;
import com.omniguard.ai.riskdetector.service.OssService;
import com.omniguard.ai.riskdetector.service.prompt.FunctionCallService;
import com.omniguard.ai.riskdetector.service.prompt.SystemPromptService;
import com.omniguard.ai.riskdetector.service.task.ServerTaskService;
import com.omniguard.ai.riskdetector.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RiskDetectorService {
    @Autowired
    FunctionCallService functionCallService;
    @Autowired
    SystemPromptService systemPromptService;
    @Autowired
    ServerTaskService serverTaskService;
    @Autowired
    OssService ossService;



    // 多模态检测-合并多个特征数据
    public Map<String,Object> detectMultimodaContent(List<DetectorObject> detectorObjects) throws IOException, InterruptedException {
        StringBuilder extendPrompt = new StringBuilder(); //补充提示
        StringBuilder contentPrompt = new StringBuilder(); // 实际内容
        // 创建任务，获取各类数据特征
        List<String> taskIds = new ArrayList<>();
        for (DetectorObject detectorObject : detectorObjects) {
            switch (detectorObject.getType()){
                case IMAGE :
                    if(StringUtil.isNotEmpty(detectorObject.getFileKey())){
                        // 如果对象键存在，默认以对象键中内容为主要内容
                        if(StringUtil.isNotEmpty(detectorObject.getContent())){
                            // 同时指定对图像的描述
                            Map<String, Object> chatRequest = generatePicDescriptionChatRequest(detectorObject.getFileKey(),detectorObject.getContent()); // 带描述版本
                            String taskId = serverTaskService.assignTask("detectMultimodaContent.getFeature.Image", "Extract the feature of the image.", CapabilityType.LLM_IMAGE, CapabilityLocation.REMOTE, chatRequest);
                            taskIds.add(taskId);
                        }else{
                            Map<String, Object> stringObjectMap = generatePicDescriptionChatRequest(detectorObject.getFileKey()); // 不带描述版本
                            String taskId = serverTaskService.assignTask("detectMultimodaContent.getFeature.Image", "Extract the feature of the image.", CapabilityType.LLM_IMAGE, CapabilityLocation.REMOTE, stringObjectMap);
                            taskIds.add(taskId);
                        }
                    }else {
                        // TODO 图像没有对象键，直接报错
                    }
                    break;
                case TEXT :
                    // 如果对象键存在，默认以对象键中内容为主要内容
                    String content;
                    if(StringUtil.isNotEmpty(detectorObject.getFileKey())){
                        // 如果对象键存在，默认以对象键中内容为主要内容
                        // 下载文件
                        ossService.downloadFile(detectorObject.getFileKey(), "detectMultimodaContent.txt");
                        // 读取文件内容为字符串
                        content = Files.readString(Paths.get("detectMultimodaContent.txt")); // TODO 把这个合并到prompt中，因为不是任务所以得单独存
                        if(StringUtil.isNotEmpty(detectorObject.getContent())){
                            contentPrompt.append("###\n数据类型：文本\n数据正文：").append(content).append("\n").append("数据补充描述：").append(detectorObject.getContent()).append("\n###");
                        }else{
                            contentPrompt.append("###\n数据类型：文本\n数据正文：").append(content).append("\n###\n");
                        }

                    }else{
                        content = detectorObject.getContent();
                        contentPrompt.append("###\n数据类型：文本\n数据正文：").append(content).append("\n###\n");
                    }

                    break;
                case AUDIO:
                    if(StringUtil.isNotEmpty(detectorObject.getFileKey())){
                        Map<String, Object> audioDescriptionMap = getAudioDescriptionMap(detectorObject.getFileKey());
                        String taskId = serverTaskService.assignTask("detectMultimodaContent.getFeature.Audio", "Extract the feature of the audio.", CapabilityType.LLM_AUDIO, CapabilityLocation.REMOTE, audioDescriptionMap);
                        taskIds.add(taskId);
                    }
                    else{
                        // TODO 音频没有对象键，直接报错
                    }
                    break;
                case VIDEO:
                    if(StringUtil.isNotEmpty(detectorObject.getFileKey())){
                        Map<String, Object> audioDescriptionMap = getVideoDescriptionMap(detectorObject.getFileKey());
                        String taskId = serverTaskService.assignTask("detectMultimodaContent.getFeature.Video", "Extract the feature of the video.", CapabilityType.LLM_VIDEO, CapabilityLocation.REMOTE, audioDescriptionMap);
                        taskIds.add(taskId);
                    }
                    else{
                        // TODO 视频没有对象键，直接报错
                    }
                    break;
                case PROMPT:
                    extendPrompt.append(detectorObject.getContent()).append("\n");
                    break;
                case null, default:
                    // TODO 报错，该内容未指定数据类型
                    break;
            }

        }
        // 任务下发完毕，现在进行汇总
        boolean finished=false;
        while (!finished && !taskIds.isEmpty()){
            for (String taskId : taskIds) {
                finished = (serverTaskService.getTaskResult(taskId)!=null);
                // 有一个失败，直接重试
                if(!finished){
                    break;
                }
            }
            Thread.sleep(500);
        }
        contentPrompt.append(extendPrompt).append("\n");
        // 确保是全部完成了的，现在批量取回执行结果，并拼接提示词
        for(String taskId : taskIds){
            Map<String, Object> taskResult = serverTaskService.getTaskResult(taskId);
            String description = (String) taskResult.get("description");
            // TODO 加上数据类型的描述
            contentPrompt.append("###\n数据类型：").append("null").append("\n数据特征描述：\n").append(description).append("\n###\n");
        }
        // 提示词构建完毕，直接执行并等待返回结果
        return detectSingleText(contentPrompt.toString());
    }

    // 单文本检测
    public Map<String, Object> detectSingleText(String textContent) throws IOException, InterruptedException {
        Map<String, Object> modelChatRequest = getDetectSingleParameterMap(textContent);
        return serverTaskService.executeTask("DetectSingleText", "Detect and return the risk analysis of the content.", CapabilityType.LLM_CHAT, CapabilityLocation.REMOTE, modelChatRequest);
    }

    public Map<String, Object> getDetectSingleParameterMap(String textContent) {
        String systemPrompt = systemPromptService.getSystemPromptByName("riskdetector_system", new HashMap<>());

        Map<String,String> params = new HashMap<>();
        params.put("TextContent", textContent);
        params.put("FunctionCall", "riskDetectionResult");
        String userPrompt = systemPromptService.getSystemPromptByName("riskdetector_user",params);
        Map<String, Object> riskDetectionResult = functionCallService.getToolByName("riskDetectionResult");
        List<ChatMessage> chatSession = new ArrayList<>();
        chatSession.add(new ChatMessage("system", systemPrompt));
        chatSession.add(new ChatMessage("user", userPrompt));
        Map<String,Object> modelChatRequest = new HashMap<>();
        modelChatRequest.put("messages",chatSession); // 置入消息（Message）
        modelChatRequest.put("tools",riskDetectionResult);
        return modelChatRequest;
    }


    // 提取图片特征
    public Map<String, Object> getPicDescription(String objectKey) throws IOException, InterruptedException {
        Map<String, Object> modelChatRequest = generatePicDescriptionChatRequest(objectKey);
        return serverTaskService.executeTask("getPicDescription", "Extract the feature of the picture.", CapabilityType.LLM_IMAGE, CapabilityLocation.REMOTE, modelChatRequest);
    }

    public Map<String, Object> generatePicDescriptionChatRequest(String objectKey) {
        String systemPrompt = systemPromptService.getSystemPromptByName("riskdetector_system", new HashMap<>());
        Map<String,String> params = new HashMap<>();
        String userPrompt = systemPromptService.getSystemPromptByName("riskdetector_imagefeature",params);
        //Map<String, Object> sendAnalyseResult = functionCallService.getToolByName("send_analyse_result");
        Map<String,Object> modelChatRequest = new HashMap<>();
        modelChatRequest.put("image",ossService.getRealURL(objectKey));
        modelChatRequest.put("system",systemPrompt);
        modelChatRequest.put("user",userPrompt);
        //List<ChatMessage> chatSession = new ArrayList<>();
        //chatSession.add(new ChatMessage("system", systemPrompt));
        //chatSession.add(new ChatMessage("user", userPrompt));
        //chatSession.add(new ChatMessage("picture", ossService.getRealURL(objectKey))); // 注意在计算端转换
        //modelChatRequest.put("tools",sendAnalyseResult);
        return modelChatRequest;
    }

    public Map<String, Object> generatePicDescriptionChatRequest(String objectKey,String description) {
        String systemPrompt = systemPromptService.getSystemPromptByName("riskdetector_system", new HashMap<>());
        Map<String,String> params = new HashMap<>();
        params.put("description",description);
        String userPrompt = systemPromptService.getSystemPromptByName("riskdetector_imagefeature_includedescription",params);
        //Map<String, Object> sendAnalyseResult = functionCallService.getToolByName("send_analyse_result");
        Map<String,Object> modelChatRequest = new HashMap<>();
        modelChatRequest.put("image",ossService.getRealURL(objectKey));
        modelChatRequest.put("system",systemPrompt);
        modelChatRequest.put("user",userPrompt);
        //List<ChatMessage> chatSession = new ArrayList<>();
        //chatSession.add(new ChatMessage("system", systemPrompt));
        //chatSession.add(new ChatMessage("user", userPrompt));
        //chatSession.add(new ChatMessage("picture", ossService.getRealURL(objectKey))); // 注意在计算端转换
        //modelChatRequest.put("tools",sendAnalyseResult);
        return modelChatRequest;
    }


    // 提取音频特征
    public Map<String, Object> getAudioDescription(String objectKey) throws IOException, InterruptedException {
        Map<String, Object> modelChatRequest = getAudioDescriptionMap(objectKey);
        //List<ChatMessage> chatSession = new ArrayList<>();
        //chatSession.add(new ChatMessage("system", systemPrompt));
        //chatSession.add(new ChatMessage("user", userPrompt));
        //chatSession.add(new ChatMessage("picture", ossService.getRealURL(objectKey))); // 注意在计算端转换
        //modelChatRequest.put("tools",sendAnalyseResult);
        return serverTaskService.executeTask("getAudioDescription", "Extract the feature of the audio.", CapabilityType.LLM_AUDIO, CapabilityLocation.REMOTE, modelChatRequest);
    }

    public Map<String, Object> getAudioDescriptionMap(String objectKey) {
        String systemPrompt = systemPromptService.getSystemPromptByName("riskdetector_system", new HashMap<>());
        Map<String,String> params = new HashMap<>();
        String userPrompt = systemPromptService.getSystemPromptByName("riskdetector_audiofeature",params);
        //Map<String, Object> sendAnalyseResult = functionCallService.getToolByName("send_analyse_result");
        Map<String,Object> modelChatRequest = new HashMap<>();
        modelChatRequest.put("audio",ossService.getRealURL(objectKey));
        modelChatRequest.put("system",systemPrompt);
        modelChatRequest.put("user",userPrompt);
        return modelChatRequest;
    }

    // 提取视频特征
    public Map<String, Object> getVideoDescription(String objectKey) throws IOException, InterruptedException {
        Map<String, Object> modelChatRequest = getVideoDescriptionMap(objectKey);
        //List<ChatMessage> chatSession = new ArrayList<>();
        //chatSession.add(new ChatMessage("system", systemPrompt));
        //chatSession.add(new ChatMessage("user", userPrompt));
        //chatSession.add(new ChatMessage("picture", ossService.getRealURL(objectKey))); // 注意在计算端转换
        //modelChatRequest.put("tools",sendAnalyseResult);
        return serverTaskService.executeTask("getVideoDescription", "Extract the feature of the video.", CapabilityType.LLM_VIDEO, CapabilityLocation.REMOTE, modelChatRequest);
    }

    private Map<String, Object> getVideoDescriptionMap(String objectKey) {
        String systemPrompt = systemPromptService.getSystemPromptByName("riskdetector_system", new HashMap<>());
        Map<String,String> params = new HashMap<>();
        String userPrompt = systemPromptService.getSystemPromptByName("riskdetector_videofeature",params);
        //Map<String, Object> sendAnalyseResult = functionCallService.getToolByName("send_analyse_result");
        Map<String,Object> modelChatRequest = new HashMap<>();
        modelChatRequest.put("video",ossService.getRealURL(objectKey));
        modelChatRequest.put("system",systemPrompt);
        modelChatRequest.put("user",userPrompt);
        return modelChatRequest;
    }

    public Map<String,Object> getVideoAudio(String objectKey) throws IOException, InterruptedException {

        Map<String,Object> request = new HashMap<>();
        request.put("video",objectKey);
        return serverTaskService.executeTask("getVideoAudio", "Extract the audio file of the video.", CapabilityType.AUDIO_DIV, CapabilityLocation.LOCAL, request);
    }
}
