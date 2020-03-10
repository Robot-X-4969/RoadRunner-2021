package org.firstinspires.ftc.teamcode.robotx.modules;

import android.graphics.Path;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robotx.libraries.XModule;
import org.openftc.revextensions2.ExpansionHubEx;
import org.openftc.revextensions2.ExpansionHubMotor;

//Combination of StoneArmServo and StoneLift
// This is so that we can have the lift and the arm be dependent on each other for some actions

public class MasterStacker extends XModule {
    public MasterStacker(OpMode op) {
        super(op);
    }

    public DcMotor liftMotor;
    public DcMotor encoder;
    public double motorPower = 0.002;
    public DigitalChannel magSwitch;
    public boolean magPressed = true;
    public boolean goingUp = false;

    public int stoneHeight = 460;

    public int level = 1;
    public int stackLevel;

    public boolean isAutoLiftMoving = false;
    public int liftPos;
    public boolean homing = false;

    public int levelOffset = 50;

    public RevColorSensorV3 intakeColor;
    public Servo stoneArm;
    double armPower;
    boolean deployed = false;
    public Servo clawServo;
    long setTime;
    boolean deploy = false;

    //Teleop claw positions
    public double teleopClawOpen = 0.5;
    public double teleopClawClosed = 0.75;
    public double teleopCapstone = 0.0;

    public double armIn = 0.13;
    public double armOut = 1.0;
    public double armUp = 0.37;
    public double armOnStone = 0.0;

    public boolean isArmUp = false;
    public boolean isArmOut = false;
    public boolean returning = false;
    public boolean liftMoveSlightly = false;

    public boolean capstone = false;
    public boolean speakTime = true;  //Determine whether the phone will say remaining time

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime capstoneTimer = new ElapsedTime();


    //Autonomous claw positions
    public boolean clawOpen = true;
    public boolean autoClose = false;
    public boolean autoIntake = true;

    //Re-state motors to check current draw
    ExpansionHubMotor left, right;
    ExpansionHubEx expansionHub;

