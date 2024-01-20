package com.fly.web.model.vo;

import cn.hutool.json.JSONUtil;
import com.fly.maker.meta.Meta;
import com.fly.web.model.entity.Generator;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

/**
 * 代码生成器vo
 */
@Data
public class GeneratorVO {
    /**
     * 相应人的信息
     */
    private UserVO userVO;

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础包
     */
    private String basePackage;

    /**
     * 版本
     */
    private String version;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 图片
     */
    private String picture;

    /**
     * 文件配置（json字符串）
     */
    private Meta.FileConfig fileConfig;

    /**
     * 模型配置（json字符串）
     */
    private Meta.ModelConfig modelConfig;

    /**
     * 代码生成器产物路径
     */
    private String distPath;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * vo转换为为实体类
     *
     * @param generatorVo
     * @return
     */
    public static Generator voToObj(GeneratorVO generatorVo) {
        if (generatorVo == null) {
            return null;
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorVo, generator);
        List<String> tagList = generatorVo.getTags();
        generator.setTags(JSONUtil.toJsonStr(tagList));
        Meta.FileConfig voFileConfig = generatorVo.getFileConfig();
        Meta.ModelConfig voModelConfig = generatorVo.getModelConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(voFileConfig));
        generator.setModelConfig(JSONUtil.toJsonStr(voModelConfig));
        return generator;
    }


    /**
     * 实体类转换为vo
     *
     * @param generator
     * @return
     */
    public static GeneratorVO objToVo(Generator generator) {
        if (generator == null) {
            return null;
        }
        GeneratorVO generatorVo = new GeneratorVO();
        BeanUtils.copyProperties(generator, generatorVo);
        generatorVo.setTags(JSONUtil.toList(generator.getTags(), String.class));
        generatorVo.setFileConfig(JSONUtil.toBean(generator.getFileConfig(), Meta.FileConfig.class));
        generatorVo.setModelConfig(JSONUtil.toBean(generator.getModelConfig(), Meta.ModelConfig.class));
        return generatorVo;
    }
}
