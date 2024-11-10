package com.omniguard.ai.riskdetector.repository.auth;

import com.omniguard.ai.riskdetector.model.auth.AiUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


/**
 * 用于在AiUser实体上执行数据库操作的接口.
 * 使用Spring Data JPA的JpaRepository提供CRUD操作和自定义查询.
 */
public interface AiUserRepository extends JpaRepository<AiUser, String> {
    AiUser findByStuNumber(String stuNumber);
    void deleteByStuNumber(String stuNumber);
    Optional<AiUser> findById(String id);
}