package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robotx.libraries.*;

public class StoneClaw extends XModule {

    public Servo clawServo;
    public boolean clawOpen = false;

    public StoneClaw(OpMode op){super(op);}

    public void init () {
        clawServo = opMode.hardwareMap.servo.get("clawServo");
        clawServo.setPosition(0);
    }

    public void toggleClaw(){
        if (clawOpen){
            clawServo.setPosition(0);
        }
        else {
            clawServo.setPosition(0.6);
        }
    }

    public void loop () {
        if(xGamepad2().dpad_left.wasPressed()){
            clawServo.setPosition(0);
        }
        if(xGamepad2().dpad_right.wasPressed()) {
            clawServo.setPosition(0.6);
        }

        if (xGamepad2().x.wasPressed()){
            toggleClaw();
        }
    }
}
