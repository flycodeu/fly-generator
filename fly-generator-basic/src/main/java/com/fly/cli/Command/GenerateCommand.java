package com.fly.cli.Command;

import cn.hutool.core.bean.BeanUtil;
import com.fly.generator.MainGenerator;
import com.fly.model.MainTemplateConfig;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * 生成文件命令
 */
@Data
@CommandLine.Command( name = "generate", mixinStandardHelpOptions = true )
public class GenerateCommand implements Callable<Integer> {
    /**
     * 作者注释
     */
    @CommandLine.Option( names = {"-a", "--author"}, interactive = true, arity = "0..1", description = "输入作者",echo = true )
    private String author = "fly";

    /**
     * 输出文字
     */
    @CommandLine.Option( names = {"-o", "--outputText"}, interactive = true, arity = "0..1", description = "输入输出文字" ,echo = true)
    private String outputText = "sum= ";

    /**
     * 是否循环
     */
    @CommandLine.Option( names = {"-l", "--loop"}, interactive = true, arity = "0..1", description = "是否循环",echo = true )
    private Boolean loop = true;

    @Override
    public Integer call() throws Exception {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        BeanUtil.copyProperties(this, mainTemplateConfig);
        MainGenerator.doGenerate(mainTemplateConfig);
        return 0;
    }
}
