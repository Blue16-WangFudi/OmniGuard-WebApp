package com.omniguard.ai.riskdetector.dto.request;


import com.omniguard.ai.riskdetector.model.DetectorObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetectorRequest {
    private String province;
    private String city;
    private String phoneNum;
    private List<DetectorObject> objects;
}
