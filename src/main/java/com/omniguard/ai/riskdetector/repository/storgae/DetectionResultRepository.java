package com.omniguard.ai.riskdetector.repository.storgae;

import com.omniguard.ai.riskdetector.model.storage.DetectionResult;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

@Repository
public interface DetectionResultRepository extends MongoRepository<DetectionResult,String> {
    Optional<DetectionResult> findById(String type);

    List<DetectionResult> findByType(String type);

    List<DetectionResult> findByCity(String city);

    List<DetectionResult> findByPhoneNum(String country);

    /**
     * 根据示例的非空、非空属性查找所有DetectionResult条目
     * 对文本字段使用模糊搜索，对其他字段使用精确匹配
     *
     * @param example 一个实例，非空属性即为查找属性
     * @return 查找结果的列表，每一个元素均为一个DetectionResult的实例
     */
    default List<DetectionResult> findAllByExample(DetectionResult example) {
        ExampleMatcher matcher = ExampleMatcher.matchingAll();
        for (Field field : example.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(example);

                if (value != null) {
                    if (value instanceof String && !((String) value).isEmpty()) {
                        String fieldName = field.getName();
                        // String字段：模糊查找
                        matcher = matcher.withMatcher(fieldName, ExampleMatcher.GenericPropertyMatchers.contains());
                    } else if (!(value instanceof String)) {
                        // 非String字段，不匹配
                        // matcher = matcher.withMatcher(field.getName(), ExampleMatcher.GenericPropertyMatchers.exact());
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Example<DetectionResult> exampleQuery = Example.of(example, matcher);
        return findAll(exampleQuery);
    }

}
