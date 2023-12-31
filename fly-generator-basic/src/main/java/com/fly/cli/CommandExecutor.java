package com.fly.cli;

import com.fly.cli.Command.ConfigCommand;
import com.fly.cli.Command.GenerateCommand;
import com.fly.cli.Command.ListGenerateCommand;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 主命令
 */
@CommandLine.Command( name = "fly", mixinStandardHelpOptions = true )
public class CommandExecutor implements Runnable {
    private final CommandLine commandLine;
    {
        commandLine = new CommandLine(this)
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ListGenerateCommand());

    }



    @Override
    public void run() {
        System.out.println("输入 -- help查看命令");
    }

    /**
     * 执行命令
     *
     * @param args
     * @return
     */
    public Integer doExecute(String[] args) {
        return commandLine.execute(args);
    }
}
