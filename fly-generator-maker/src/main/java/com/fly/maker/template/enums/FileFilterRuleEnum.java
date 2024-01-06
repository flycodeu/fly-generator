package com.fly.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 规则过滤枚举值
 */
@Getter
public enum FileFilterRuleEnum {
    CONTAINS("包含", "contains"),
    STARTS_WITH("前缀匹配", "starts_with"),
    ENDS_WITH("后缀匹配", "ends_with"),
    REGEX("正则匹配", "regex"),
    EQUALS("等于", "equals");


    private String text;

    private String value;

    FileFilterRuleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据枚举值获取枚举
     *
     * @param value
     * @return
     */
    public static FileFilterRuleEnum getByEnumValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (FileFilterRuleEnum typeEnum : FileFilterRuleEnum.values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