    public void init() {
        liftMotor = opMode.hardwareMap.dcMotor.get("liftMotor");
        //liftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        encoder = opMode.hardwareMap.dcMotor.get("liftMotor");
        encoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //endStop = opMode.hardwareMap.touchSensor.get("endStop");
        //capServo = opMode.hardwareMap.servo.get("capServo");
        magSwitch = opMode.hardwareMap.get(DigitalChannel.class, "magSwitch");
        magSwitch.setMode(DigitalChannel.Mode.INPUT);
        //initialize motor
        stoneArm = opMode.hardwareMap.servo.get("stoneArm2");
        //stoneArm.setDirection(DcMotorSimple.Direction.REVERSE);
        //stoneArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        clawServo = opMode.hardwareMap.servo.get("clawServo");
        clawServo.setPosition(teleopClawOpen); //Set position to open
        setTime = System.currentTimeMillis();
        stoneArm.setPosition(armIn);
        intakeColor = opMode.hardwareMap.get(RevColorSensorV3.class, "intakeColor");
        intakeColor.setI2cAddress(I2cAddr.create7bit(0x52)); //The REV color sensor v3 uses this address

        expansionHub = opMode.hardwareMap.get(ExpansionHubEx.class, "Expansion Hub 2");
        left = (ExpansionHubMotor) opMode.hardwareMap.dcMotor.get("flywheelLeft");
        right = (ExpansionHubMotor) opMode.hardwareMap.dcMotor.get("flywheelRight");
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

    public void toggleClaw() {
        if (clawOpen) {
            clawServo.setPosition(teleopClawClosed);
            clawOpen = false;
        } else {
            clawServo.setPosition(teleopClawOpen);
            clawOpen = true;
        }
    }

    public void returnArm() {
        if (isArmOut) {
            if (capstone) {
                clawServo.setPosition(teleopCapstone);
                clawOpen = true;
                returning = true;
            } else {
                clawServo.setPosition(teleopClawOpen);
                clawOpen = true;
                returning = true;
            }
            timer.reset();
        }
    }

    public void autoCloseClaw() {
        autoClose = true;

        timer.reset();
    }

    public void loop() {
        //Check intake current draw
        opMode.telemetry.addData("Left current draw:", left.getCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS));
        opMode.telemetry.addData("RIght current draw:", right.getCurrentDraw(ExpansionHubEx.CurrentDrawUnits.AMPS));

        if (magSwitch.getState()) {
            magPressed = false;
        } else {
            magPressed = true;
        }

        liftPos = encoder.getCurrentPosition();


        //opMode.telemetry.addData("Magnetic Switch Pressed?", magPressed);
        opMode.telemetry.addData("Lift position:", liftPos);

        //check if the encoder position is greater than the starting position and that there is no power from
        //the joy sticks.
        if (!magPressed && xGamepad2().left_stick_y == 0 && !isAutoLiftMoving && !liftMoveSlightly && !homing) {
            liftMotor.setPower(motorPower); //if so, set a constant motor power
        }
        //Check to see if the lift is going down and if the magnetic limit switch is pressed
        else if ((xGamepad2().left_stick_y > 0 && magPressed && !goingUp && !homing) || (isAutoLiftMoving && magPressed && !goingUp && !homing)) {
            liftMotor.setPower(0.0); //If so, set the motor power to 0
            //liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            isAutoLiftMoving = false;
        } else if (liftMotor.getCurrentPosition() <= 150 && !goingUp && xGamepad2().left_stick_y >= 0 && !magPressed && !homing) {
            liftMotor.setPower(-0.007);
            isAutoLiftMoving = false;
        } else if (!isAutoLiftMoving && !homing) {
            liftMotor.setPower(-xGamepad2().left_stick_y); // if not, just set it to the joystick value as normal
        }

        ///////////////HOMING ROUTINE//////////////////////////
        if (magPressed && xGamepad2().dpad_right.wasPressed()) {
            liftMotor.setPower(0.2);
            homing = true;
        }
        if (homing && !magPressed) {
            homing = false;
            liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        ///////////////AUTO LIFT FOR STACKING//////////////////
        if (xGamepad2().right_bumper.wasPressed()){
            encoder.setTargetPosition(stackLevel);
            liftMotor.setPower(1.0);
            goingUp = true;
            isAutoLiftMoving = true;
            level++;
        }
        else if (xGamepad2().left_bumper.wasPressed()){
            liftMotor.setPower(-1.0);
            isAutoLiftMoving = true;
        }

        if (level == 1){
            stackLevel = 510;
        }
        else if (level == 2){
            stackLevel = 980;
        }
        else if (level == 3){
            stackLevel = 1370;
        }
        else if (level == 4){
            stackLevel = 1720;
        }
        else if (level == 5){
            stackLevel = 580; //Switch arm position
        }
        else if (level == 6){
            stackLevel = 960;
        }
        else if (level == 7){
            stackLevel = 1370;
        }
        else if (level == 8){
            stackLevel = 1715;
        }
        else if (level == 9){
            stackLevel = 2125;
        }
        else if (level == 10){
            stackLevel = 2505;
        }

        if (goingUp && liftPos >= encoder.getTargetPosition()){
            liftMotor.setPower(0.0);
            goingUp = false;
            isAutoLiftMoving = false;
        }

        if (xGamepad1().dpad_up.wasPressed()){
            level++;
        }
        else if (xGamepad1().dpad_down.wasPressed() && level >= 2){
            level--;
        }
        if (xGamepad1().dpad_left.wasPressed()){
            level = 1;
        }
        if (xGamepad1().dpad_right.wasPressed()){
            level = liftPos/stoneHeight;
        }
        opMode.telemetry.addData("Level:", level);


        //////////////////BEGIN ARM CODE////////////////////////
        if (intakeColor.red() > 1000 && intakeColor.green() > 1000 && clawOpen && autoIntake && liftMotor.getCurrentPosition() <= 200){
            stoneArm.setPosition(1.0);
            clawOpen = false;
            autoCloseClaw();
        }

        //Prevent arm from automatically coming down if the claw breaks so that we can feed
        if (xGamepad1().left_stick_button.wasPressed() || xGamepad1().right_stick_button.wasPressed()){
            if (autoIntake){
                autoIntake = false;
            }
            else {
                autoIntake = true;
            }
        }


        opMode.telemetry.addData("Auto intake mode:", autoIntake);

        if (autoClose && timer.seconds() > 0.2){
            clawServo.setPosition(teleopClawClosed);
            autoClose = false;
        }

        if ((xGamepad2().right_trigger >= 0.7 && !isArmUp) || (xGamepad2().right_bumper.wasPressed())){ //Raise the arm
            stoneArm.setPosition(armUp);
            isArmUp = true;
        }

        if (xGamepad2().left_trigger >= 0.7 && isArmUp){ //If same button is pressed twice, reset arm
            stoneArm.setPosition(armIn);
            isArmUp = false;
        }

        if (xGamepad2().a.wasPressed() && isArmUp){ //Move arm into placing position
            stoneArm.setPosition(armOut);
            isArmUp = false;
            isArmOut = true;
        }
        if (isArmOut && xGamepad2().a.wasReleased()){ //Once the button is released, reset the arm
            returnArm();
        }
        if (timer.seconds() > 0.12 && returning && !capstone){
            clawServo.setPosition(teleopClawOpen);
            liftMotor.setPower(1.0);
            liftMoveSlightly = true;
            isAutoLiftMoving = true;
            isArmOut = false;
            returning = false;
            stoneArm.setPosition(armIn);

            timer.reset();
        }
        else if (timer.seconds() > 0.48 && returning && capstone){
            clawServo.setPosition(teleopClawOpen);
            liftMotor.setPower(1.0);
            liftMoveSlightly = true;
            isAutoLiftMoving = true;
            isArmOut = false;
            returning = false;
            stoneArm.setPosition(armIn);

            capstone = false;

            timer.reset();
        }

        if (timer.seconds() > 0.2 && liftMoveSlightly){
            liftMotor.setPower(-1.0);
            liftMoveSlightly = false;
            isAutoLiftMoving = true;
        }

        if (level >= 6){
            armOut = 0.55;
        }
        else {
            armOut = 1.0;
        }

        if (xGamepad2().x.wasPressed()){
            toggleClaw();
        }

        //Deploy capstone only if it is 10 seconds until endgame. This prevents accidentally dropping the capstone before endgame
        //Driver 1 can override timer by pulling both triggers at the same time
        if ((xGamepad1().left_trigger >= 0.8 && xGamepad1().right_trigger >= 0.8)){
            clawServo.setPosition(teleopCapstone);
        }
        if (xGamepad2().y.wasPressed()){
            capstone = true;
        }
        opMode.telemetry.addData("Capstone?", capstone);

        //Auto drive coach mode --> telemetry reads out times
        opMode.telemetry.addData("Remaining time", 120 - capstoneTimer.seconds());

        if (speakTime) {
            if (capstoneTimer.seconds() == 60) {
                opMode.telemetry.speak("1 minute remaining");
            }
            if (capstoneTimer.seconds() == 90) {
                opMode.telemetry.speak("Endgame!");
            }
            if (capstoneTimer.seconds() == 110) {
                opMode.telemetry.speak("10");
            }
            if (capstoneTimer.seconds() == 111) {
                opMode.telemetry.speak("9");
            }
            if (capstoneTimer.seconds() == 112) {
                opMode.telemetry.speak("8");
            }
            if (capstoneTimer.seconds() == 113) {
                opMode.telemetry.speak("7");
            }
            if (capstoneTimer.seconds() == 114) {
                opMode.telemetry.speak("6");
            }
            if (capstoneTimer.seconds() == 115) {
                opMode.telemetry.speak("5");
            }
            if (capstoneTimer.seconds() == 116) {
                opMode.telemetry.speak("4");
            }
            if (capstoneTimer.seconds() == 117) {
                opMode.telemetry.speak("3");
            }
            if (capstoneTimer.seconds() == 118) {
                opMode.telemetry.speak("2");
            }
            if (capstoneTimer.seconds() == 119) {
                opMode.telemetry.speak("1");
            }
            if (capstoneTimer.seconds() == 120) {
                opMode.telemetry.speak("Stop");
            }
        }
    }
}
