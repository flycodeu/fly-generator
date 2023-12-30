package com.fly.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 静态文件生成器
 */
public class StaticGenerator {

    public static void main(String[] args) throws IOException {
        //  获取项目路径
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        // 输入路径
        String inputPath = projectPath + File.separator + "fly-generator-demo-projects" + File.separator + "acm-template";
        System.out.println(inputPath);
        // 输出路径
        String targetPath = projectPath;
//        copyFilesByHutool(inputPath, targetPath);
        copyFilesByRecursive(inputPath, targetPath);
    }


    /**
     * 拷贝文件夹到对应的地方，将输入目录完整拷贝到输出目录
     *
     * @param inputPath  输入路径
     * @param targetPath 输出路径
     */
    public static void copyFilesByHutool(String inputPath, String targetPath) {
        FileUtil.copy(inputPath, targetPath, false);
    }

    /**
     * 原生File复制
     *
     * @param inputPath  输入路径
     * @param targetPath 输出路径
     * @throws IOException
     */
    public static void copyFilesByRecursive(String inputPath, String targetPath) throws IOException {
        File inputFile = new File(inputPath);
        File targetFile = new File(targetPath);
        copyFileByRecursive(inputFile, targetFile);
    }


    /**
     * 原生File复制
     *
     * @param inputPath  输入文件
     * @param targetPath 输出文件
     * @throws IOException
     */
    public static void copyFileByRecursive(File inputPath, File targetPath) throws IOException {
        // 判断输入路径是文件还是文件夹
        if (inputPath.isDirectory()) {
            System.out.println("目录名称-----" + inputPath.getName());
            System.out.println("目录大小-----" + inputPath.length());
            // 创建新的目录
            File newTargetPath = new File(targetPath, inputPath.getName());
            // 如果是目录
            if (!newTargetPath.exists()) {
                newTargetPath.mkdirs();
            }
            // 获取目录下的所有文件和目录
            File[] files = inputPath.listFiles();
            if (ArrayUtil.isEmpty(files)) {
                return;
            }
            for (File file : files) {
                // 递归调用 递归调用
                copyFileByRecursive(file, newTargetPath);
            }
        } else {
            // 如果是文件 直接拷贝
            Path destPath = targetPath.toPath().resolve(inputPath.getName());
            System.out.println("文件名称-----" + inputPath.getName());
            System.out.println("文件大小-----" + inputPath.length());
            Files.copy(inputPath.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
