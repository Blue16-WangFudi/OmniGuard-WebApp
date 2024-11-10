package com.omniguard.ai.riskdetector.model.prompt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "system_prompt")
@NoArgsConstructor
@AllArgsConstructor
public class SystemPrompt {
    @Id
    private String id;
    private String name;
    private String type;//什么提示（system 、user）
    private String category; // 工具集合，暂时未实现不需要使用
    private String data;

}

