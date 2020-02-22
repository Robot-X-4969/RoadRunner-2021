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

    public double armIn = 0.87;
    public double armOut = 0.01;
    public double armUp = 0.55;

    public boolean isArmUp = false;
    public boolean isArmOut = false;
    public boolean returning = false;
    public boolean liftMoveSlightly = false;

    public boolean capstone = false;

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime capstoneTimer = new ElapsedTime();

    public boolean clawOpen = true;
    public boolean autoClose = false;
    public boolean autoIntake = true;

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
        clawServo.setPosition(0.28); //Set position to open
        setTime = System.currentTimeMillis();
        stoneArm.setPosition(armIn);
        intakeColor = opMode.hardwareMap.get(RevColorSensorV3.class, "intakeColor");
        intakeColor.setI2cAddress(I2cAddr.create7bit(0x52)); //The REV color sensor v3 uses this address
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
            clawServo.setPosition(0);
            clawOpen = false;
        } else {
            clawServo.setPosition(0.28);
            clawOpen = true;
        }
    }

    public void returnArm() {
        if (isArmOut) {
            if (capstone) {
                clawServo.setPosition(1.0);
                clawOpen = true;
                returning = true;
            } else {
                clawServo.setPosition(0.28);
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
        if (magSwitch.getState()) {
            magPressed = false;
        } else {
            magPressed = true;
        }

        liftPos = encoder.getCurrentPosition();


        //opMode.telemetry.addData("Magnetic Switch Pressed?", magPressed);
        opMode.telemetry.addData("Lift position:", liftPos);

        //opMode.telemetry.addData("Motor Power: ", liftMotor.getPower() + xGamepad2().left_stick_y + " Encoder Value: " + encoder.getCurrentPosition());

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
        } else if (liftMotor.getCurrentPosition() <= 200 && !goingUp && xGamepad2().left_stick_y >= 0 && !magPressed && !homing) {
            liftMotor.setPower(-0.007);
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
            stackLevel = 490;
        }
        else if (level == 2){
            stackLevel = 960;
        }
        else if (level == 3){
            stackLevel = 1350;
        }
        else if (level == 4){
            stackLevel = 1720;
        }
        else if (level == 5){
            stackLevel = 560; //Switch arm position
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

        /*if (level >= 4){
            levelOffset = 1870;
        }
        else {
            levelOffset = 50;
        }

         */

        //////////////////BEGIN ARM CODE////////////////////////
        if (intakeColor.red() > 1000 && intakeColor.green() > 1000 && clawOpen && autoIntake){
            stoneArm.setPosition(0.96);
            clawOpen = false;
            autoCloseClaw();
        }

        //Prevent arm from automatically coming down if the claw breaks so that we can feed
        if (xGamepad1().left_stick_button.wasPressed() && xGamepad1().right_stick_button.wasPressed()){
            if (autoIntake){
                autoIntake = false;
            }
            else {
                autoIntake = true;
            }
        }

        if (autoClose && timer.seconds() > 0.5){
            clawServo.setPosition(0.0);
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
        if (timer.seconds() > 0.5 && returning && !capstone){
            clawServo.setPosition(0.28);
            liftMotor.setPower(1.0);
            liftMoveSlightly = true;
            isArmOut = false;
            returning = false;

            timer.reset();
        }
        else if (timer.seconds() > 1.0 && returning && capstone){
            clawServo.setPosition(0.28);
            liftMotor.setPower(1.0);
            liftMoveSlightly = true;
            isArmOut = false;
            returning = false;

            capstone = false;

            timer.reset();
        }

        if (timer.seconds() > 0.4 && liftMoveSlightly){
            stoneArm.setPosition(armIn);
            liftMotor.setPower(-1.0);
            liftMoveSlightly = false;
        }

        /*if (xGamepad2().a.wasPressed()){
            grab();
        }
        if (deploy && timer.seconds() > .5){
            deploy();
            deploy = false;
        }

         */

        if (level >= 6){
            armOut = 0.43;
        }
        else {
            armOut = 0.01;
        }

        /*if(xGamepad2().dpad_left.wasPressed()){
            clawServo.setPosition(0);
            clawOpen = false;
        }
        if(xGamepad2().dpad_right.wasPressed()) {
            clawServo.setPosition(0.3);
            clawOpen = true;
        }

         */
        if (xGamepad2().x.wasPressed()){
            toggleClaw();
        }

        //Deploy capstone only if it is 10 seconds until endgame. This prevents accidentally dropping the capstone before endgame
        //Driver 1 can override timer by pulling both triggers at the same time
        if ((xGamepad1().left_trigger == 1.0 && xGamepad1().right_trigger == 1)){
            clawServo.setPosition(1.0);
        }
        if (xGamepad2().y.wasPressed() && capstoneTimer.seconds() > 80){
            capstone = true;
        }
        opMode.telemetry.addData("Capstone?", capstone);
    }
}
