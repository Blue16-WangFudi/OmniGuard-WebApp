package com.omniguard.ai.riskdetector.service.prompt;


import com.omniguard.ai.riskdetector.model.prompt.SystemPrompt;
import com.omniguard.ai.riskdetector.repository.prompt.SystemPromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SystemPromptService {

    @Autowired
    SystemPromptRepository systemPromptRepository;

    public String getSystemPromptByName(String name, Map<String,String> parameters){
        SystemPrompt byName = systemPromptRepository.findByName(name);
        return replacePlaceholders(byName.getData(), parameters);
    }

    public static String replacePlaceholders(String text, Map<String, String> parameters) {
        if(parameters==null)
            parameters = new HashMap<>();
        // 定义一个模式来匹配形如 {参数名} 的占位符
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(text);

        // 使用 matcher 迭代所有的占位符，并从 parameters 中查找对应的值
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // 获取占位符的名字
            String paramName = matcher.group(1);
            // 从参数集中获取对应的值，如果没有找到，则保留原样
            String replacement = parameters.getOrDefault(paramName, matcher.group(0));
            // 替换掉占位符
            matcher.appendReplacement(sb, replacement);
        }
        // 处理完所有匹配项后，添加剩余的部分
        matcher.appendTail(sb);

        return sb.toString();
    }

}
