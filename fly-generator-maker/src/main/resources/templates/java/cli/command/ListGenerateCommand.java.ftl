package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

/**
 * 展示所有文件命令
 */
@CommandLine.Command( name = "list",description = "查看文件列表",mixinStandardHelpOptions = true )
public class ListGenerateCommand implements Runnable {

    @Override
    public void run() {
        // 输入路径
        String inputPath = "${fileConfig.inputRootPath}";
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files) {
            System.out.println(file);
        }
    }
}
