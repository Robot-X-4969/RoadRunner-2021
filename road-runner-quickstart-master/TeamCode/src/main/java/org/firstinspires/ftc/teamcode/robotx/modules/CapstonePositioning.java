package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robotx.modules.*;
import org.firstinspires.ftc.teamcode.robotx.libraries.*;

public class CapstonePositioning extends XModule {

    public Servo capstoneServo;

    public CapstonePositioning(OpMode op){super(op);}

    public void init () {
        capstoneServo = opMode.hardwareMap.servo.get("capstoneServo");
        capstoneServo.setPosition(0.46);
    }
    public void loop () {

        if(xGamepad2().x.wasPressed()) {
            if(capstoneServo.getPosition() == 0.46) {
                capstoneServo.setPosition(0.96);
            } else {
                capstoneServo.setPosition(0.46);
            }
        }
    }
}
