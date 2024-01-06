package com.fly.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 范围过滤枚举值
 */
@Getter
public enum FileFilterRangeEnum {
    FILE_NAME("文件名称", "filename"),
    FILE_CONTENT("文件内容", "fileContent");


    private String text;

    private String value;

    FileFilterRangeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据枚举值获取枚举
     * @param value
     * @return
     */
    public static FileFilterRangeEnum getByEnumValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (FileFilterRangeEnum typeEnum : FileFilterRangeEnum.values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
