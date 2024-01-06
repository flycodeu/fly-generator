package com.fly.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.fly.maker.template.enums.FileFilterRangeEnum;
import com.fly.maker.template.enums.FileFilterRuleEnum;
import com.fly.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件过滤
 */
public class FileFilter {

    /**
     *  单个文件过滤
     * @param fileFilterConfigs 文件过滤配置
     * @param file  文件
     * @return
     */
    public static boolean doSingleFileFilter(List<FileFilterConfig> fileFilterConfigs, File file) {
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        // 校验器结果
        boolean result = true;

        if (CollUtil.isEmpty(fileFilterConfigs)) {
            return true;
        }

        for (FileFilterConfig fileFilterConfig : fileFilterConfigs) {
            String range = fileFilterConfig.getRange();
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();

            // 文件范围过滤枚举值
            FileFilterRangeEnum fileFilterRangeEnum = FileFilterRangeEnum.getByEnumValue(range);
            if (fileFilterRangeEnum == null) {
                continue;
            }
            // 先提取内容
            String content = fileName;
            switch (fileFilterRangeEnum) {
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
                default:
            }

            FileFilterRuleEnum fileFilterRuleEnum = FileFilterRuleEnum.getByEnumValue(rule);
            if (fileFilterRuleEnum == null) {
                continue;
            }

            switch (fileFilterRuleEnum) {
                case CONTAINS:
                    result = content.contains(value);
                    break;
                case STARTS_WITH:
                    result = content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                    break;
                case EQUALS:
                    result = content.equals(value);
                    break;
                default:
            }

            // 有一个不满足，就返回
            if (!result) {
                return false;
            }
        }
        return true;
    }

    /**
     * 支持单个文件或者文件夹的过滤，返回文件列表
     * @param filePath
     * @param fileFilterConfigs
     * @return
     */
    public static List<File> doFilter(String filePath,List<FileFilterConfig> fileFilterConfigs){
        List<File> fileList = FileUtil.loopFiles(filePath);
        return fileList.stream().filter(file ->doSingleFileFilter(fileFilterConfigs,file)).collect(Collectors.toList());
    }
}
