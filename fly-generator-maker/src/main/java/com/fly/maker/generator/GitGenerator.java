package com.fly.maker.generator;

import java.io.*;

public class GitGenerator {
    public static void doGenerator(String projectDir) throws IOException, InterruptedException {
        // 调用Process类执行Maven打包命令
        String gitInit = "git init";

        ProcessBuilder processBuilder = new ProcessBuilder(gitInit.split(" "));
        processBuilder.directory(new File(projectDir));
        Process process = processBuilder.start();

        // 读取命令输出
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("git初始化成功：" + exitCode);
    }

}
