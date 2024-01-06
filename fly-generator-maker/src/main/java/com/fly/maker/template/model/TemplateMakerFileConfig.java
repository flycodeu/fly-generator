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
    }
}
