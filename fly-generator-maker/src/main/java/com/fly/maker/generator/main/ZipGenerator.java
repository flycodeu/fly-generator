package com.fly.maker.generator.main;

/**
 * 构建代码生成器打包zip,重写父类方法
 */
public class ZipGenerator extends GenerateTemplate {

    @Override
    protected String buildDist(String outputPath, String shellOutPutFilePath, String jarPath, String sourceCopyDestPath) {
        String distPath = super.buildDist(outputPath, shellOutPutFilePath, jarPath, sourceCopyDestPath);
        return super.buildZip(distPath);
    }
}
