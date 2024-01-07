package com.fly.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fly.maker.meta.Meta;
import com.fly.maker.meta.enums.FileGenerateTypeEnum;
import com.fly.maker.meta.enums.FileTypeEnum;
import com.fly.maker.template.FileFilter;
import com.fly.maker.template.enums.FileFilterRangeEnum;
import com.fly.maker.template.enums.FileFilterRuleEnum;
import com.fly.maker.template.model.FileFilterConfig;
import com.fly.maker.template.model.TemplateMakerFileConfig;
import com.fly.maker.template.model.TemplateMakerModelConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TemplateMaker {

    /**
     * 制作模板
     *
     * @param newMeta                  新的元信息
     * @param originProjectPath        项目原始路径
     * @param templateMakerFileConfig  文件过滤
     * @param templateMakerModelConfig 数据模型
     * @param id                       旧项目id
     * @return id
     */
    public static long makeTemplate(Meta newMeta, String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, Long id) {
        // 没有id就生成
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }
        // 有id，写业务
        // 工作区间
        // 原始路径
        // 2.2 文件父级路径
        // 复制目录
        // 雪花算法id
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String tempFilePath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(tempFilePath)) {
            FileUtil.mkdir(tempFilePath);
            FileUtil.copy(originProjectPath, tempFilePath, true);
        }

        // 一、项目模板基本信息
        // 1. 基础配置信息
        // 2. 文件信息
        String sourceRootPath = tempFilePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        // 数据模型分组
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        // 处理模型信息
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream().map(modelInfoConfig -> {
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelInfoConfig, modelInfo);
            return modelInfo;
        }).collect(Collectors.toList());

        // - 本次新增的模型配置列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();
        // 有分组
        if (modelGroupConfig != null) {
            String condition = modelGroupConfig.getCondition();
            String groupKey = modelGroupConfig.getGroupKey();
            String groupName = modelGroupConfig.getGroupName();

            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            modelInfo.setCondition(condition);
            modelInfo.setGroupKey(groupKey);
            modelInfo.setGroupName(groupName);

            // 全部模型放到一个分组
            modelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(modelInfo);
        } else {
            // 没有分组，添加所有的模型信息到列表
            newModelInfoList.addAll(inputModelInfoList);
        }


        // 使用文件的绝对路径来处理对应的文件读取
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFiles();
        // 过滤文件
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigList) {
            String inputFilePath = fileInfoConfig.getPath();
            // 输入文件绝对路径
            String inputFileAbsolutePath = sourceRootPath + File.separator + inputFilePath;
            // 得到过滤后的文件列表
            List<File> fileList = FileFilter.doFilter(inputFileAbsolutePath, fileInfoConfig.getFileFilterConfigs());
            // 过滤掉ftl文件
            fileList = fileList.stream().filter(file -> !file.getAbsolutePath().endsWith(".ftl")).collect(Collectors.toList());
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(file, templateMakerModelConfig, sourceRootPath);
                newFileInfoList.add(fileInfo);
            }
        }

        // 文件分组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (fileGroupConfig != null) {
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();

            // 设置文件分组信息
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            // 文件全部放到分组里面
            groupFileInfo.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }


        // meta生成路径
        String metaOutputPath = tempFilePath + File.separator + "meta.json";

        // meta文件存在，使用旧的
        if (FileUtil.exist(metaOutputPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class, false);
            // 将旧的meta的值写入到新的meta里面
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;
            // 追加配置
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);

            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);
            // 配置去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        } else {
            // meta文件不存在，创建
            // 1. 基础配置信息提取出去
            // 2. 文件配置信息
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);

            // 3. 模型配置信息
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            modelConfig.setModels(modelInfoList);
            modelInfoList.addAll(newModelInfoList);
        }
        // 4. 生成meta文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        return id;
    }

    /**
     * 生成文件模板
     *
     * @param inputFile                输入的文件
     * @param templateMakerModelConfig 模型信息
     * @param sourceRootPath           根路径
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(File inputFile, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        // 获取绝对路径
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 生成相对路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";
        // 3. 数据模型信息
        // 4. 生成ftl文件
        // 4.1 获取文件内容信息

        String fileContent = null;
        // 如果之前不存在对应的模板文件，就使用输入路径创建新的文件
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        if (!hasTemplateFile) {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        } else {
            // 存在对应的模板文件，就使用旧的模板文件的输出路径来进行读取
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }

        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        // 支持多个替换参数
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            // 没有分组
            if (modelGroupConfig == null) {
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
            } else {
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}", groupKey, modelInfoConfig.getFieldName());
            }
            // 多次替换信息
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }

        // 二、生成Meta元信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 判断新的文件内容和之前的文件内容是否相同
        // 之前不存在模板，没有更改文件内容，就是静态的
        boolean contentEquals = fileContent.equals(newFileContent);
        if (!hasTemplateFile) {
            if (contentEquals) {
                // 静态文件，不需要生成,路径和原来输入路径一样
                fileInfo.setInputPath(fileInputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            } else {
                // 4.3 生成ftl文件
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
            }
        } else if (contentEquals) {
            // 之前存在模板并且内容不同
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }

        return fileInfo;
    }

    /**
     * 文件去重
     *
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        //1. 将所有文件配置（fileInfo）分为有分组的和无分组的
        // 有分组的以组来划分,key是groupKey,value是FileInfo列表
        // 示例 {"groupKey":"a","files":[1,2]}  {"groupKey":"a","files":[2,3]}   {"groupKey":"b","files":[4,5]}
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));

        //2. 对于有分组的文件配置，如果有相同的分组，同分组内的文件进行合并（merge），不同分组可同时保留
        //  {"groupKey":"a","files":[1,2]}  {"groupKey":"a","files":[2,3]}
        //  先变成 {"groupKey":"a","files":[[1,2],[2,3]}
        //  flatMap展开 {"groupKey":"a","files":[1,2,2,3}
        //  合并后 {"groupKey":"a","files":[1,2,3]}
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileList = entry.getValue();
            String groupKey = entry.getKey();
            ArrayList<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(Collectors
                            .toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)).values());

            // 因为之前是将新的代码添加到旧的代码的下面，所以覆盖的时候只需要获取最新的就可以
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileList);
            newFileInfo.setFiles(newFileInfoList);
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }

        //3. 创建新的文件配置列表（结果列表），先将合并后的分组添加到结果列表,现在是单个文件
        ArrayList<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        //4. 再将无分组的文件配置列表添加到结果列表
        List<Meta.FileConfig.FileInfo> noGroupKeyFileList = fileInfoList.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());

        resultList.addAll(new ArrayList<>(
                noGroupKeyFileList.stream()
                        .collect(
                                Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)
                        ).values()));
        return resultList;
    }

    /**
     * 模型去重
     *
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        //1. 将所有文件配置（modelInfo）分为有分组的和无分组的
        // 有分组的以组来划分,key是groupKey,value是ModelInfo列表
        // 示例 {"groupKey":"a","models":[1,2]}  {"groupKey":"a","models":[2,3]}   {"groupKey":"b","models":[4,5]}
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));

        //2. 对于有分组的文件配置，如果有相同的分组，同分组内的文件进行合并（merge），不同分组可同时保留
        //  {"groupKey":"a","models":[1,2]}  {"groupKey":"a","models":[2,3]}
        //  先变成 {"groupKey":"a","models":[[1,2],[2,3]}
        //  flatMap展开 {"groupKey":"a","models":[1,2,2,3}
        //  合并后 {"groupKey":"a","models":[1,2,3]}
        // 保存每个组对应的合并后的对象 map
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempModelList = entry.getValue();
            String groupKey = entry.getKey();
            ArrayList<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(tempModelList.stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(Collectors
                            .toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values());

            // 因为之前是将新的代码添加到旧的代码的下面，所以覆盖的时候只需要获取最新的就可以
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelList);
            newModelInfo.setModels(newModelInfoList);
            groupKeyMergedModelInfoMap.put(groupKey, newModelInfo);
        }

        //3. 创建新的文件配置列表（结果列表），先将合并后的分组添加到结果列表,现在是单个文件
        ArrayList<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());

        //4. 再将无分组的文件配置列表添加到结果列表
        List<Meta.ModelConfig.ModelInfo> noGroupKeyModelList = modelInfoList.stream().filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());

        resultList.addAll(new ArrayList<>(
                noGroupKeyModelList.stream()
                        .collect(
                                Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                        ).values()));
        return resultList;
    }


    public static void main(String[] args) {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "fly-generator-demo-projects/springboot-init";

        List<String> inputFilePathList = new ArrayList<>();

        String inputFilePath1 = "/src/main/java/com/yupi/springbootinit/constant/";

        String inputFilePath2 = "/src/main/resources/application.yml";
        inputFilePathList.add(inputFilePath1);
        inputFilePathList.add(inputFilePath2);
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum=");

        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("Test");


        //String searchStr = "Sum:";
        String searchStr = "BaseResponse";

        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();

        // 文件过滤配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        // 文件名包含 Base 的文件列表
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        fileFilterConfigList.add(fileFilterConfig);
        fileInfoConfig1.setFileFilterConfigs(fileFilterConfigList);
        fileInfoConfig1.setPath(inputFilePath1);


        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputFilePath2);

        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1, fileInfoConfig2));


        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setCondition("test");
        fileGroupConfig.setGroupKey("test2");
        fileGroupConfig.setGroupName("test");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);


        // 模型配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        // 分组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupName("mysql配置");
        modelGroupConfig.setGroupKey("mysql");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);


        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");


        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);


        long l = makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, 1743817327068332032L);
        System.out.println(l);
    }
}
