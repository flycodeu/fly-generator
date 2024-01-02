package com.fly.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

/**
 * 元信息校验
 */
public class MetaValidator {

    public static void doValidateAndFill(Meta meta) {
        validAndFillMetaRoot(meta);

        validAndFillFileConfig(meta);

        validAndFillModelConfig(meta);
    }

    private static void validAndFillModelConfig(Meta meta) {
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig == null) {
            return;
        }
        //modelConfig

        List<Meta.ModelConfig.ModelInfo> models = modelConfig.getModels();
        if (!CollUtil.isNotEmpty(models)) {
            return;
        }
        for (Meta.ModelConfig.ModelInfo modelInfo : models) {
            // 必填
            String fieldName = modelInfo.getFieldName();
            String modelInfoType = StrUtil.blankToDefault(modelInfo.getType(), "String");
            modelInfo.setType(modelInfoType);

            if (StrUtil.isBlank(fieldName)) {
                throw new MetaException("未填写filedName");
            }
        }
    }

    private static void validAndFillFileConfig(Meta meta) {
        Meta.FileConfig fileConfig = meta.getFileConfig();
        //fileConfig
        if (fileConfig == null) {
            return;
        }

        String inputRootPath = fileConfig.getInputRootPath();
        String outputRootPath = fileConfig.getOutputRootPath();
        String sourceRootPath = fileConfig.getSourceRootPath();

        // .source+sourceRootPath最后一个层级的路径
        String defaultInputRootPath = ".source/" + FileUtil.getLastPathEle(Paths.get(sourceRootPath).toAbsolutePath()).getFileName().toString();
        String defaultOutputRootPath = "generated";
        String defaultType = "dir";


        String type = fileConfig.getType();
        List<Meta.FileConfig.FileInfo> fileInfos = fileConfig.getFiles();

        if (StrUtil.isBlank(sourceRootPath)) {
            throw new MetaException("未填写源码根路径");
        }
        if (StrUtil.isEmpty(inputRootPath)) {
            fileConfig.setInputRootPath(defaultInputRootPath);
        }

        if (StrUtil.isEmpty(outputRootPath)) {
            fileConfig.setOutputRootPath(defaultOutputRootPath);
        }
        if (StrUtil.isEmpty(type)) {
            fileConfig.setType(defaultType);
        }

        if (!CollUtil.isEmpty(fileInfos)) {
            return;
        }

        for (Meta.FileConfig.FileInfo fileInfo : fileInfos) {
            String inputPath = fileInfo.getInputPath();
            String outputPath = fileInfo.getOutputPath();
            String fileInfoType = fileInfo.getType();
            String generateType = fileInfo.getGenerateType();
            // 必填
            if (StrUtil.isBlank(inputPath)) {
                throw new MetaException("文件配置中文件信息未填写输入路径");
            }
            // 不写就是和输入的一样
            if (StrUtil.isEmpty(outputPath)) {
                fileInfo.setOutputPath(inputPath);
            }
            // 判断输入路径后面的最后一级的结尾是不是有小数点，有就是文件
            if (StrUtil.isBlank(fileInfoType)) {
                // 无后缀是目录
                if (StrUtil.isBlank(FileUtil.getSuffix(inputPath))) {
                    fileInfo.setType("dir");
                } else {
                    fileInfo.setType("file");
                }
            }
            //generateType文件结尾不为ftl就是static否则dynamic
            if (StrUtil.isBlank(generateType)) {
                if (inputPath.endsWith(".ftl")) {
                    fileInfo.setGenerateType("dynamic");
                }
                fileInfo.setGenerateType("dynamic");
            } else {
                fileInfo.setGenerateType("static");
            }
        }
    }

    /**
     * 抽象根路径校验
     *
     * @param meta
     */
    private static void validAndFillMetaRoot(Meta meta) {
        String name = StrUtil.blankToDefault(meta.getName(), "fly-maker");
        meta.setName(name);

        String description = StrUtil.blankToDefault(meta.getDescription(), "我的代码生成器");
        meta.setDescription(description);

        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(), "com.fly");
        meta.setBasePackage(basePackage);

        String version = StrUtil.blankToDefault(meta.getVersion(), "1.0");
        meta.setVersion(version);

        String author = StrUtil.blankToDefault(meta.getAuthor(), "fly");
        meta.setAuthor(author);

        String createTime = StrUtil.blankToDefault(meta.getCreateTime(), DateUtil.now());
        meta.setCreateTime(createTime);

        Boolean useGit = meta.getUseGit();
        if (useGit == null) {
            meta.setUseGit(false);
        }
    }
}
