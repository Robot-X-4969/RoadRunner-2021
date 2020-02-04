package org.firstinspires.ftc.teamcode.robotx.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robotx.libraries.XOpMode;
import org.firstinspires.ftc.teamcode.robotx.modules.MotorEncoderTest;

@TeleOp(name = "EncoderTestOp", group = "Tests")
public class EncoderTestOp extends XOpMode {

    MotorEncoderTest motorEncoderTest;

    public void initModules(){
        super.initModules();

        motorEncoderTest = new MotorEncoderTest(this);
        activeModules.add(motorEncoderTest);
    }
}
