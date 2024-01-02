package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void doGenerate(DataModel object) throws IOException, TemplateException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;

<#list modelConfig.models as modelInfo>
        ${modelInfo.type} ${modelInfo.fieldName} = object.${modelInfo.fieldName};
</#list>

<#list fileConfig.files as fileInfo>
    <#if fileInfo.condition??>
        if(${fileInfo.condition}){
            inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
        <#if fileInfo.generateType == "dynamic">
            // 动态文件生成
            DynamicGenerator.doGenerate(inputPath, outputPath, object);
        <#else>
            // 静态文件生成
            StaticGenerator.copyFilesByHutool(inputPath, outputPath);
        </#if>
        }
    <#else>
        inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
        outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
        <#if fileInfo.generateType == "dynamic">
        // 动态文件生成
        DynamicGenerator.doGenerate(inputPath, outputPath, object);
        <#else>
        // 静态文件生成
        StaticGenerator.copyFilesByHutool(inputPath, outputPath);
        </#if>
    </#if>
</#list>
    }
}
