package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.robotx.libraries.*;

public class StoneLift extends XModule {
    public StoneLift(OpMode op){super(op);}

    DcMotor liftMotor;
    DcMotor encoder;
    public double motorPower = 0.01;
    public DigitalChannel magSwitch;
    public boolean magPressed = true;
    public boolean goingUp = false;

    public int stoneHeight = 494;

    public int level = 1;

    public boolean isAutoLiftMoving = false;
    public int liftPos;

    public int levelOffset = 50;

    public void init(){
        liftMotor = opMode.hardwareMap.dcMotor.get("liftMotor");
        //liftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        encoder = opMode.hardwareMap.dcMotor.get("liftMotor");
        encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //endStop = opMode.hardwareMap.touchSensor.get("endStop");
        //capServo = opMode.hardwareMap.servo.get("capServo");
        magSwitch = opMode.hardwareMap.get(DigitalChannel.class, "magSwitch");
        magSwitch.setMode(DigitalChannel.Mode.INPUT);
    }

    /*public void toggleCap(){
        if (capped){
            capServo.setPosition(inPos);
            capped = false;
        }
        else {
            capServo.setPosition(cappedPos);
            capped = true;
        }
    }*/

    public void start(){
        //liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }


    public void loop() {
        if(magSwitch.getState()){
            magPressed = false;
        }
        else {
            magPressed = true;
        }

        liftPos = encoder.getCurrentPosition();

        opMode.telemetry.addData("Magnetic Switch Pressed?", magPressed);
        opMode.telemetry.addData("Lift position:", liftPos);

        //opMode.telemetry.addData("Motor Power: ", liftMotor.getPower() + xGamepad2().left_stick_y + " Encoder Value: " + encoder.getCurrentPosition());

        //check if the encoder position is greater than the starting position and that there is no power from
        //the joy sticks.
        if(!magPressed && xGamepad2().left_stick_y == 0 && !isAutoLiftMoving){
            liftMotor.setPower(motorPower); //if so, set a constant mo[tor power
        }
        //Check to see if the lift is going down and if the magnetic limit switch is pressed
        else if((xGamepad2().left_stick_y > 0 && magPressed && !goingUp) || (isAutoLiftMoving && magPressed && !goingUp)){
            liftMotor.setPower(0.0); //If so, set the motor power to 0
            //liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            isAutoLiftMoving = false;
        }
        else if (!isAutoLiftMoving){
            liftMotor.setPower(-xGamepad2().left_stick_y); // if not, just set it to the joystick value as normal
        }


        ///////////////AUTO LIFT FOR STACKING//////////////////
        if (xGamepad2().right_bumper.wasPressed()){
            encoder.setTargetPosition((level * stoneHeight) - levelOffset);
            liftMotor.setPower(1.0);
            goingUp = true;
            isAutoLiftMoving = true;
            level++;
        }
        else if (xGamepad2().left_bumper.wasPressed()){
            liftMotor.setPower(-1.0);
            isAutoLiftMoving = true;
        }

        if (goingUp && liftPos >= encoder.getTargetPosition()){
            liftMotor.setPower(0.0);
            goingUp = false;
            isAutoLiftMoving = false;
        }


        if (xGamepad1().dpad_up.wasPressed()){
            level++;
        }
        else if (xGamepad1().dpad_down.wasPressed() && level >= 2){
            level--;
        }
        if (xGamepad1().dpad_left.wasPressed()){
            level = 3;
        }
        if (xGamepad1().dpad_right.wasPressed()){
            level = liftPos/stoneHeight;
        }
        opMode.telemetry.addData("Level:", level);

        if (level >= 4){
            levelOffset = 1870;
        }
        else {
            levelOffset = 50;
        }
    }
}
