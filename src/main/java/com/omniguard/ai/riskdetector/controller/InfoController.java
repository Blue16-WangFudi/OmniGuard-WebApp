package com.omniguard.ai.riskdetector.controller;


import com.omniguard.ai.riskdetector.config.enumeration.ResultCode;
import com.omniguard.ai.riskdetector.dto.ResultResponse;
import com.omniguard.ai.riskdetector.dto.SecurityRequest;
import com.omniguard.ai.riskdetector.dto.request.DetectorRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v3/info")
public class InfoController {


    // 提交服务器的标识符，返回服务器状态信息的集合。可以指定查询时间段。
    @PostMapping("/server/status")
    public ResultResponse<?> getServerInfo(@RequestBody SecurityRequest<DetectorRequest> securityRequest){
        return new ResultResponse<>(ResultCode.SUCCESS,"成功",null);
    }

    // 获取对应内容的保存数据，例如type=risk/deepfake/ai；id=dsgn500dg51。
    @PostMapping("/database/content")
    public ResultResponse<?> getData(@RequestBody SecurityRequest<DetectorRequest> securityRequest){
        return new ResultResponse<>(ResultCode.SUCCESS,"成功",null);
    }

    // 获取所有风险信息的id列表，可以指定筛选条件（province city phonenum）
    @PostMapping("/database/all")
    public ResultResponse<?> getDataList(@RequestBody SecurityRequest<DetectorRequest> securityRequest){
        return new ResultResponse<>(ResultCode.SUCCESS,"成功",null);
    }
}

