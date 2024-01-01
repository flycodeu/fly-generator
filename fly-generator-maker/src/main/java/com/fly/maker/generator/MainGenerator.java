package com.fly.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.fly.maker.generator.file.DynamicFileGenerator;
import com.fly.maker.meta.Meta;
import com.fly.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 生成meta实体类
 */
public class MainGenerator {

    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMeta();
        System.out.println(meta);

        // 输出路径
        //String projectPath = System.getProperty("user.dir");
        // String outputPath = projectPath + File.separator + "generated";
        String outputPath = meta.getFileConfig().getOutputRootPath() + File.separator + "generated";
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

        // 生成model.DataModel
        inputFilePath = inputResourcePath + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "model" + File.separator + "DataModel.java";
        //System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成cli包下面对应的文件
        // 生成cli.command.ConfigCommand
        inputFilePath = inputResourcePath + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "command" + File.separator + "ConfigCommand.java";
        //System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成cli.command.GenerateCommand
        inputFilePath = inputResourcePath + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "command" + File.separator + "GenerateCommand.java";
        //System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成cli.command.ListGenerateCommand
        inputFilePath = inputResourcePath + "templates/java/cli/command/ListGenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "command" + File.separator + "ListGenerateCommand.java";
        //System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成cli.command.CommandExecutor.java.ftl
        inputFilePath = inputResourcePath + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "CommandExecutor.java";
        //System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成Main
        inputFilePath = inputResourcePath + "templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "Main.java";
        //System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成MainGenerator
        inputFilePath = inputResourcePath + "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "generator" + File.separator + "MainGenerator.java";
        //System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成StaticFileGenerator
        inputFilePath = inputResourcePath + "templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "generator" + File.separator + "StaticGenerator.java";
        //System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // DynamicFileGenerator
        inputFilePath = inputResourcePath + "templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "generator" + File.separator + "DynamicGenerator.java";
        //System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 生成pom
        inputFilePath = inputResourcePath + "templates/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        //System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 构建jar
        JarGenerator.doGenerator(outputPath);

        // 封装脚本
        String shellOutPutFilePath = outputPath + File.separator + "generator";
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "target/" + jarName;
        ScriptGenerator.doGenerate(shellOutPutFilePath, jarPath);
    }
}
