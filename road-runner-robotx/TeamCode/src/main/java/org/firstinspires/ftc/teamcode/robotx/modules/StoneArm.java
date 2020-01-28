package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robotx.libraries.*;

/**
 * Created by John David Sniegocki and Sam McDowell on 10.28.2019
 */
public class StoneArm extends XModule {

    public DcMotor stoneArm;
    double armPower;
    boolean deployed = false;
    public Servo clawServo;
    long setTime;
    boolean deploy = false;

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime capstoneTimer = new ElapsedTime();

    public boolean clawOpen = false;

    public StoneArm(OpMode op) {
        super(op);
    }

    public void init() {
        //initialize motor
        stoneArm = opMode.hardwareMap.dcMotor.get("stoneArm");
        //stoneArm.setDirection(DcMotorSimple.Direction.REVERSE);
        //stoneArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        clawServo = opMode.hardwareMap.servo.get("clawServo");
        clawServo.setPosition(0); //Set position to closed
        setTime = System.currentTimeMillis();
        stoneArm.setPower(0.4);
    }
    public void grab(){ //Automatically grab stone and deploy arm or release stone and retract arm
        if (deployed){
            clawServo.setPosition(0.3);
            deploy = true;
        }
        else {
            clawServo.setPosition(0.0);
            deploy = true;
        }
        timer.reset();
    }
    public void deploy(){
        if (deployed){
            stoneArm.setPower(0.35);
            deployed = false;
        }
        else {
            stoneArm.setPower(-0.35);
            deployed = true;
        }
    }
    public void toggleClaw(){
        if (clawOpen){
            clawServo.setPosition(0);
            clawOpen = false;
        }
        else {
            clawServo.setPosition(0.3);
            clawOpen = true;
        }
    }

    public void loop () {
        if (xGamepad2().right_trigger > 0 || xGamepad2().left_trigger > 0){
            stoneArm.setPower(-((xGamepad2().right_trigger - xGamepad2().left_trigger)/1.5));
        }

        /*if (xGamepad2().a.wasPressed()){
            grab();
        }
        if (deploy && timer.seconds() > .5){
            deploy();
            deploy = false;
        }

         */

        if(xGamepad2().dpad_left.wasPressed()){
            clawServo.setPosition(0);
        }
        if(xGamepad2().dpad_right.wasPressed()) {
            clawServo.setPosition(0.3);
        }
        if (xGamepad2().x.wasPressed()){
            toggleClaw();
        }

        //Deploy capstone only if it is 5 seconds until endgame. This prevents accidentally dropping the capstone before endgame
        //Driver 1 can override timer by pulling both triggers at the same time
        if ((xGamepad2().y.wasPressed() && capstoneTimer.seconds() > 85) || (xGamepad1().left_trigger == 1.0 && xGamepad1().right_trigger == 1)){
            clawServo.setPosition(1.0);
        }
    }
    public void stop(){
        stoneArm.setPower(-.35);
    }
}