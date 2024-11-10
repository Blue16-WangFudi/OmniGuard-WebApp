package com.omniguard.ai.riskdetector.repository.auth;

import com.omniguard.ai.riskdetector.model.auth.AccessLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用于查询各个服务访问权限等级的接口.
 * 使用Spring Data JPA的JpaRepository提供CRUD操作和自定义查询.
 */
@Repository
public interface AccessLevelRepository extends JpaRepository<AccessLevel, String> {
    Optional<AccessLevel> findAccessLevelByLocation(String location);
}
