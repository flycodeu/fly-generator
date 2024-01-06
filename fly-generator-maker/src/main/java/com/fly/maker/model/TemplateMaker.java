package com.fly.maker.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
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

    /**
     * 制作模板
     *
     * @param newMeta           新的元信息
     * @param originProjectPath 项目原始路径
     * @param inputFileListPath 输入文件列表的路径
     * @param modelInfo         数据模型
     * @param searchStr         替换参数
     * @param id                旧项目id
     * @return id
     */
    private static long makeTemplate(Meta newMeta, String originProjectPath, List<String> inputFileListPath, Meta.ModelConfig.ModelInfo modelInfo, String searchStr, Long id) {
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

        // 使用文件的绝对路径来处理对应的文件读取
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();

        // 读取多个文件
        for (String inputFilePath : inputFileListPath) {
            String inputFileAbsolutePath = sourceRootPath + File.separator + inputFilePath;
            //如果是目录， 读取目录下面所有文件
            if (FileUtil.isDirectory(inputFileAbsolutePath)) {
                List<File> fileList = FileUtil.loopFiles(inputFileAbsolutePath);
                for (File file : fileList) {
                    Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(file, modelInfo, searchStr, sourceRootPath);
                    newFileInfoList.add(fileInfo);
                }
            } else {
                // 是文件直接处理
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(new File(inputFileAbsolutePath), modelInfo, searchStr, sourceRootPath);
                newFileInfoList.add(fileInfo);
            }
        }


        // meta生成路径
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

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
            modelInfoList.add(modelInfo);
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
            modelInfoList.add(modelInfo);

        }
        // 4. 生成meta文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        return id;
    }

    /**
     * 生成文件模板
     *
     * @param inputFile      输入的文件
     * @param modelInfo      模型信息
     * @param searchStr      替换的参数
     * @param sourceRootPath 根路径
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(File inputFile, Meta.ModelConfig.ModelInfo modelInfo, String searchStr, String sourceRootPath) {
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
        if (!FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        } else {
            // 存在对应的模板文件，就使用旧的模板文件的输出路径来进行读取
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }

        // 4.2 替换文件信息
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);

        // 二、生成Meta元信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());

        // 判断新的文件内容和之前的文件内容是否相同
        if (fileContent.equals(newFileContent)) {
            // 静态文件，不需要生成,路径和原来输入路径一样
            fileInfo.setOutputPath(fileInputPath);
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        } else {
            fileInfo.setOutputPath(fileOutputPath);
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            // 4.3 生成ftl文件
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
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(
                fileInfoList.stream()
                        .collect(
                                Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)
                        ).values()
        );
        return newFileInfoList;
    }

    /**
     * 模型去重
     *
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(
                modelInfoList.stream()
                        .collect(
                                Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                        ).values()
        );
        return newModelInfoList;
    }


    public static void main(String[] args) {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "fly-generator-demo-projects/springboot-init";

        List<String> inputFilePathList = new ArrayList<>();

        String inputFilePath1 = "/src/main/java/com/yupi/springbootinit/common/";

        String inputFilePath2 = "/src/main/java/com/yupi/springbootinit/model/";
        inputFilePathList.add(inputFilePath1);
        inputFilePathList.add(inputFilePath2);
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum=");

        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("BaseResponse");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("Test");


        //String searchStr = "Sum:";
        String searchStr = "BaseResponse";
        long l = makeTemplate(meta, originProjectPath, inputFilePathList, modelInfo, searchStr, null);
        System.out.println(l);
    }
}
