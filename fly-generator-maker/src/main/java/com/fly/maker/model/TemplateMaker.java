package com.fly.maker.model;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fly.maker.meta.Meta;
import com.fly.maker.meta.enums.FileGenerateTypeEnum;
import com.fly.maker.meta.enums.FileTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateMaker {

    private static long makeTemplate(Long id) {
        // 没有id就生成
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }
        // 有id，写业务
        // 工作区间
        // 原始路径
        String projectPath = System.getProperty("user.dir");
        // 2.2 文件父级路径
        String originProjectPath = new File(projectPath).getParent() + File.separator + "fly-generator-demo-projects/acm-template";
        // 复制目录
        // 雪花算法id
        String tempDirPath = projectPath + File.separator + ".temp";
        String tempFilePath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(tempFilePath)) {
            FileUtil.mkdir(tempFilePath);
            FileUtil.copy(originProjectPath, tempFilePath, true);
        }

        // 一、项目模板基本信息
        // 1. 基础配置信息
        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";
        // 2. 文件信息
        // 2.1 文件路径
        //String projectPath = System.getProperty("user.dir");
        // 2.2 文件父级路径
        String sourceRootPath = tempFilePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
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
        String fileOutputAbsolutePath = sourceRootPath + File.separator + fileOutputPath;
        String fileContent = null;
        // 如果之前不存在对应的模板文件，就使用输入路径创建新的文件
        if (!FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        } else {
            // 存在对应的模板文件，就使用旧的模板文件的输出路径来进行读取
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }

        // 4.2 替换文件信息
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, "Sum:", replacement);
        // 4.3 生成ftl文件
        FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);


        // 二、生成Meta元信息

        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        // 因为只有一个文件，所以提出来共用
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // meta文件存在，使用旧的
        if (FileUtil.exist(metaOutputPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class, true);
            // 追加配置
            List<Meta.FileConfig.FileInfo> oldFileInfoList = oldMeta.getFileConfig().getFiles();
            oldFileInfoList.add(fileInfo);

            List<Meta.ModelConfig.ModelInfo> oldModelInfoList = oldMeta.getModelConfig().getModels();
            oldModelInfoList.add(modelInfo);
            // 配置去重
            oldMeta.getFileConfig().setFiles(distinctFiles(oldFileInfoList));
            oldMeta.getModelConfig().setModels(distinctModels(oldModelInfoList));

            // 更新meta信息
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(oldMeta), metaOutputPath);
        } else {
            // meta文件不存在，创建
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

            fileInfoList.add(fileInfo);

            // 3. 模型配置信息
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            meta.setModelConfig(modelConfig);
            modelInfoList.add(modelInfo);
            modelConfig.setModels(modelInfoList);

            // 4. 生成meta文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutputPath);
        }
        return id;
    }

    /**
     * 文件去重
     *
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        ArrayList<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(fileInfoList.stream()
                .collect(Collectors
                        .toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, ((exist, replace) -> replace))).values());

        return newFileInfoList;
    }

    /**
     * 数据模型去重
     *
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        ArrayList<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(modelInfoList.stream()
                .collect(Collectors
                        .toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, ((exist, replace) -> replace))).values());

        return newModelInfoList;
    }

    public static void main(String[] args) {
        makeTemplate(1743108401603608576L);
    }
}
