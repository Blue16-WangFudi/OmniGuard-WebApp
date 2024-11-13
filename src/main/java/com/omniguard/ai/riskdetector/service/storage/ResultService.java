package com.omniguard.ai.riskdetector.service.storage;


import com.omniguard.ai.riskdetector.model.DetectorObject;
import com.omniguard.ai.riskdetector.model.storage.DetectionResult;
import com.omniguard.ai.riskdetector.repository.storgae.DetectionResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ResultService {

    @Autowired
    DetectionResultRepository detectionResultRepository;

        public String saveResult(String type, String city, String phoneNum, List<DetectorObject> data, Map<String, Object> result) {
        String id = UUID.randomUUID().toString();
        detectionResultRepository.save(new DetectionResult(id,type,city,phoneNum,data,result));
        return id;
    }

    public DetectionResult getResult(String id) {
        return detectionResultRepository.findById(id).get();
    }

    public List<DetectionResult> getResult(DetectionResult detectionResult){
        return detectionResultRepository.findAllByExample(detectionResult);

    }
}
