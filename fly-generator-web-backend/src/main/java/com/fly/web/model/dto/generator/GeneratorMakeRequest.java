package com.fly.web.model.dto.generator;

import com.fly.maker.meta.Meta;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 制作代码生成器请求
 */
@Data
public class GeneratorMakeRequest implements Serializable {
    /**
     * 生成器信息
     */
    private Meta meta;
    /**
     * 压缩文件路径
     */
    private String zipFilePath;

    private static final long serialVersionUID = 1L;
}