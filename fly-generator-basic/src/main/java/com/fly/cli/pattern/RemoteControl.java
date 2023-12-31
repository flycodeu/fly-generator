package com.fly.cli.pattern;

/**
 * 遥控器
 */
public class RemoteControl {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void onButtonWasPushed() {
        command.execute();
    }
}
