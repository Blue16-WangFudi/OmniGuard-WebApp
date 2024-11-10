// AiUser.java
package com.omniguard.ai.riskdetector.model.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户账号的实体类.
 * 包含用户账号密码及访问权限等级信息.
 */
@Entity
@Table(name = "ai_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiUser {
    /**
     * 用户唯一标识符.
     */
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String ID;

    /**
     * 学生编号, 必须唯一且不可为空.
     */
    @Column(name = "stu_number", unique = true, nullable = false)
    private String stuNumber;

    /**
     * 学生身份证号.
     */
    @Column(name = "rid_number")
    private String ridNumber;

    /**
     * 用户密码, 不可为空.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 学生姓名.
     */
    @Column(name = "name")
    private String name;

    /**
     * 用户性别
     */
    @Column(name = "gender")
    private String gender;

    /**
     * 所属学院.
     */
    @Column(name = "college")
    private String college;

    /**
     * 专业名称.
     */
    @Column(name = "major")
    private String major;

    /**
     * 所在班级.
     */
    @Column(name = "class_name")
    private String className;

    /**
     * 用户的访问权限级别，默认为1.
     */
    @Column(name = "access_level", nullable = false)
    private int accessLevel = 1;
}
