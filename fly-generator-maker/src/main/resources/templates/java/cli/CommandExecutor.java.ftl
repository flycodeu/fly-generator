package ${basePackage}.cli;

import ${basePackage}.cli.command.ConfigCommand;
import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.ListGenerateCommand;
import picocli.CommandLine;

/**
 * 主命令
 */
@CommandLine.Command( name = "${name}", mixinStandardHelpOptions = true )
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
