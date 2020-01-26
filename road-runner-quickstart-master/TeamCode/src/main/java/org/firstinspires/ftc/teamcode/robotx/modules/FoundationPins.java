package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robotx.libraries.*;

public class FoundationPins extends XModule {
    public FoundationPins(OpMode op){super(op);}

    Servo rightPin;
    Servo leftPin;
    //Relative to the front of the robot
    boolean pinsOut = true;
    double rightOut = .34;
    double leftOut = .65;
    double rightIn = 0.15;
    double leftIn = 0.92;

    public void init(){
        rightPin = opMode.hardwareMap.servo.get("rightPin");
        leftPin = opMode.hardwareMap.servo.get("leftPin");
        rightPin.setPosition(rightOut);
        leftPin.setPosition(leftOut);
    }
    public void deployPins(){
        if (pinsOut){
            rightPin.setPosition(rightIn);
            leftPin.setPosition(leftIn);
            pinsOut = false;
        }
        else{
            rightPin.setPosition(rightOut);
            leftPin.setPosition(leftOut);
            pinsOut = true;
        }
    }
    public void loop(){
        if (xGamepad1().y.wasPressed()){
            deployPins();
        }
    }
}
