package com.omniguard.ai.riskdetector.model.prompt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document(collection = "function_call")
@NoArgsConstructor
@AllArgsConstructor
public class FunctionCall {
    @Id
    private String id;
    private String name;
    private String category; // 工具集合，暂时未实现不需要使用
    private Map<String,Object> data;

}

