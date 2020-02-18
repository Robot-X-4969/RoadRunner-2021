package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robotx.libraries.XModule;

public class ChooseParkingPosition extends XModule {

    public boolean isWall = false;
    public boolean isBridge = false;
    //Ending Positions
    public boolean isBuildingSide = false;
    public boolean isLoadingSide = false;
    //Starting Positions

    public ChooseParkingPosition(OpMode op){super(op);}

    public void loop() {
        while (isBridge == false && isWall == false) {
            opMode.telemetry.addLine("Wall or bridge?");
            if (xGamepad2().dpad_up.wasPressed()) {
                isBridge = true;
            }
            else if (xGamepad2().dpad_down.wasPressed()) {
                isWall = true;
            }
        }
        while (isLoadingSide == false && isBuildingSide == false){
            opMode.telemetry.addLine("Left or right?");
            if (xGamepad2().dpad_right.wasPressed()) {
                isLoadingSide = true;
            } else if (xGamepad2().dpad_left.wasPressed()) {
                isBuildingSide = true;
            }
        }
    }
}
