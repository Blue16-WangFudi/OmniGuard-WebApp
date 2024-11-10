package com.omniguard.ai.riskdetector.service.auth;

import com.omniguard.ai.riskdetector.model.auth.AiUser;
import com.omniguard.ai.riskdetector.repository.auth.AiUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 账号信息管理服务类，用于处理用户账号的增删查改操作。
 * 包括根据学号查询、更新账号信息以及删除用户等功能。
 */
@Service
@Transactional
public class AiUserService {

    @Autowired
    private AiUserRepository aiUserRepository;

    /**
     * 根据学号查询用户账号信息。
     *
     * @param stuNumber 学生学号
     * @return 对应学号的用户账号信息 {@link AiUser}
     */
    public AiUser get(String stuNumber) {
        return aiUserRepository.findByStuNumber(stuNumber);
    }

    public AiUser getById(String id) {
        return aiUserRepository.findById(id).orElse(null);
    }

    /**
     * 添加或更新用户账号信息。
     *
     * @param aiUser 用户账号信息对象
     */
    public void update(AiUser aiUser) {
        aiUserRepository.save(aiUser);
    }

    /**
     * 删除指定学号的用户账号信息。
     *
     * @param stuNumber 学生学号
     */
    public void delete(String stuNumber) {
        aiUserRepository.deleteByStuNumber(stuNumber);
    }
}
