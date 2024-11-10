package com.omniguard.ai.riskdetector.service;

import com.omniguard.ai.riskdetector.dto.SecurityRequest;
import com.omniguard.ai.riskdetector.model.auth.AccessLevel;
import com.omniguard.ai.riskdetector.model.auth.AiUser;
import com.omniguard.ai.riskdetector.repository.auth.AccessLevelRepository;
import com.omniguard.ai.riskdetector.repository.auth.AiUserRepository;
import com.omniguard.ai.riskdetector.service.auth.AiUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * 安全服务类，主要用于处理权限判定、用户访问等级查询及Token更新等安全相关的业务逻辑。
 */
@Service
public class SecurityService {

    @Autowired
    private AiUserService aiUserService;

    @Autowired
    private AccessLevelRepository accessLevelRepository;

    @Autowired
    private AiUserRepository aiUserRepository;

    /**
     * 更新用户的Token，采用删除原来的再新建一个新的Token的方式。
     *
     * @param aiUser 要更新Token的用户信息对象
     * @return 更新后的用户信息 {@link AiUser}
     */
    public AiUser changeUserToken(AiUser aiUser) {
        AiUser newAiUser = new AiUser(UUID.randomUUID().toString(),
                aiUser.getStuNumber(),aiUser.getRidNumber(),
                aiUser.getPassword(),aiUser.getName(),
                aiUser.getGender(), aiUser.getCollege(),
                aiUser.getMajor(),aiUser.getClassName(),aiUser.getAccessLevel());
        // 删除原有用户记录
        aiUserService.delete(aiUser.getStuNumber());
        // 保存更新后的用户信息
        aiUserService.update(newAiUser);
        return newAiUser;
    }

    /**
     * 检查指定的请求是否有权限访问某个接口。
     *
     * @param securityRequest 请求的安全信息
     * @param endpoint 接口的路径或位置
     * @return 如果请求的用户权限等级大于等于接口访问等级，返回 true，否则返回 false。
     */
    public boolean checkAccess(SecurityRequest<?> securityRequest, String endpoint) {
        AccessLevel defaultAccessLevel = new AccessLevel();
        defaultAccessLevel.setAccessLevel(100); // 默认接口访问权限

        // 获取指定接口的访问等级，如果不存在则使用默认值
        Optional<AccessLevel> accessLevelByLocation = accessLevelRepository.findAccessLevelByLocation(endpoint);
        int requiredAccessLevel = accessLevelByLocation.orElse(defaultAccessLevel).getAccessLevel();

        return getAccessLevel(securityRequest.getToken()) >= requiredAccessLevel;
    }

    /**
     * 根据Token获取用户的访问权限等级。
     *
     * @param token 用户的身份标识Token
     * @return 用户的访问权限等级
     */
    public int getAccessLevel(String token) {
        AiUser defaultAiUser = new AiUser();
        defaultAiUser.setAccessLevel(0); // 默认用户访问权限等级

        // 根据Token查询用户访问权限，如果查询不到则返回默认等级
        Optional<AiUser> userOptional = aiUserRepository.findById(token);
        return userOptional.orElse(defaultAiUser).getAccessLevel();
    }

}
