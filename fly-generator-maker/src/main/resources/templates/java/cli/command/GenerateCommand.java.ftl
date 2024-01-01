package ${basePackage}.cli.Command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * 生成文件命令
 */
@Data
@CommandLine.Command( name = "generate", mixinStandardHelpOptions = true )
public class GenerateCommand implements Callable<Integer> {
    <#list  modelConfig.models as modelInfo>
    /**
     * ${modelInfo.description}
     */
    @CommandLine.Option( names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}",</#if> "--${modelInfo.fieldName}"}, interactive = true, arity = "0..1", <#if modelInfo.description??>description = "${modelInfo.description}"</#if>,echo = true )
    private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??>=${modelInfo.defaultValue?c}</#if>;
</#list>
    @Override
    public Integer call() throws Exception {
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}
