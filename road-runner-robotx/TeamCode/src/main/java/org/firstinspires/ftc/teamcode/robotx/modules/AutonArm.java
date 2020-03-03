package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robotx.libraries.XModule;

public class AutonArm extends XModule {
    public AutonArm(OpMode op){super(op);}

    public Servo dumbassServo; //Reference to Daniel's 3D-printed arm
    public Servo evanServo; //Because it grabs the little children (stones)

    public double armInPos;
    public double armOutPos;
    public double clawClosedPos;
    public double clawOpenPos;

    public void init(){
        dumbassServo = opMode.hardwareMap.servo.get("dumbassServo");
        evanServo = opMode.hardwareMap.servo.get("evanServo");
        dumbassServo.setPosition(armInPos);
        evanServo.setPosition(clawOpenPos);
    }
    public void reachForVictim(){
        dumbassServo.setPosition(armOutPos);
    }
    public void pullVictimIntoRobot(){
        dumbassServo.setPosition(armInPos);
    }

    public void beLikeEvanAndGrabTheLittleChildren(){
        evanServo.setPosition(clawClosedPos);
    }
    public void prepareForNextVictim(){
        evanServo.setPosition(clawOpenPos);
    }
    
    public void loop(){}
}
