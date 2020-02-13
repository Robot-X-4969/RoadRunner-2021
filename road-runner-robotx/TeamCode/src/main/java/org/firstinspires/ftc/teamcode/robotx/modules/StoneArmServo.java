package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robotx.libraries.XModule;

/**
 * Created by John David Sniegocki and Sam McDowell on 10.28.2019
 */
public class StoneArmServo extends XModule {

    public RevColorSensorV3 intakeColor;
    public Servo stoneArm;
    double armPower;
    boolean deployed = false;
    public Servo clawServo;
    long setTime;
    boolean deploy = false;

    public double armIn = 0.88;
    public double armOut = 0.0525;
    public double armUp = 0.6;

    public boolean isArmUp = false;
    public boolean isArmOut = false;
    public boolean returning = false;
    public int level = 1;

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime capstoneTimer = new ElapsedTime();

    public boolean clawOpen = true;
    public boolean autoClose = false;

    public StoneArmServo(OpMode op) {
        super(op);
    }

    public void init() {
        //initialize motor
        stoneArm = opMode.hardwareMap.servo.get("stoneArm2");
        //stoneArm.setDirection(DcMotorSimple.Direction.REVERSE);
        //stoneArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        clawServo = opMode.hardwareMap.servo.get("clawServo");
        clawServo.setPosition(0.28); //Set position to open
        setTime = System.currentTimeMillis();
        stoneArm.setPosition(armIn);
        intakeColor = opMode.hardwareMap.get(RevColorSensorV3.class, "intakeColor");
        intakeColor.setI2cAddress(I2cAddr.create7bit(0x52)); //The REV color sensor v3 uses this address
    }

    public void toggleClaw(){
        if (clawOpen){
            clawServo.setPosition(0);
            clawOpen = false;
        }
        else {
            clawServo.setPosition(0.28);
            clawOpen = true;
        }
    }

    public void returnArm(){
        if (isArmOut){
            clawServo.setPosition(0.28);
            clawOpen = true;
            returning = true;

            timer.reset();
        }
    }
    public void autoCloseClaw(){
        autoClose = true;

        timer.reset();
    }

    public void start(){
        timer.reset();
    }

    public void loop () {
        if (intakeColor.red() > 1000 && intakeColor.green() > 1000 && clawOpen){
            stoneArm.setPosition(0.96);
            clawOpen = false;
            autoCloseClaw();
        }
        if (autoClose && timer.seconds() > 0.5){
            clawServo.setPosition(0.0);
            autoClose = false;
        }

        if ((xGamepad2().right_trigger >= 0.7 && !isArmUp) || (xGamepad2().right_bumper.wasPressed())){ //Raise the arm
            stoneArm.setPosition(armUp);
            isArmUp = true;
        }

        if (xGamepad2().left_trigger >= 0.7 && isArmUp){ //If same button is pressed twice, reset arm
            stoneArm.setPosition(armIn);
            isArmUp = false;
        }

        if (xGamepad2().a.wasPressed() && isArmUp){ //Move arm into placing position
            stoneArm.setPosition(armOut);
            isArmUp = false;
            isArmOut = true;
        }
        if (isArmOut && xGamepad2().a.wasReleased()){ //Once the button is released, reset the arm
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

        if (xGamepad1().dpad_up.wasPressed()){
            level++;
        }
        else if (xGamepad1().dpad_down.wasPressed() && level >= 2){
            level--;
        }
        if (xGamepad1().dpad_left.wasPressed()){
            level = 1;
        }
        if (xGamepad2().right_bumper.wasPressed()){
            level++;
        }

        if (level >= 5){
            armOut = 0.44;
        }
        else {
            armOut = 0.0525;
        }

        if(xGamepad2().dpad_left.wasPressed()){
            clawServo.setPosition(0);
            clawOpen = false;
        }
        if(xGamepad2().dpad_right.wasPressed()) {
            clawServo.setPosition(0.3);
            clawOpen = true;
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