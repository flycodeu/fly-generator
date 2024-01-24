package com.fly.web;

import com.fly.web.model.entity.Generator;
import com.fly.web.service.impl.GeneratorServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class InsertValues {
    @Resource
    private GeneratorServiceImpl generatorService;

    @Test
    void testInsert() {
        Generator generator = generatorService.getById(19);
        for (int i = 0; i < 100000; i++) {
            generator.setId(null);
            generatorService.save(generator);
        }
    }
}
