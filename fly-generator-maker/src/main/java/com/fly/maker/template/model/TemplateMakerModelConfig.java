package com.fly.maker.template.model;

import com.fly.maker.meta.Meta;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模板数据模型过滤配置
 */
@Data
public class TemplateMakerModelConfig {

    private List<ModelInfoConfig> models;

    private ModelGroupConfig modelGroupConfig;


    @Data
    @NoArgsConstructor
    public static class ModelInfoConfig {
        private String fieldName;
        private String type;
        private String description;
        private Object defaultValue;
        private String abbr;
        // 替换的参数
        private String replaceText;
    }


    /**
     * 模型分组配置
     */
    @Data
    @NoArgsConstructor
    public static class ModelGroupConfig {
        /**
         * 分组条件
         */
        private String condition;
        /**
         * 分组的key
         */
        private String groupKey;
        /**
         * 分组的名称
         */
        private String groupName;
    }
}
