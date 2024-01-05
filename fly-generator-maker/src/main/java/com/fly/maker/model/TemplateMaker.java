package com.fly.maker.model;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fly.maker.meta.Meta;
import com.fly.maker.meta.enums.FileGenerateTypeEnum;
import com.fly.maker.meta.enums.FileTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TemplateMaker {

    public static void main(String[] args) {
        // 一、项目模板基本信息
        // 1. 基础配置信息
        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";
        // 2. 文件信息
        // 2.1 文件路径
        String projectPath = System.getProperty("user.dir");
        // 2.2 文件父级路径
        String sourceRootPath = new File(projectPath).getParent() + File.separator + "fly-generator-demo-projects/acm-template";
        sourceRootPath=sourceRootPath.replaceAll("\\\\","/");
        // 2.3 找到对应文件位置路径
        String fileInputPath = "/src/com/yupi/acm/MainTemplate.java";
        // 2.4 输出.ftl文件路径
        String fileOutputPath = fileInputPath + ".ftl";

        // 3. 数据模型信息
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("sum=");

        // 4. 生成ftl文件
        // 4.1 获取文件内容信息
        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
        String fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        // 4.2 替换文件信息
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, "Sum:", replacement);
        // 4.3 生成ftl文件
        String fileOutputAbsolutePath = sourceRootPath + File.separator + fileOutputPath;
        FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);


        // 二、生成Meta元信息
        Meta meta = new Meta();
        // 1. 基础配置信息
        meta.setName(name);
        meta.setDescription(description);

        // 2. 文件配置信息
        Meta.FileConfig fileConfig = new Meta.FileConfig();
        meta.setFileConfig(fileConfig);
        fileConfig.setSourceRootPath(sourceRootPath);
        List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
        fileConfig.setFiles(fileInfoList);

        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
        fileInfoList.add(fileInfo);

        // 3. 模型配置信息
        List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
        Meta.ModelConfig modelConfig = new Meta.ModelConfig();
        meta.setModelConfig(modelConfig);
        modelInfoList.add(modelInfo);
        modelConfig.setModels(modelInfoList);

        // 4. 生成meta文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutputPath);
    }
}
