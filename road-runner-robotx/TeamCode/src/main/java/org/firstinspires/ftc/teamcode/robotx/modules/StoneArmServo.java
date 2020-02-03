package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robotx.libraries.XModule;

/**
 * Created by John David Sniegocki and Sam McDowell on 10.28.2019
 */
public class StoneArmServo extends XModule {

    public Servo stoneArm;
    double armPower;
    boolean deployed = false;
    public Servo clawServo;
    long setTime;
    boolean deploy = false;

    public double armIn;
    public double armOut;
    public double armUp;

    public boolean isArmUp = false;
    public boolean isArmOut = false;
    public boolean returning = false;

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime capstoneTimer = new ElapsedTime();

    public boolean clawOpen = false;

    public StoneArmServo(OpMode op) {
        super(op);
    }

    public void init() {
        //initialize motor
        stoneArm = opMode.hardwareMap.servo.get("stoneArm");
        //stoneArm.setDirection(DcMotorSimple.Direction.REVERSE);
        //stoneArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        clawServo = opMode.hardwareMap.servo.get("clawServo");
        clawServo.setPosition(0); //Set position to closed
        setTime = System.currentTimeMillis();
        stoneArm.setPosition(armIn);
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

    public void returnArm(){
        if (isArmOut){
            clawServo.setPosition(0.3);
            returning = true;

            timer.reset();
        }
    }

    public void loop () {
        if (xGamepad2().y.wasPressed()){
            stoneArm.setPosition(armUp);
            isArmUp = true;
        }
        if (xGamepad2().a.wasPressed() && isArmUp){
            stoneArm.setPosition(armOut);
            isArmUp = false;
            isArmOut = true;
        }
        if (isArmOut && xGamepad2().a.wasReleased() && stoneArm.getPosition() == armOut){
            returnArm();
        }
        if (timer.seconds() > 0.5 && returning){
            stoneArm.setPosition(armIn);
            isArmOut = false;
            returning = false;
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
}