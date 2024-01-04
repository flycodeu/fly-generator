package ${basePackage}.cli.command;

import cn.hutool.core.util.ReflectUtil;
import ${basePackage}.model.DataModel;
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
        Field[] fields = ReflectUtil.getFields(DataModel.class);
        for (Field field : fields) {
            System.out.print("字段名称" + field.getName()+"\t");
            System.out.print("字段类型" + field.getType());
            System.out.println();
        }
    }
}
