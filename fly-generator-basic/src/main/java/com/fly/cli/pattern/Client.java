package com.fly.cli.pattern;

public class Client {

    public static void main(String[] args) {
        Device tv = new Device("TV");
        Device stereo = new Device("stereo");

        // 创建具体的命令对象
        TurnOnCommand turnOnCommand = new TurnOnCommand(tv);
        TurnOffCommand turnOffCommand = new TurnOffCommand(stereo);

        // 创建命令控制者
        RemoteControl remote = new RemoteControl();

        // 执行命令
        remote.setCommand(turnOnCommand);
        remote.onButtonWasPushed();

        remote.setCommand(turnOffCommand);
        remote.onButtonWasPushed();
    }
}
