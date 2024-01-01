package ${basePackage}.generator;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void doGenerate(Object object) throws IOException, TemplateException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
<#list fileConfig.files as fileInfo>
        inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
        outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
        // 动态文件生成
    <#if fileInfo.generateType == "dynamic">
        DynamicGenerator.doGenerate(inputPath, outputPath, object);
        // 静态文件生成
    <#else>
        StaticGenerator.copyFilesByHutool(inputPath, outputPath);
    </#if>
</#list>
    }
}
