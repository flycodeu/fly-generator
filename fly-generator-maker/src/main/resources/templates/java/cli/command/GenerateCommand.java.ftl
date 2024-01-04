package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;

import freemarker.template.TemplateException;
import lombok.Data;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;
<#macro generateModel indent modelInfo>
${indent}/**
${indent}* ${modelInfo.description}
${indent}*/
${indent}@CommandLine.Option( names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}",</#if> "--${modelInfo.fieldName}"}, interactive = true, arity = "0..1", <#if modelInfo.description??>description = "${modelInfo.description}"</#if>,echo = true )
${indent}private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??>=${modelInfo.defaultValue?c}</#if>;
</#macro>

<#macro  generateCommand indent modelInfo>
${indent}System.out.println("请输入${modelInfo.groupName}配置信息");
${indent}CommandLine commandLine = new CommandLine(${modelInfo.type}Command.class);
${indent}commandLine.execute(${modelInfo.allArgsStr});
</#macro>
/**
 * 生成文件命令
 */
@Data
@CommandLine.Command( name = "generate", mixinStandardHelpOptions = true )
public class GenerateCommand implements Callable<Integer> {
<#list  modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
    /**
    * ${modelInfo.groupName}
    */
    static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();
    /**
    * ${modelInfo.groupName}
    */
    @CommandLine.Command( name = "${modelInfo.groupKey}", mixinStandardHelpOptions = true )
    @Data
    static class ${modelInfo.type}Command implements Runnable{
        <#list modelInfo.models as subModelInfo>
            <@generateModel indent="       " modelInfo=subModelInfo></@generateModel>
        </#list>

        @Override
        public void run() {
            <#list  modelInfo.models as subModelInfo>
            ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
            </#list>
            }
        }
    <#else>
    <@generateModel indent="    " modelInfo=modelInfo></@generateModel>
    </#if>
</#list>
    @Override
    public Integer call() throws TemplateException, IOException {
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#if modelInfo.condition??>
        if(${modelInfo.condition}){
            <@generateCommand indent="             " modelInfo=modelInfo/>
        }
        <#else>
        <@generateCommand indent="       " modelInfo=modelInfo/>
        </#if>
        </#if>
        </#list>
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        dataModel.${modelInfo.groupKey}=${modelInfo.groupKey};
        </#if>
    </#list>
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}
