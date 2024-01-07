package com.fly.maker.template;

import cn.hutool.core.util.StrUtil;
import com.fly.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模板制作工具
 */
public class TemplateMakerUtils {

    /**
     * 从根路径移除相应的文件配置
     *
     * @param fileInfoList
     * @return
     */
    public static List<Meta.FileConfig.FileInfo> removeGroupFilesFromRoot(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 获取所有分组
        List<Meta.FileConfig.FileInfo> groupFileInfoList = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        // 获取分组里面的文件列表，打平
        List<Meta.FileConfig.FileInfo> groupInnerPathList = groupFileInfoList
                .stream()
                .flatMap(fileInfo -> fileInfo.getFiles().stream())
                .collect(Collectors.toList());
        // 获取分组里面所有的输入路径集合
        Set<String> fileInputPathSet = groupInnerPathList
                .stream()
                .map(Meta.FileConfig.FileInfo::getInputPath)
                .collect(Collectors.toSet());
        // 集合去重,将所有的文件列表和去重的输入路径进行过滤
        return fileInfoList
                .stream()
                .filter(fileInfo -> !fileInputPathSet.contains(fileInfo.getInputPath()))
                .collect(Collectors.toList());

    }
}
