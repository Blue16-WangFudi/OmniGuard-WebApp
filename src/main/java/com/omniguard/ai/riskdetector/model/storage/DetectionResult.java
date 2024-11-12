package com.omniguard.ai.riskdetector.model.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document(collection = "detection_result")
@NoArgsConstructor
@AllArgsConstructor
public class DetectionResult {

    @Id
    private String id;
    private String type;//识别类型 ai risk deepfake
    private String city;
    private String phoneNum;
    private Map<String, Object> result;

}
