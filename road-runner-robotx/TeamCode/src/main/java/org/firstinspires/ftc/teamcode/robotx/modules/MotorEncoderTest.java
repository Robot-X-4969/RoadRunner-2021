package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.robotx.libraries.XModule;

public class MotorEncoderTest extends XModule {

    public DcMotor liftMotor;
    public DcMotor encoder;
    public double motorPosition;

    public MotorEncoderTest(OpMode op){super(op);}

    public void init(){
        liftMotor = opMode.hardwareMap.dcMotor.get("liftMotor");
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        encoder = opMode.hardwareMap.dcMotor.get("flywheelRight");
    }

    public void loop(){
        opMode.telemetry.addData("Encoder position:", motorPosition);
        motorPosition = -liftMotor.getCurrentPosition();

        liftMotor.setPower(xGamepad1().left_stick_y);
    }
}
