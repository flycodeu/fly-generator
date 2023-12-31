package com.fly.cli.pattern;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 被遥控的设备
 */
public class Device {
    private String name;

    public Device(String name) {
        this.name = name;
    }

    public void turnOn() {
        System.out.println("已打开" + this.name);
    }

    public void turnOff() {
        System.out.println("已关闭" + this.name);
    }
}
