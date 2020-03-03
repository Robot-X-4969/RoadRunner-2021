package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robotx.libraries.XModule;

public class AutonArm extends XModule {
    public AutonArm(OpMode op){super(op);}

    public Servo dumbassServo; //Reference to Daniel's 3D-printed arm
    public Servo evanServo; //Because it grabs the little children (stones)

    public double armInPos = 0.05;
    public double armCarryPos = 0.15;
    public double armOutPos = 0.52;
    public double armStackPos = 0.42;

    public double clawClosedPos = 0.37;
    public double clawInPos = 0.48;
    public double clawOpenPos = 0;

    public void init(){
        dumbassServo = opMode.hardwareMap.servo.get("dumbassServo");
        evanServo = opMode.hardwareMap.servo.get("evanServo");
        dumbassServo.setPosition(armInPos);
        evanServo.setPosition(clawInPos);
    }
    public void ArmStack(){ dumbassServo.setPosition(armStackPos); }
    public void ArmDown(){
        dumbassServo.setPosition(armOutPos);
    }
    public void ArmUp(){
        dumbassServo.setPosition(armInPos);
    }
    public void ArmCarry(){
        dumbassServo.setPosition(armCarryPos);
    }


    public void CloseClaw(){
        evanServo.setPosition(clawClosedPos);
    }
    public void OpenClaw(){
        evanServo.setPosition(clawOpenPos);
    }
    public void ClawIn(){ evanServo.setPosition(clawInPos); }


    public void loop(){}
}
