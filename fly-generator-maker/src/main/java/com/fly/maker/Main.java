package com.fly.maker;


import com.fly.maker.cli.CommandExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        //args = new String[]{"generate","-l","-o","-a"}; // 示例参数
        //args = new String[]{"list"}; // 示例参数
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}