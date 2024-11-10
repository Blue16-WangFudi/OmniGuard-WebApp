package com.omniguard.ai.riskdetector.repository.prompt;


import com.omniguard.ai.riskdetector.model.prompt.FunctionCall;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// TODO 注意每一个ChatSession的ID项也得指定成chatSession（检查一下不然会重复）
public interface FunctionCallRepository extends MongoRepository<FunctionCall, String> {
    FunctionCall findByName(String name);
    List<FunctionCall> findByCategory(String category);
}