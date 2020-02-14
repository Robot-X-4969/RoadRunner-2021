package org.firstinspires.ftc.teamcode.robotx.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.robotx.modules.*;
import org.firstinspires.ftc.teamcode.robotx.libraries.*;

@TeleOp(name = "JebediahOp", group = "Competition")
public class JebediahOp extends XOpMode {

    //public StoneClaw stoneClaw;
    public FlywheelIntake flywheelIntake;
    //CoachDDrive coachDDrive;
    public OrientationDrive orientationDrive;
    //public StoneArmServo stoneArmServo;
    //public StoneLift stoneLift;
    public FoundationPins foundationPins;
    //public CapstonePositioning capstonePositioning;
    public ContinuousServoTest continuousServoTest;
    //public ParkingSlide parkingSlide;
    public MasterStacker masterStacker;


    public void initModules() {
        super.initModules();
        //stoneClaw = new StoneClaw(this);
        //activeModules.add(stoneClaw);

        flywheelIntake = new FlywheelIntake(this);
        activeModules.add(flywheelIntake);

        //stoneArmServo = new StoneArmServo(this);
        //activeModules.add(stoneArmServo);

        //coachDDrive = new CoachDDrive(this);
        //activeModules.add(coachDDrive);

        orientationDrive = new OrientationDrive(this);
        activeModules.add(orientationDrive);

        //stoneLift = new StoneLift(this);
        //activeModules.add(stoneLift);

        foundationPins = new FoundationPins(this);
        activeModules.add(foundationPins);

        //capstonePositioning = new CapstonePositioning(this);
        //activeModules.add(capstonePositioning);

        continuousServoTest = new ContinuousServoTest(this);
        activeModules.add(continuousServoTest);

        //parkingSlide = new ParkingSlide(this);
        //activeModules.add(parkingSlide);

        masterStacker = new MasterStacker(this);
        activeModules.add(masterStacker);
    }

}
