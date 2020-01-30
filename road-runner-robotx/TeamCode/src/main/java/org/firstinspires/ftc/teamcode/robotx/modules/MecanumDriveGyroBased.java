package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.robotx.modules.*;
import org.firstinspires.ftc.teamcode.robotx.libraries.*;

/**
 * Created by KD on 11/25/2017.
 */

public class MecanumDriveGyroBased extends XModule {

    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;


    public MecanumDriveGyroBased(OpMode op) {
        super(op);
    }

    public void init() {
        frontLeft = opMode.hardwareMap.dcMotor.get("frontLeft");
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight = opMode.hardwareMap.dcMotor.get("frontRight");
        //frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight = opMode.hardwareMap.dcMotor.get("backRight");
        //backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft = opMode.hardwareMap.dcMotor.get("backLeft");
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void loop() {
        float forwardBackAxis = xGamepad1().left_stick_y; //Forward and backwards axis
        float leftRightAxis = xGamepad1().left_stick_x; //Strafing axis
        float spinAxis = -xGamepad1().right_stick_x; //spinning axis
        // Combine the 3 powers to get a power for each motor with Mechanum wheels.
        float FRpower = forwardBackAxis - leftRightAxis + spinAxis;
        float FLpower = forwardBackAxis + leftRightAxis - spinAxis;
        float BRpower = forwardBackAxis + leftRightAxis + spinAxis;
        float BLpower = forwardBackAxis - leftRightAxis - spinAxis;

        // Slow down the robot if bumpers are held down.
        if (xGamepad1().left_bumper.isDown() && xGamepad1().right_bumper.isDown()){
            FLpower = FLpower / 5;
            FRpower = FRpower / 5;
            BRpower = BRpower / 5;
            BLpower = BLpower / 5;
        } else if (xGamepad1().left_bumper.isDown() || xGamepad1().right_bumper.isDown()){
            FLpower = FLpower / 2;
            FRpower = FRpower / 2;
            BRpower = BRpower / 2;
            BLpower = BLpower / 2;
        }

        // Assign the finalized powers to the motors.
        frontRight.setPower(FRpower);
        frontLeft.setPower(FLpower);
        backRight.setPower(BRpower);
        backLeft.setPower(BLpower);

        // Output the encoder positions for each motor for debug.
        opMode.telemetry.addData("frontRight: ", frontRight.getCurrentPosition());
        opMode.telemetry.addData("frontLeft: ", frontLeft.getCurrentPosition());
        opMode.telemetry.addData("backRight: ", backRight.getCurrentPosition());
        opMode.telemetry.addData("backLeft: ", backLeft.getCurrentPosition());


    }



}



