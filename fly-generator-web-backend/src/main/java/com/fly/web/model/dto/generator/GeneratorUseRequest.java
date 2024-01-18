package com.fly.web.model.dto.generator;

import lombok.Data;

import java.util.Map;

/**
 * 用户使用文件传递参数
 */
@Data
public class GeneratorUseRequest {
    private Long id;
    /**
     * 数据模型参数
     */
    Map<String,Object> dataModel;
}
