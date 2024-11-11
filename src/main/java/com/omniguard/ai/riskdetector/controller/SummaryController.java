package com.omniguard.ai.riskdetector.controller;


import com.omniguard.ai.riskdetector.config.enumeration.ResultCode;
import com.omniguard.ai.riskdetector.dto.ResultResponse;
import com.omniguard.ai.riskdetector.dto.SecurityRequest;
import com.omniguard.ai.riskdetector.dto.request.DetectorRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v3/summary")
public class SummaryController {
    // 返回检测报告的下载地址（生成检测报告）

    // 无论是什么检测，只需要提交
    @PostMapping("/query")
    public ResultResponse<?> multimodalAiDetector(@RequestBody SecurityRequest<DetectorRequest> securityRequest){
        return new ResultResponse<>(ResultCode.SUCCESS,"成功",null);
    }

}
