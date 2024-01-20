package com.fly.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.fly.maker.generator.GitGenerator;
import com.fly.maker.generator.JarGenerator;
import com.fly.maker.generator.ScriptGenerator;
import com.fly.maker.generator.file.DynamicFileGenerator;
import com.fly.maker.meta.Meta;
import com.fly.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public abstract class GenerateTemplate {

    public void doGenerate() throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMeta();
        System.out.println(meta);

        // 输出路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated" + File.separator + meta.getName();
        // 不存在路径
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }
        doGenerate(meta,outputPath);
    }


    /**
     * 支持传入参数调用maker制作工具
     * @param meta
     * @param outputPath
     * @throws TemplateException
     * @throws IOException
     * @throws InterruptedException
     */
    public void doGenerate(  Meta meta,String outputPath ) throws TemplateException, IOException, InterruptedException {
        // 不存在路径
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 1. 原始模板路径复制到生成的代码包中去
        String sourceCopyDestPath = copySource(meta, outputPath);

        // 2. 读取resource里面的地址信息，生成对应的动态静态文件
        generateCode(meta, outputPath);

        // 3. 构建jar
        String jarPath = buildJar(meta, outputPath);

        // 4. 封装脚本
        String shellOutputFilePath = getShellOutPutFilePath(outputPath, jarPath);

        //5.精简版程序包，只有源模板和jar，脚本
        buildDist(outputPath, shellOutputFilePath, jarPath, sourceCopyDestPath);
    }
    /**
     * 构建zip打包
     *
     * @param outputPath
     * @return
     */
    protected String buildZip(String outputPath) {
        String zipPath = outputPath + ".zip";
        ZipUtil.zip(outputPath, zipPath);
        return zipPath;
    }

    /**
     * 构建精简版程序包，只有源模板和jar，脚本
     *
     * @param outputPath          输出路径
     * @param shellOutPutFilePath 原始的shell路径
     * @param jarPath             原始的jar路径
     * @param sourceCopyDestPath  复制地址路径
     */
    protected String buildDist(String outputPath, String shellOutPutFilePath, String jarPath, String sourceCopyDestPath) {
        // 目标地址就是和之前的目录同级别加了-dist
        String destOutPutPath = outputPath + "-dest";
        // 复制jar包
        // 复制jar包的最终地址
        String targetJarAbsolutePath = destOutPutPath + File.separator + "target";
        // 创建target目录
        FileUtil.mkdir(targetJarAbsolutePath);
        // 源jar包
        String jarAbsolutePath = outputPath + File.separator + jarPath;
        FileUtil.copy(jarAbsolutePath, targetJarAbsolutePath, true);

        // 复制脚本
        FileUtil.copy(shellOutPutFilePath, destOutPutPath, true);
        FileUtil.copy(shellOutPutFilePath + ".bat", destOutPutPath, true);

        // 复制源模板
        FileUtil.copy(sourceCopyDestPath, destOutPutPath, true);
        return destOutPutPath;
    }

    /**
     * 获取shell的输出路径
     *
     * @param outputPath 输出路径
     * @param jarPath    jar所在的路径
     * @return
     */
    protected String getShellOutPutFilePath(String outputPath, String jarPath) {
        String shellOutPutFilePath = outputPath + File.separator + "generator";
        ScriptGenerator.doGenerate(shellOutPutFilePath, jarPath);
        return shellOutPutFilePath;
    }


    /**
     * 构建jar包
     *
     * @param meta       元信息
     * @param outputPath 输出路径
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    protected String buildJar(Meta meta, String outputPath) throws IOException, InterruptedException {
        JarGenerator.doGenerator(outputPath);
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "target/" + jarName;
        return jarPath;
    }

    /**
     * 生成动态，静态代码
     *
     * @param meta       元信息
     * @param outputPath 输出路径
     * @throws IOException
     * @throws TemplateException
     * @throws InterruptedException
     */
    protected void generateCode(Meta meta, String outputPath) throws IOException, TemplateException, InterruptedException {
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

        // 生成MainGenerator
        inputFilePath = inputResourcePath + "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "generator" + File.separator + "MainGenerator.java";
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

        // 生成cli.command.JsonGenerateCommand
        inputFilePath = inputResourcePath + "templates/java/cli/command/JsonGenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "command" + File.separator + "JsonGenerateCommand.java";
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

        // 生成README文件
        inputFilePath = inputResourcePath + "templates/README.md.ftl";
        outputFilePath = outputPath + File.separator + "README.md";
        //System.out.println(outputFilePath);
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // 构建git，这个需要在整个目录下
        // todo 用户可以选择
        if (meta.getUseGit()) {
            String gitOutPutFilePath = meta.getFileConfig().getOutputRootPath();
            System.out.println("gitignore--->" + gitOutPutFilePath);
            GitGenerator.doGenerator(gitOutPutFilePath);
            inputFilePath = inputResourcePath + "templates/.gitignore.ftl";
            outputFilePath = gitOutPutFilePath + File.separator + ".gitignore";
            System.out.println("gitignore--->" + outputFilePath);
            DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        }

    }

    /**
     * 复制源文件到.source目录下
     *
     * @param meta       元信息
     * @param outputPath 输出路径
     * @return
     */
    protected String copySource(Meta meta, String outputPath) {
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, false);
        return sourceCopyDestPath;
    }
}
