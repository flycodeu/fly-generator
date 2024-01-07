package com.fly.maker.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 模板文件过滤配置
 */
@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> files;

    private FileGroupConfig fileGroupConfig;


    @Data
    @NoArgsConstructor
    public static class FileInfoConfig {
        /**
         * 路径
         */
        private String path;

        /**
         * 匹配规则
         */
        private List<FileFilterConfig> fileFilterConfigs;

        /**
         * 条件
         */
        private String condition;

    }


    /**
     * 文件分组配置
     */
    @Data
    @NoArgsConstructor
    public static class FileGroupConfig {
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
