package com.fly.generator;

import com.fly.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        // 静态生成
        //  获取项目路径
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        // 输入路径
        String inputPath = projectPath + File.separator + "fly-generator-demo-projects" + File.separator + "acm-template";
        System.out.println(inputPath);
        // 输出路径
        String targetPath = projectPath;
        //copyFilesByHutool(inputPath, targetPath);
        StaticGenerator.copyFilesByRecursive(inputPath, targetPath);

        // 动态生成
        String dynamicInputPath = projectPath + File.separator + "fly-generator-basic" + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template/src/com/yupi/acm/MainTemplate.java";
        MainTemplateConfig model = new MainTemplateConfig();
        model.setLoop(false);
        model.setOutputText("结果=");
        model.setAuthor("flycode");
        DynamicGenerator.doGenerate(dynamicInputPath, dynamicOutputPath, model);
    }
}
