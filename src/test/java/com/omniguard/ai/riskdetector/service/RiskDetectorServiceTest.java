package com.omniguard.ai.riskdetector.service;


import com.omniguard.ai.riskdetector.service.detector.RiskDetectorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
public class RiskDetectorServiceTest {
    @Autowired
    private RiskDetectorService riskDetectorService;

    @Test
    public void getPicDescriptionTest() throws IOException, InterruptedException {
        Map<String, Object> picDescription = riskDetectorService.getPicDescription("exampledir/image.png");

    }
}
