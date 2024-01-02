package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;


/**
* 核心代码生成器
*/
public class MainGenerator {

    /**
    *  执行生成
    * @param object 数据模型
    * @throws IOException
    * @throws TemplateException
    */
    public static void doGenerate(DataModel object) throws IOException, TemplateException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;

<#list modelConfig.models as modelInfo>
        ${modelInfo.type} ${modelInfo.fieldName} = object.${modelInfo.fieldName};
</#list>

<#list fileConfig.files as fileInfo>
    <#if fileInfo.groupKey??>
         // groupKey =${fileInfo.groupKey}
      <#if fileInfo.condition??>
        if(${fileInfo.condition}){
        <#list fileInfo.files as fileInfo>
            inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
            <#if fileInfo.generateType == "dynamic">
            // 动态文件生成
            DynamicGenerator.doGenerate(inputPath, outputPath, object);
            <#else>
            // 静态文件生成
            StaticGenerator.copyFilesByHutool(inputPath, outputPath);
            </#if>
        </#list>
        }
        <#else>
         <#list fileInfo.files as fileInfo>
             inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
             outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
             <#if fileInfo.generateType == "dynamic">
             // 动态文件生成
             DynamicGenerator.doGenerate(inputPath, outputPath, object);
             <#else>
             // 静态文件生成
             StaticGenerator.copyFilesByHutool(inputPath, outputPath);
             </#if>
         </#list>
        </#if>
    <#else>
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
    </#if>
</#list>
    }
}
