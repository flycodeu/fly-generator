package com.fly.maker.generator;

import java.io.*;

public class JarGenerator {
    public static void doGenerator(String projectDir) throws IOException, InterruptedException {
        // 调用Process类执行Maven打包命令
        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";
        String otherMavenCommand = "mvn clean package -DskipTests=true";
        String mavenCommand = winMavenCommand;

        ProcessBuilder processBuilder = new ProcessBuilder(mavenCommand.split(" "));
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
        System.out.println("Maven打包命令执行完毕，退出码：" + exitCode);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        doGenerator("D:\\fly\\project\\fly-generator\\fly-generator\\fly-generator-basic");
    }
}
