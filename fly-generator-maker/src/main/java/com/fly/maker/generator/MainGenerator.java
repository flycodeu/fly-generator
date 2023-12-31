package com.fly.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.fly.maker.generator.file.DynamicFileGenerator;
import com.fly.maker.meta.Meta;
import com.fly.maker.meta.MetaManager;
import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;

import java.io.File;
import java.io.IOException;

/**
 * 生成meta实体类
 */
public class MainGenerator {

    public static void main(String[] args) throws TemplateException, IOException {
        Meta meta = MetaManager.getMeta();
        System.out.println(meta);

        // 输出路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated";
        System.out.println(outputPath);
        // 不存在路径
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }
        // 读取resource
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // 生成java包的基础路径
        // com.fly
        String outputBasePackage = meta.getBasePackage();

        // 生成java包的路径
        // com/fly
        String outputBasePackagePath = StrUtil.join(File.separator, StrUtil.split(outputBasePackage, "."));

        // 拼接generated
        // generated/src/main/java/com/fly
        String outputBaseJavaPackagePath = outputPath + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + outputBasePackagePath;

        String inputFilePath;

        String outputFilePath;

        // model.DataModel
        inputFilePath = inputResourcePath + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "model" + File.separator + "DataModel.java";

        System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
    }
}
