package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robotx.libraries.XModule;

public class ParkingSlide extends XModule {

    public Servo slideServo;
    public boolean slideOut = false;

    public double inPos = 0.5;
    public double outPos = 1;

    public long setTime;

    ElapsedTime slideTimer = new ElapsedTime();

    public ParkingSlide(OpMode op){super(op);}

    public void init(){
        slideServo = opMode.hardwareMap.servo.get("slideServo");
        slideServo.setPosition(inPos);
        setTime = System.currentTimeMillis();
    }

    public void toggleSlide(){
        if (slideOut){
            slideServo.setPosition(inPos);
            slideOut = false;
        }
        else {
            slideServo.setPosition(outPos);
            slideOut = true;
        }
    }
    public void loop(){
        opMode.telemetry.addData("Elapsed time:", (int)slideTimer.seconds());

        if (xGamepad1().x.wasPressed() && slideTimer.seconds() > 90){
            toggleSlide();
        }
    }
}
