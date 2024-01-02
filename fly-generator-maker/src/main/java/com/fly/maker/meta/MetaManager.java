package com.fly.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * 元信息初始化管理
 */
public class MetaManager {

    private static volatile Meta meta;

    /**
     * 防止外部实例化
     */
    private MetaManager() {
    }

    /**
     * 单例模式---双检锁
     * 避免多线程同时进入导致meta被初始化多次
     *
     * @return meta
     */
    public static Meta getMeta() {
        if (meta == null) {
            synchronized (MetaManager.class) {
                if (meta == null) {
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    /**
     * 初始化元信息
     *
     * @return meta
     */
    private static Meta initMeta() {
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
        //需要判断参数是否正常
        MetaValidator.doValidateAndFill(newMeta);
        return newMeta;
    }
}
