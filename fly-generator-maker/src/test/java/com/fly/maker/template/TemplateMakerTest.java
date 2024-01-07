package com.fly.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.fly.maker.meta.Meta;
import com.fly.maker.template.enums.FileFilterRangeEnum;
import com.fly.maker.template.enums.FileFilterRuleEnum;
import com.fly.maker.template.model.FileFilterConfig;
import com.fly.maker.template.model.TemplateMakerConfig;
import com.fly.maker.template.model.TemplateMakerFileConfig;
import com.fly.maker.template.model.TemplateMakerModelConfig;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 测试bug
 */
public class TemplateMakerTest {
    @Test
    public void testMakeTemplateBug1() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "fly-generator-demo-projects/springboot-init";

        String inputFilePath1 = "/src/main/resources/application.yml";

        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        // 文件过滤配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setFileFilterConfigs(fileFilterConfigList);
        fileInfoConfig1.setPath(inputFilePath1);
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        templateMakerFileConfig.setFiles(Collections.singletonList(fileInfoConfig1));

        // 模型配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        long l = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, 1743817327068332032L);
        System.out.println(l);
    }

    @Test
    public void testMakeTemplateBug2() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "fly-generator-demo-projects/springboot-init";

        String inputFilePath1 = "./";

        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        // 文件过滤配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setFileFilterConfigs(fileFilterConfigList);
        fileInfoConfig1.setPath(inputFilePath1);
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        templateMakerFileConfig.setFiles(Collections.singletonList(fileInfoConfig1));

        // 模型配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        long l = TemplateMaker.makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig, 1743817327068332032L);
        System.out.println(l);
    }


    @Test
    public void testTemplateJson() {
        String str = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(str, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }
}