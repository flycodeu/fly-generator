package com.fly.cli.example;

import picocli.CommandLine;

@CommandLine.Command( name = "main", mixinStandardHelpOptions = true )
public class SubCommandExample implements Runnable {
    @Override
    public void run() {
        System.out.println("主方法执行");
    }

    @CommandLine.Command( name = "query", mixinStandardHelpOptions = true )
    static class QueryCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("查询命令执行");
        }
    }


    @CommandLine.Command( name = "delete", mixinStandardHelpOptions = true )
    static class DeleteCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("删除命令执行");
        }
    }

    @CommandLine.Command( name = "add", mixinStandardHelpOptions = true )
    static class AddCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("增加命令执行");
        }
    }

    public static void main(String[] args) {
        // 主命令
//        String[] myargs = new String[]{};
        // 查看主命令的Help
        //String[] myargs = new String[]{"--help"};

        // 查看添加命令
//        String[] myargs = new String[]{"add"};

        // 添加命令帮助
        String[] myargs = new String[]{"add", "--help"};
        int exitCode = new CommandLine(new SubCommandExample())
                .addSubcommand(new QueryCommand())
                .addSubcommand(new DeleteCommand())
                .addSubcommand(new AddCommand())
                .execute(myargs);
        System.exit(exitCode);
    }
}
