package com.omniguard.ai.riskdetector.model;

import com.omniguard.ai.riskdetector.config.enumeration.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectorObject {
    // 规则，在fileKey和content二选一的时候，两者均作为一个独立识别对象识别；如果两者都提交，则content作为对fileKey所指向的文件的解释（类似于系统提示）
    private String fileKey; // 对象键，不接受URL
    private String content; // 仅纯文本
    private DataType type; // 指定数据的类型
}
