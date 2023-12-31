package com.fly.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * 动态生成文件
 */
public class DynamicFileGenerator {

    public static void doGenerate(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是FreeMarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_21);
        // 第二步：设置模板文件所在的路径。
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);
        // 第三步：设置模板文件使用的字符集。一般就是utf-8.
//        configuration.setDefaultEncoding("utf-8");
        configuration.setEncoding(Locale.CANADA, "UTF-8");
        // 第四步：加载模板文件，创建一个模板对象。
        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName);

        // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。

        // 第六步：创建一个Writer对象，一般创建FileWriter对象，指定生成的文件名。
        // 文件不存在
        if (!FileUtil.exist(outputPath)){
            FileUtil.touch(outputPath);
        }
        Writer out = new FileWriter(outputPath);
        // 第七步：调用模板对象的process方法输出文件。
        template.process(model, out);

        // 第八步：关闭流。
        out.close();
    }
}
