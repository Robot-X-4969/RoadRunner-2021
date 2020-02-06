package org.firstinspires.ftc.teamcode.robotx.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.robotx.modules.*;
import org.firstinspires.ftc.teamcode.robotx.libraries.*;

@Disabled
@TeleOp(name = "RGB Test", group = "test")
public class RgbTest extends XOpMode {
    RgbSignals rgbSignals;

    public void initModules(){
        rgbSignals = new RgbSignals(this);
        activeModules.add(rgbSignals);
    }
}
