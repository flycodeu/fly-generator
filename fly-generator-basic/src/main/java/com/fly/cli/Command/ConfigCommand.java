package com.fly.cli.Command;

import cn.hutool.core.util.ReflectUtil;
import com.fly.model.MainTemplateConfig;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * 展示所有参数信息
 */
@CommandLine.Command( name = "config", mixinStandardHelpOptions = true )
public class ConfigCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("查看参数信息");
//        Class<MainTemplateConfig> mainTemplateConfigClass = MainTemplateConfig.class;
//        Field[] fields = mainTemplateConfigClass.getFields();
        Field[] fields = ReflectUtil.getFields(MainTemplateConfig.class);
        for (Field field : fields) {
            System.out.println("字段名称" + field.getName());
            System.out.println("字段类型" + field.getType());
        }
    }
}
