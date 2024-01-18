package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;
import lombok.Data;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * 读取json文件生成代码
 */
@Data
@CommandLine.Command( name = "json-generate", mixinStandardHelpOptions = true )
public class JsonGenerateCommand implements Callable<Integer> {
    /**
     * 是否生成循环
     */
    @CommandLine.Option( names = {"-f", "--file"}, interactive = true, arity = "0..1", description = "json文件路径", echo = true )
    private String filePath;

    @Override
    public Integer call() throws TemplateException, IOException {
        // 读取json文件
        String jsonStr = FileUtil.readUtf8String(filePath);
        DataModel dataModel = JSONUtil.toBean(jsonStr, DataModel.class);
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}

