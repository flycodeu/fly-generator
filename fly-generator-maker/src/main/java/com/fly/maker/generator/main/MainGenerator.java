package com.fly.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.fly.maker.generator.GitGenerator;
import com.fly.maker.generator.JarGenerator;
import com.fly.maker.generator.ScriptGenerator;
import com.fly.maker.generator.file.DynamicFileGenerator;
import com.fly.maker.meta.Meta;
import com.fly.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 重写父类template的方法
 */
public class MainGenerator extends GenerateTemplate {
    @Override
    protected String buildDist(String outputPath, String shellOutPutFilePath, String jarPath, String sourceCopyDestPath) {
        System.out.println("不生成dist");
        return "";
    }


}
