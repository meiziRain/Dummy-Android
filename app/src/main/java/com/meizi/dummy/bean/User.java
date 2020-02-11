package com.meizi.dummy.bean;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname User
 * @Description TODO
 * @Date 2020/2/6 12:27
 * @Created by jion
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String userEmail;
    private String password;
    private String nickName;
    private String userPhone;
    private String userSex;

    /**
     * 最后一次登陆时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 登录次数
     */
    private Long loginCount;
}
