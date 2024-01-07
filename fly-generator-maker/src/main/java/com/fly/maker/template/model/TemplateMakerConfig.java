package com.fly.maker.template.model;

import com.fly.maker.meta.Meta;
import lombok.Data;

/**
 * 封装配置参数
 */
@Data
public class TemplateMakerConfig {

    /**
     * 元信息配置
     */
    private Meta meta = new Meta();

    /**
     * 原始路径
     */
    private String originProjectPath;

    /**
     * 工作区间id
     */
    private Long id;

    /**
     * 文件配置
     */
    private TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    /**
     * 模型配置
     */
    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();
}
