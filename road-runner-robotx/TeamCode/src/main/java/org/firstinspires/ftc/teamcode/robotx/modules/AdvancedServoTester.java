package org.firstinspires.ftc.teamcode.robotx.modules;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.robotx.libraries.XModule;

public class AdvancedServoTester extends XModule {
    public AdvancedServoTester(OpMode op){super(op);}

    public Servo servo1;
    public Servo servo2;
    public Servo servo3;
    public Servo servo4;
    public Servo servo5;
    public Servo servo6;
    public Servo servo7;
    public Servo servo8;
    public Servo servo9;
    public Servo servo10;
    public Servo servo11;
    public Servo servo12;

    public int port = 0;
    public double servoPosition;
    boolean scaleEnabled = false;
    boolean setPosEnabled = false;



    public void init(){
        servo1 = opMode.hardwareMap.servo.get("servo1");
        servo2 = opMode.hardwareMap.servo.get("servo2");
        servo3 = opMode.hardwareMap.servo.get("servo3");
        servo4 = opMode.hardwareMap.servo.get("servo4");
        servo5 = opMode.hardwareMap.servo.get("servo5");
        servo6 = opMode.hardwareMap.servo.get("servo6");
        servo7 = opMode.hardwareMap.servo.get("servo7");
        servo8 = opMode.hardwareMap.servo.get("servo8");
        servo9 = opMode.hardwareMap.servo.get("servo9");
        servo10 = opMode.hardwareMap.servo.get("servo10");
        servo11 = opMode.hardwareMap.servo.get("servo11");
        servo12 = opMode.hardwareMap.servo.get("servo12");
        servoPosition = 0.0;
    }
    public void loop(){
        if (xGamepad1().a.wasPressed()) {
            if (scaleEnabled) {
                if (port == 0){
                    servo1.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }
                else if (port == 1){
                    servo2.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }
                else if (port == 2){
                    servo3.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }
                else if (port == 3){
                    servo4.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }
                else if (port == 4){
                    servo5.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }
                else if (port == 5){
                    servo6.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }
                else if (port == 6){
                    servo7.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }
                else if (port == 7){
                    servo8.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }
                else if (port == 8){
                    servo9.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }
                else if (port == 9){
                    servo10.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }
                else if (port == 10){
                    servo11.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }
                else if (port == 11){
                    servo12.scaleRange(0.0, 1.0);
                    scaleEnabled = false;
                }

            } else {
                if (port == 0){
                    servo1.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
                else if (port == 1){
                    servo2.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
                else if (port == 2){
                    servo3.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
                else if (port == 3){
                    servo4.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
                else if (port == 4){
                    servo5.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
                else if (port == 5){
                    servo6.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
                else if (port == 6){
                    servo7.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
                else if (port == 7){
                    servo8.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
                else if (port == 8){
                    servo9.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
                else if (port == 9){
                    servo10.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
                else if (port == 10){
                    servo11.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
                else if (port == 11){
                    servo12.scaleRange(0.02, 0.98);
                    scaleEnabled = true;
                }
            }
        }

        double unit = 0.1;
        if (xGamepad1().left_bumper.isDown()) {
            unit = 0.01;
        }
        if (xGamepad1().right_bumper.isDown()) {
            unit = 0.001;
        }
        if (xGamepad1().left_bumper.isDown() && xGamepad1().right_bumper.isDown()) {
            unit = 0.0001;
        }
        opMode.telemetry.addData("Unit", unit);

        if (xGamepad1().dpad_up.wasPressed()) {
            servoPosition += unit;
        }
        if (xGamepad1().dpad_down.wasPressed()) {
            servoPosition -= unit;
        }

        if (servoPosition > 1.0) {
            servoPosition = 1.0;
        }
        if (servoPosition < 0.0) {
            servoPosition = 0.0;
        }

        //Set the servo position
        if (port == 0 && setPosEnabled){
            servo1.setPosition(servoPosition);
        }
        if (port == 1 && setPosEnabled){
            servo2.setPosition(servoPosition);
        }
        if (port == 2 && setPosEnabled){
            servo3.setPosition(servoPosition);
        }
        if (port == 3 && setPosEnabled){
            servo4.setPosition(servoPosition);
        }
        if (port == 4 && setPosEnabled){
            servo5.setPosition(servoPosition);
        }
        if (port == 5 && setPosEnabled){
            servo6.setPosition(servoPosition);
        }
        if (port == 6 && setPosEnabled){
            servo7.setPosition(servoPosition);
        }
        if (port == 7 && setPosEnabled){
            servo8.setPosition(servoPosition);
        }
        if (port == 8 && setPosEnabled){
            servo9.setPosition(servoPosition);
        }
        if (port == 9 && setPosEnabled){
            servo10.setPosition(servoPosition);
        }
        if (port == 10 && setPosEnabled){
            servo11.setPosition(servoPosition);
        }
        if (port == 11 && setPosEnabled){
            servo12.setPosition(servoPosition);
        }

        if (xGamepad1().x.wasPressed()){
            if (setPosEnabled){
                setPosEnabled = false;
            }
            else {
                setPosEnabled = true;
            }
        }

        //Change servo port
        if (xGamepad1().dpad_right.wasPressed() && port <= 11){
            port++;
        }
        if (xGamepad1().dpad_left.wasPressed() && port >= 1){
            port--;
        }
        opMode.telemetry.addData("Scale Enabled?", scaleEnabled);
        opMode.telemetry.addData("Servo Position", servoPosition);
        opMode.telemetry.addData("Port:", port);
        opMode.telemetry.addData("Set position?", setPosEnabled);
    }
}
