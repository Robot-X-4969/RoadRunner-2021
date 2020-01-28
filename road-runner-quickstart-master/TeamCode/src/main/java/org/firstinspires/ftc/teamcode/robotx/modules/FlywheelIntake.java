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
        opMode.telemetry.addData("Intake Power: ", intakePower);

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

        if (xGamepad2().left_bumper.wasPressed() && intakePower > 0){
            intakePower -= 0.05;
        }
        if (xGamepad2().right_bumper.wasPressed() && intakePower <= 1){
            intakePower += 0.05;
        }

        opMode.telemetry.addData("Stone in robot?", stone);

        /*opMode.telemetry.addData("Red:", intakeColor.red());
        opMode.telemetry.addData("Blue:", intakeColor.blue());
        opMode.telemetry.addData("Green:", intakeColor.green());
         */
        if(xGamepad2().dpad_down.wasPressed()) {
            toggleFly();
        }
        if(xGamepad2().dpad_up.wasPressed()){
            toggleFlyReverse();
        }

        if (intakeColor.red() > 1000 && intakeColor.green() > 1000 && flyForward){
            stone = true;
        }
        else {
            stone = false;
        }

        if (jiggle && timer.seconds() > .4){
            flywheelLeft.setPower(intakePower);
            flywheelRight.setPower(intakePower);

            flyForward = true;

            jiggle = false;
        }

        if (xGamepad2().b.wasPressed()){
            jiggleIntake();
        }
        if (xGamepad2().left_stick_button.wasPressed() || xGamepad2().right_stick_button.wasPressed()){
            intakePower = 0.5;
        }

        if (flyForward && stone){
            flywheelLeft.setPower(0.0);
            flywheelRight.setPower(0.0);

            isFlywheelOn = false;
            flyForward = false;
            flyBackward = false;
        }

        /*if(clawServo.getPosition() >= 0.05){
            if(xGamepad2().x.wasPressed()){
                flywheelLeft.setPower(0);
                flywheelRight.setPower(0);
            }

        }

         */
    }

}
