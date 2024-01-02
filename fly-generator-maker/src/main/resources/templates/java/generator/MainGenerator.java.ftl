package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
    <#if fileInfo.generateType == "dynamic">
        // 动态文件生成
${indent}DynamicGenerator.doGenerate(inputPath, outputPath, object);
    <#else>
        // 静态文件生成
${indent}StaticGenerator.copyFilesByHutool(inputPath, outputPath);
    </#if>
</#macro>

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
            <@generateFile fileInfo=fileInfo indent="            "/>
        </#list>
        }
        <#else>
         <#list fileInfo.files as fileInfo>
             <@generateFile fileInfo=fileInfo indent="        "/>
         </#list>
        </#if>
    <#else>
<#if fileInfo.condition??>
    if(${fileInfo.condition}){
    <@generateFile fileInfo=fileInfo indent="        "/>
        }
    <#else>
    <@generateFile fileInfo=fileInfo indent="        "/>
</#if>

</#if>
</#list>
    }
}
