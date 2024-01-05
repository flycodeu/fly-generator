package com.fly.maker.generator.file;


import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 文件生成器
 * 结合动态文件生成和静态文件生成
 */
public class MainFileGenerator {

    public static void doGenerate(Object dataModel) throws IOException, TemplateException {
        // 静态生成
        //  获取项目路径
        String projectPath = System.getProperty("user.dir");
        //System.out.println(projectPath);
        // 整个项目的根路径
        File parentFile = new File(projectPath).getParentFile();
        // 输入路径
        String inputPath = new File(parentFile, "fly-generator-demo-projects/acm-template").getAbsolutePath();
        //System.out.println(inputPath);
        // 输出路径
        String targetPath = projectPath;
        //copyFilesByHutool(inputPath, targetPath);
        StaticFileGenerator.copyFilesByHutool(inputPath, targetPath);

        // 动态生成
        String dynamicInputPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = targetPath + File.separator + "acm-template/src/com/yupi/acm/MainTemplate.java";

        DynamicFileGenerator.doGenerate(dynamicInputPath, dynamicOutputPath, dataModel);
    }
}
