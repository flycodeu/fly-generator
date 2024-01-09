package com.fly.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

/**
 * 主类测试
 *
 */
@SpringBootTest
class MainApplicationTests {

    @Test
    void addPwd() {
        String SALT = "fly";
        String password = "123456";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        System.out.println(encryptPassword);
    }
}
