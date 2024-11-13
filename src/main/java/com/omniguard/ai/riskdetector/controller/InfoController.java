package com.omniguard.ai.riskdetector.controller;


import com.omniguard.ai.riskdetector.config.enumeration.ResultCode;
import com.omniguard.ai.riskdetector.dto.ResultResponse;
import com.omniguard.ai.riskdetector.dto.SecurityRequest;
import com.omniguard.ai.riskdetector.dto.request.DetectorRequest;
import com.omniguard.ai.riskdetector.model.storage.DetectionResult;
import com.omniguard.ai.riskdetector.repository.ServerStatusRepository;
import com.omniguard.ai.riskdetector.service.SecurityService;
import com.omniguard.ai.riskdetector.service.storage.ResultService;
import com.omniguard.ai.riskdetector.utils.websocket.dto.model.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v3/info")
public class InfoController {

    @Autowired
    SecurityService securityService;

    @Autowired
    ResultService resultService;

    @Autowired
    ServerStatusRepository serverStatusRepository;


    // 提交服务器的标识符，返回服务器状态信息的集合。可以指定查询时间段。
    @PostMapping("/server/status")
    public ResultResponse<?> getServerInfo(@RequestBody SecurityRequest<ServerInfo> securityRequest){
        // 权限校验
        if (!securityService.checkAccess(securityRequest, "ominguard.infocontroller.status")) {
            return new ResultResponse<>(ResultCode.UNAUTHORIZED, "无权访问接口", null);
        }

        ServerInfo data = securityRequest.getData();
        Example<ServerInfo> serverInfo = Example.of(data);
        List<ServerInfo> allByExample = serverStatusRepository.findAll(serverInfo);
        return new ResultResponse<>(ResultCode.SUCCESS,"成功",allByExample);
    }

    // 获取对应内容的保存数据，例如type=risk/deepfake/ai；id=dsgn500dg51。
    @PostMapping("/database/content")
    public ResultResponse<?> getData(@RequestBody SecurityRequest<DetectionResult> securityRequest){
        // 权限校验
        if (!securityService.checkAccess(securityRequest, "ominguard.infocontroller.content")) {
            return new ResultResponse<>(ResultCode.UNAUTHORIZED, "无权访问接口", null);
        }

        DetectionResult detectionResult = securityRequest.getData();
        List<DetectionResult> result = resultService.getResult(detectionResult);
        return new ResultResponse<>(ResultCode.SUCCESS,"成功",result);
    }

}

