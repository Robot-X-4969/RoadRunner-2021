package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.robotx.libraries.XModule;

import java.util.logging.XMLFormatter;

public class MotorEncoderTest extends XModule {

    public DcMotor liftMotor;

    public MotorEncoderTest(OpMode op){super(op);}

    public void init(){
        liftMotor = opMode.hardwareMap.dcMotor.get("liftMotor");
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void start(){
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void loop(){
        opMode.telemetry.addData("Encoder position:", liftMotor.getCurrentPosition());

        liftMotor.setPower(xGamepad1().left_stick_y);
    }
}
