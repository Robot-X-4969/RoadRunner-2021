package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.robotx.libraries.*;

public class StoneLift extends XModule {
    public StoneLift(OpMode op){super(op);}

    DcMotor liftMotor;
    DcMotor encoder;
    public double motorPower = -0.15;
    public DigitalChannel magSwitch;
    public boolean magPressed = true;
    public boolean goingUp = false;

    public int stoneHeight = 200;

    public int level = 3;

    public void init(){
        liftMotor = opMode.hardwareMap.dcMotor.get("liftMotor");
        //liftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        encoder = opMode.hardwareMap.dcMotor.get("flywheelRight");
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
        encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }


    public void loop() {
        if(magSwitch.getState()){
            magPressed = false;
        }
        else {
            magPressed = true;
        }

        opMode.telemetry.addData("Magnetic Switch Pressed?", magPressed);
        opMode.telemetry.addData("Lift position:", encoder.getCurrentPosition());

        //opMode.telemetry.addData("Motor Power: ", liftMotor.getPower() + xGamepad2().left_stick_y + " Encoder Value: " + encoder.getCurrentPosition());

        //check if the encoder position is greater than the starting position and that there is no power from
        //the joy sticks.
        if(!magPressed && xGamepad2().left_stick_y == 0){
            liftMotor.setPower(motorPower); //if so, set a constant motor power
        }
        //Check to see if the lift is going down and if the magnetic limit switch is pressed
        else if(xGamepad2().left_stick_y > 0 && magPressed){
            liftMotor.setPower(0.0); //If so, set the motor power to 0
            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        else{
            liftMotor.setPower(xGamepad2().left_stick_y); // if not, just set it to the joystick value as normal
        }


        ///////////////AUTO LIFT FOR STACKING//////////////////
        if (xGamepad2().right_bumper.wasPressed()){
            liftMotor.setTargetPosition(level * stoneHeight);
            liftMotor.setPower(1.0);
            goingUp = true;
            level++;
        }
        else if (xGamepad2().left_bumper.wasPressed()){
            liftMotor.setPower(-1.0);
        }

        if (goingUp && liftMotor.getCurrentPosition() >= liftMotor.getTargetPosition()){
            liftMotor.setPower(0.0);
            goingUp = false;
        }
        if (xGamepad1().dpad_up.wasPressed()){
            level++;
        }
        else if (xGamepad1().dpad_down.wasPressed() && level >= 4){
            level--;
        }
        if (xGamepad1().dpad_left.wasPressed()){
            level = 3;
        }
        if (xGamepad1().dpad_right.wasPressed()){
            level = liftMotor.getCurrentPosition()/stoneHeight;
        }
        opMode.telemetry.addData("Level:", level);
    }

}

