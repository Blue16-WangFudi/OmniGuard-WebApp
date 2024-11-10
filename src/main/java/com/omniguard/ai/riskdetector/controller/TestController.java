package com.omniguard.ai.riskdetector.controller;


import com.omniguard.ai.riskdetector.service.detector.RiskDetectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/springboottest")
public class TestController {
    @Autowired
    RiskDetectorService riskDetectorService;

    @RequestMapping()
    public Map<String, Object> test() throws IOException, InterruptedException {
        return riskDetectorService.getPicDescription("/exampledir/image.png");
    }

    @RequestMapping("/audio")
    public Map<String, Object> testAudio() throws IOException, InterruptedException {
        return riskDetectorService.getAudioDescription("exampledir/test.wav");
    }
    @RequestMapping("/video")
    public Map<String, Object> testVideo() throws IOException, InterruptedException {
        return riskDetectorService.getVideoDescription("exampledir/long.mp4");
    }
    @RequestMapping("/videodiv")
    public Map<String, Object> testVideoDiv() throws IOException, InterruptedException {
        return riskDetectorService.getVideoAudio("exampledir/long.mp4");
    }

}
