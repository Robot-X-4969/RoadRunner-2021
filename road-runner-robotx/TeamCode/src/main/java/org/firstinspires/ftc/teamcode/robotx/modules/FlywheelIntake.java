package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robotx.libraries.*;

public class FlywheelIntake extends XModule {
    public DcMotor flywheelLeft;
    public DcMotor flywheelRight;
    public boolean isFlywheelOn = false;
    public RevColorSensorV3 intakeColor;
    public boolean stone = false;
    public Servo clawServo;

    ElapsedTime timer = new ElapsedTime();
    long setTime;
    boolean jiggle = false;

    public boolean flyForward = false;
    public boolean flyBackward = false;

    public double intakePower = 0.5;


    public FlywheelIntake(OpMode op){super(op);}


    public void init(){
        flywheelLeft = opMode.hardwareMap.dcMotor.get("flywheelLeft");
        flywheelLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheelRight = opMode.hardwareMap.dcMotor.get("flywheelRight");
        intakeColor = opMode.hardwareMap.get(RevColorSensorV3.class, "intakeColor");
        clawServo = opMode.hardwareMap.servo.get("clawServo");
        intakeColor.setI2cAddress(I2cAddr.create7bit(0x52)); //The REV color sensor v3 uses this address
        setTime = System.currentTimeMillis();

    }
    public void toggleFly(){
        if (isFlywheelOn){
            isFlywheelOn = false;
            flywheelLeft.setPower(0.0);
            flywheelRight.setPower(0.0);
            flyForward = false;
            flyBackward = false;
        }
        else{
            isFlywheelOn = true;
            flywheelRight.setPower(intakePower);
            flywheelLeft.setPower(intakePower);
            flyForward = true;
            flyBackward = false;
        }
    }
    public void toggleFlyReverse(){
        if (isFlywheelOn){
            isFlywheelOn = false;
            flywheelLeft.setPower(0.0);
            flywheelRight.setPower(0.0);
            flyBackward = false;
            flyForward = false;
        }
        else{
            isFlywheelOn = true;
            flywheelRight.setPower(-intakePower);
            flywheelLeft.setPower(-intakePower);
            flyBackward = true;
            flyForward = false;
        }
    }
    public void jiggleIntake(){
            flywheelRight.setPower(-intakePower);
            flywheelLeft.setPower(-intakePower);

            timer.reset();

            jiggle = true;
        }
    public void loop(){
        //Display intake current power
        opMode.telemetry.addData("Intake Power: ", intakePower);

        //Main statement for setting the intake power
        if (isFlywheelOn && !jiggle){
            if (flyForward){
                flywheelLeft.setPower(intakePower);
                flywheelRight.setPower(intakePower);
            }
            else if (flyBackward){
                flywheelRight.setPower(-intakePower);
                flywheelLeft.setPower(-intakePower);
            }
        }

        //Allows for intake power adjustment by using the bumpers
        if (xGamepad2().left_bumper.wasPressed() && intakePower > 0){
            intakePower -= 0.05;
        }
        if (xGamepad2().right_bumper.wasPressed() && intakePower <= 1){
            intakePower += 0.05;
        }

        //Toggle intake in
        if(xGamepad2().dpad_down.wasPressed()) {
            toggleFly();
        }

        //Toggle intake out
        if(xGamepad2().dpad_up.wasPressed()){
            toggleFlyReverse();
        }

        //Press b to reverse intake for a small amount of time to right stones
        if (xGamepad2().b.wasPressed()){
            jiggleIntake();
        }

        //After time has expired, return intake to original direction
        //Use boolean to only do this action when we want
        if (jiggle && timer.seconds() > .4){
            flywheelLeft.setPower(intakePower);
            flywheelRight.setPower(intakePower);

            flyForward = true;

            jiggle = false;
        }


        //Reset intake power to 0.5 default
        if (xGamepad2().left_stick_button.wasPressed() || xGamepad2().right_stick_button.wasPressed()){
            intakePower = 0.5;
        }


        ///////////////////////Color sensing///////////////////////////////////

        opMode.telemetry.addData("Stone in robot?", stone);

        //Test rgb values for REV color sensor
        /*opMode.telemetry.addData("Red:", intakeColor.red());
        opMode.telemetry.addData("Blue:", intakeColor.blue());
        opMode.telemetry.addData("Green:", intakeColor.green());
         */

        //Set boolean to true if yellow is detected
        if (intakeColor.red() > 1000 && intakeColor.green() > 1000 && flyForward){ //Increase these numbers to reduce range from the sensor
            stone = true;
        }
        else {
            stone = false;
        }

        //If there is a stone in the robot, stop the intake
        //flyForward boolean allows intake to be reversed when there is a stone in the robot
        if (flyForward && stone){
            flywheelLeft.setPower(0.0);
            flywheelRight.setPower(0.0);

            isFlywheelOn = false;
            flyForward = false;
            flyBackward = false;
        }
    }

}
