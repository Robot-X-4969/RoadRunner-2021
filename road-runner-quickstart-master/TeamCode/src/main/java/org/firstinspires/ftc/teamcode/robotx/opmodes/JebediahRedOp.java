package org.firstinspires.ftc.teamcode.robotx.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.robotx.modules.*;
import org.firstinspires.ftc.teamcode.robotx.libraries.*;
@Disabled
@TeleOp(name = "JebediahRedOp", group = "Competition")
public class JebediahRedOp extends XOpMode {

    RgbRedTeam rgbRedTeam;
    //public StoneClaw stoneClaw;
    public FlywheelIntake flywheelIntake;
    //CoachDDrive coachDDrive;
    public OrientationDrive orientationDrive;
    public StoneArm stoneArm;
    public StoneLift stoneLift;
    public FoundationPins foundationPins;


    public void initModules(){
        super.initModules();
        //stoneClaw = new StoneClaw(this);
        //activeModules.add(stoneClaw);

        flywheelIntake = new FlywheelIntake(this);
        activeModules.add(flywheelIntake);

        stoneArm = new StoneArm(this);
        activeModules.add(stoneArm);

        rgbRedTeam = new RgbRedTeam(this);
        activeModules.add(rgbRedTeam);

        //coachDDrive = new CoachDDrive(this);
        //activeModules.add(coachDDrive);

        orientationDrive = new OrientationDrive(this);
        activeModules.add(orientationDrive);

        stoneLift = new StoneLift(this);
        activeModules.add(stoneLift);

        foundationPins = new FoundationPins(this);
        activeModules.add(foundationPins);
    }
}
