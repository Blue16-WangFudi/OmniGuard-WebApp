// AccessLevel.java
package com.omniguard.ai.riskdetector.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示不同接口的访问级别的实体.
 * 用于定义特定区域的访问控制规则.
 */
@Entity
@Table(name = "ai_access_level")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessLevel {
    /**
     * 访问级别唯一标识符.
     */
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String ID;

    /**
     * 访问位置.
     */
    @Column(name = "location")
    private String location;

    /**
     * 访问权限级别.
     */
    @Column(name = "access_level")
    private int accessLevel;
}
