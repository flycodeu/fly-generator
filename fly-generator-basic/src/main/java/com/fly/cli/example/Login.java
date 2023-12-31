package com.fly.cli.example;

import picocli.CommandLine;

import java.util.concurrent.Callable;

public class Login implements Callable<Integer> {

    @CommandLine.Option( names = {"-u", "--user"}, description = "用户名" )
    String user;

    @CommandLine.Option( names = {"-p", "--password"}, interactive = true, description = "输入密码" )
    String password;

    @CommandLine.Option( names = {"-c", "--checkPassword"}, interactive = true, description = "确认密码" )
    String checkPassword;

    @Override
    public Integer call() throws Exception {
        System.out.println("password: " + password);
        System.out.println("checkPassword: " + checkPassword);
        return 0;
    }


    public static void main(String[] args) {
        new CommandLine(new Login()).execute("-u", "admin", "-p","-c");
    }
}
