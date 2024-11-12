package com.omniguard.ai.riskdetector.controller;

import com.omniguard.ai.riskdetector.config.enumeration.ResultCode;
import com.omniguard.ai.riskdetector.dto.ResultResponse;
import com.omniguard.ai.riskdetector.dto.SecurityRequest;
import com.omniguard.ai.riskdetector.dto.request.DetectorRequest;
import com.omniguard.ai.riskdetector.model.DetectorObject;
import com.omniguard.ai.riskdetector.service.SecurityService;
import com.omniguard.ai.riskdetector.service.detector.AiDetectorService;
import com.omniguard.ai.riskdetector.service.detector.RiskDetectorService;
import com.omniguard.ai.riskdetector.service.storage.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v3/detector/multimodal")
public class DetectorController {

    @Autowired
    RiskDetectorService riskDetectorService;

    @Autowired
    AiDetectorService aiDetectorService;

    @Autowired
    SecurityService securityService;

    @Autowired
    ResultService resultService;

    // 大任务：AI判定、风险判定、AI换脸检测

    // AI检测
    @PostMapping("/ai")
    public ResultResponse<?> multimodalAiDetector(@RequestBody SecurityRequest<DetectorRequest> securityRequest) throws IOException, InterruptedException {
        // 权限校验
        if (!securityService.checkAccess(securityRequest, "ominguard.detectorcontroller.multimodal.ai")) {
            return new ResultResponse<>(ResultCode.UNAUTHORIZED, "无权访问接口", null);
        }
        DetectorRequest detectorRequest = securityRequest.getData();
        List<DetectorObject> objects = detectorRequest.getObjects();
        //识别
        Map<String, Object> stringObjectMap = aiDetectorService.detectMultimodaContent(objects);
        String id = resultService.saveResult("omniguard.aidection", detectorRequest.getCity(), detectorRequest.getPhoneNum(), stringObjectMap);
        return new ResultResponse<>(ResultCode.SUCCESS,id,stringObjectMap);
    }

    // 风险检测-多模态
    @PostMapping("/risk")
    public ResultResponse<?> multimodalRiskDetector(@RequestBody SecurityRequest<DetectorRequest> securityRequest) throws IOException, InterruptedException {
        // 权限校验
        if (!securityService.checkAccess(securityRequest, "ominguard.detectorcontroller.multimodal.risk")) {
            return new ResultResponse<>(ResultCode.UNAUTHORIZED, "无权访问接口", null);
        }
        DetectorRequest detectorRequest = securityRequest.getData();
        List<DetectorObject> objects = detectorRequest.getObjects();
        //识别
        Map<String, Object> stringObjectMap = riskDetectorService.detectMultimodaContent(objects);
        String id = resultService.saveResult("omniguard.riskdection", detectorRequest.getCity(), detectorRequest.getPhoneNum(), stringObjectMap);
        return new ResultResponse<>(ResultCode.SUCCESS,id,stringObjectMap);
    }

    @PostMapping("/deepfake")
    public ResultResponse<?> deepfake(@RequestBody SecurityRequest<DetectorRequest> securityRequest){
        return new ResultResponse<>(ResultCode.SUCCESS,"成功",null);
    }

    // 风险检测-纯文本
    @PostMapping("/risk/text")
    public ResultResponse<?> textRiskDetector(@RequestBody SecurityRequest<String> securityRequest) throws IOException, InterruptedException {
        Map<String, Object> stringObjectMap = riskDetectorService.detectSingleText(securityRequest.getData());
        return new ResultResponse<>(ResultCode.SUCCESS,"成功",stringObjectMap);
    }
}
