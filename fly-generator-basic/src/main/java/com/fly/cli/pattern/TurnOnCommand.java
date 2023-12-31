package com.fly.cli.pattern;

/**
 * 开启设备的具体命令
 */
public class TurnOnCommand implements Command {
    private Device device;

    public TurnOnCommand(Device device) {
        this.device = device;
    }

    @Override
    public void execute() {
        device.turnOn();
    }
}
