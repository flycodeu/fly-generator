package com.fly.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * 动态生成文件
 */
public class DynamicFileGenerator {

    /**
     * 使用相对路径生成文件
     *
     * @param relativeInputPath 相对输入路径
     * @param outputPath        输出路径
     * @param model             数据模型
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerate(String relativeInputPath, String outputPath, Object model) throws IOException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        int lastSplitIndex = relativeInputPath.lastIndexOf("/");
        String basePackagePath = relativeInputPath.substring(0, lastSplitIndex);
        String templateName = relativeInputPath.substring(lastSplitIndex + 1);

        // 指定模板文件所在的路径
        ClassTemplateLoader templateLoader = new ClassTemplateLoader(DynamicFileGenerator.class, basePackagePath);
        configuration.setTemplateLoader(templateLoader);

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        // 创建模板对象，加载指定模板
        Template template = configuration.getTemplate(templateName);

        // 文件不存在则创建文件和父目录
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }

        // 生成
        Writer out = new FileWriter(outputPath);
        template.process(model, out);

        // 生成文件后别忘了关闭哦
        out.close();
    }

    /**
     * 基础的生成模板
     *
     * @param inputPath
     * @param outputPath
     * @param model
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerateBasic(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是FreeMarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        // 第二步：设置模板文件所在的路径。
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);
        // 第三步：设置模板文件使用的字符集。一般就是utf-8.
        configuration.setDefaultEncoding("utf-8");
        configuration.setEncoding(Locale.CANADA, "UTF-8");
        // 第四步：加载模板文件，创建一个模板对象。
        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName, "utf-8");

        // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。

        // 第六步：创建一个Writer对象，一般创建FileWriter对象，指定生成的文件名。
        // 文件不存在
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)), StandardCharsets.UTF_8));
        // 第七步：调用模板对象的process方法输出文件。
        template.process(model, out);

        // 第八步：关闭流。
        out.close();
    }
}
