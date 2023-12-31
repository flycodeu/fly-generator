package com.fly.maker.generator.file;

import cn.hutool.core.io.FileUtil;

/**
 * 静态文件生成器
 */
public class StaticFileGenerator {


    /**
     * 拷贝文件夹到对应的地方，将输入目录完整拷贝到输出目录
     *
     * @param inputPath  输入路径
     * @param targetPath 输出路径
     */
    public static void copyFilesByHutool(String inputPath, String targetPath) {
        FileUtil.copy(inputPath, targetPath, false);
    }

}
