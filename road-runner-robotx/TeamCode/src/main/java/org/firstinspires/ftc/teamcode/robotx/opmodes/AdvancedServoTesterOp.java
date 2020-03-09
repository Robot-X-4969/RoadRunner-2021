package org.firstinspires.ftc.teamcode.robotx.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robotx.libraries.XOpMode;
import org.firstinspires.ftc.teamcode.robotx.modules.AdvancedServoTester;

@TeleOp(name = "AdvancedServoTesterOp", group = "Tests")
public class AdvancedServoTesterOp extends XOpMode {
    public AdvancedServoTester advancedServoTester;

    public void initModules(){
        super.initModules();

        advancedServoTester = new AdvancedServoTester(this);
        activeModules.add(advancedServoTester);
    }
}
