package org.firstinspires.ftc.teamcode.robotx.RAPS.RR.RAPS;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.path.heading.ConstantInterpolator;
import com.acmerobotics.roadrunner.path.heading.SplineInterpolator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveBase;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveREVOptimized;
import org.firstinspires.ftc.teamcode.robotx.modules.AutonArm;
import org.firstinspires.ftc.teamcode.robotx.modules.FlywheelIntake;
import org.firstinspires.ftc.teamcode.robotx.modules.FoundationPins;
import org.firstinspires.ftc.teamcode.robotx.modules.MasterStacker;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;

import static java.lang.Math.toRadians;

@Autonomous
public class FourStoneBlue extends LinearOpMode {


    //0 means skystone, 1+ means yellow stone
    //-1 for debug, but we can keep it like this because if it works, it should change to either 0 or 255

    private static int valMid = -1;
    private static int valLeft = -1;
    private static int valRight = -1;

    private static float rectHeight = .6f / 8f;
    private static float rectWidth = 1.5f / 8f;

    private static float CameraOffsetX = 0f
            / 8f;//changing this moves the three rects and the three circles left or right, range : (-2, 2) not inclusive
    private static float CameraOffsetY = 3f
            / 8f;//changing this moves the three rects and circles up or down, range: (-4, 4) not inclusive

    private static float[] midPos = {4f / 8f + CameraOffsetX, 4f / 8f + CameraOffsetY};//0 = col, 1 = row
    private static float[] leftPos = {2f / 8f + CameraOffsetX, 4f / 8f + CameraOffsetY};
    private static float[] rightPos = {6f / 8f + CameraOffsetX, 4f / 8f + CameraOffsetY};
    //moves all rectangles right or left by amount. units are in ratio to monitor

    private final int rows = 1280;
    private final int cols = 720;

    public boolean isLeft = false;
    public boolean isRight = false;
    public boolean isCenter = false;


    OpenCvInternalCamera phoneCam;
    FlywheelIntake flywheelIntake;
    //StoneArmServo stoneArmServo;
    MasterStacker masterStacker;
    FoundationPins pins;
    AutonArm autonArm;


    @Override
    public void runOpMode() throws InterruptedException {

        SampleMecanumDriveBase drive = new SampleMecanumDriveREVOptimized(hardwareMap);

        int cameraMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        phoneCam = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);
        //phoneCam = new OpenCvWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        phoneCam.openCameraDevice();//open camera
        phoneCam.setPipeline(new StageSwitchingPipeline());//different stages
        phoneCam.startStreaming(rows, cols, OpenCvCameraRotation.SIDEWAYS_RIGHT);//display on RC
        //width, height
        //width = height in this case, because camera is in portrait mode.

        /**
        if(getBatteryVoltage() >= 14.00){
            multiplier = 0.07;
            telemetry.addData("Multiplier", multiplier);
        }else if(getBatteryVoltage() < 14.00 && getBatteryVoltage() >= 13.80 ){
            multiplier = 0.06;
            telemetry.addData("Multiplier", multiplier);
        }else if(getBatteryVoltage() < 13.80 && getBatteryVoltage() >= 13.65){
            multiplier = 0.04;
            telemetry.addData("Multiplier", multiplier);
        }else if(getBatteryVoltage() < 13.65 && getBatteryVoltage() >= 13.40){
            multiplier = 0.023;
            telemetry.addData("Multiplier", multiplier);
        }else{
            multiplier = 0;
            telemetry.addData("Multiplier", multiplier);
        }**/


        flywheelIntake = new FlywheelIntake(this);
        flywheelIntake.init();

        masterStacker = new MasterStacker(this);
        masterStacker.init();

        pins = new FoundationPins(this);
        pins.init();

        autonArm = new AutonArm(this);
        autonArm.init();

        flywheelIntake.start();
        masterStacker.start();
        pins.start();
        autonArm.start();
        /**
        telemetry.addData("Starting Side: ", "Loading/Skystone");
        telemetry.addData("Position: ","Facing back wall, Color Sensor lines up with middle of tile");
        telemetry.update();

        telemetry.update();
        //HeadingInterpolator interp = new LinearInterpolator(Math.toRadians(drive.getExternalHeading()), Math.toRadians(45));



        **/
        //this is the main first trajectory.


        //Path 2 is the testing path, use this path to get the robot to go to a specific pos

        telemetry.addLine("Ready!");
        telemetry.update();
        waitForStart();


        if (isStopRequested()) return;

        while (opModeIsActive()) {



            telemetry.addData("Values", valLeft + "   " + valMid + "   " + valRight);
            /**
            telemetry.addData("Height", rows);
            telemetry.addData("Width", cols);

            telemetry.addData("Frame Count", phoneCam.getFrameCount());
            telemetry.addData("FPS", String.format("%.2f", phoneCam.getFps()));
            telemetry.addData("Total frame time ms", phoneCam.getTotalFrameTimeMs());
            telemetry.addData("Pipeline time ms", phoneCam.getPipelineTimeMs());
            **/

            telemetry.update();







            /////////////////////Movement///////////////////////

            masterStacker.stoneArm.setPosition(0.96);


            if(valLeft == 0 && valMid >= 1 && valRight >= 1){
                telemetry.addData("Skystone Position: ", "Left");
                telemetry.update();
                isLeft = true;
                autonArm.OpenClaw();
                drive.followTrajectorySync( //move to first skystone
                        drive.trajectoryBuilder()
                                .lineTo(new Vector2d(-12,28), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.ArmDown(); //collect skystone 1
                sleep(400);
                autonArm.CloseClaw();
                sleep(300);
                autonArm.ArmCarry();
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()//get to foundation
                                .setReversed(true)
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-87,29), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.OpenClaw();
                drive.followTrajectorySync( //go to second skystone
                        drive.trajectoryBuilder()
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(11,28.5), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.ArmDown(); //collect claw from position
                sleep(400);
                autonArm.CloseClaw();
                sleep(300);
                autonArm.ArmCarry();
                drive.followTrajectorySync( // head back to foundation, but the back corner, this is denoted by -90 vs 76
                        drive.trajectoryBuilder()
                                .setReversed(true)
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-96,30), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.OpenClaw();
                /////////////////3rd skystone/////////////////
                drive.followTrajectorySync( //go to third skystone
                        drive.trajectoryBuilder()
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-3,28.5), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.ArmDown(); //collect claw from position
                sleep(400);
                autonArm.CloseClaw();
                sleep(300);
                autonArm.ArmUp();
                drive.followTrajectorySync( // head back to foundation, but the back corner, this is denoted by -90 vs 76
                        drive.trajectoryBuilder()
                                .setReversed(true)
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-76,31), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.ArmStack(); //stack stone on foundation
                sleep(350);
                autonArm.OpenClaw();
                sleep(400);
                autonArm.ArmUp();
                autonArm.ClawIn();

                /////////////////Foundation///////////////
                drive.turnSync(toRadians(-88));
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .back(8) //get to a location where we can touch foundation
                                .addMarker(0.3, ()->{
                                    pins.deployPins();//latch onto foundation
                                    return Unit.INSTANCE;
                                })
                                .splineTo(new Pose2d(-50,20)) //move foundation to stacking position under skybridge
                                .addMarker(2.5, ()->{
                                    pins.deployPins();//unlatch onto foundation
                                    return Unit.INSTANCE;
                                })
                                .setReversed(true)
                                .lineTo(new Vector2d(-87,20), new ConstantInterpolator(0))
                                .setReversed(false)
                                .splineTo(new Pose2d(-35,26, 0), new ConstantInterpolator(0))
                                .build()
                );
                sleep(10000);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }else if(valLeft >= 1 && valMid >= 1 && valRight == 0){
                telemetry.addData("Skystone Position: ", "Right");
                telemetry.update();
                isRight = true;
                autonArm.OpenClaw();
                drive.followTrajectorySync( //move to first skystone
                        drive.trajectoryBuilder()
                                .lineTo(new Vector2d(4,28), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.ArmDown(); //collect skystone 1
                sleep(300);
                autonArm.CloseClaw();
                sleep(300);
                autonArm.ArmCarry();
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()//get to foundation
                                .setReversed(true)
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-87,29), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.OpenClaw();
                drive.followTrajectorySync( //go to second skystone
                        drive.trajectoryBuilder()
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(26,28.5), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.ArmDown(); //collect claw from position
                sleep(300);
                autonArm.CloseClaw();
                sleep(300);
                autonArm.ArmCarry();
                drive.followTrajectorySync( // head back to foundation, but the back corner, this is denoted by -90 vs 76
                        drive.trajectoryBuilder()
                                .setReversed(true)
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-96,30), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.OpenClaw();
                /////////////////3rd skystone/////////////////
                drive.followTrajectorySync( //go to third skystone
                        drive.trajectoryBuilder()
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-13,28), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.ArmDown(); //collect claw from position
                sleep(300);
                autonArm.CloseClaw();
                sleep(300);
                autonArm.ArmUp();
                drive.followTrajectorySync( // head back to foundation, but the back corner, this is denoted by -90 vs 76
                        drive.trajectoryBuilder()
                                .setReversed(true)
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-76,31), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.ArmStack(); //stack stone on foundation
                sleep(350);
                autonArm.OpenClaw();
                sleep(400);
                autonArm.ArmUp();
                autonArm.ClawIn();

                /////////////////Foundation///////////////
                drive.turnSync(toRadians(-88));
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .back(6) //get to a location where we can touch foundation
                                .addMarker(0.3, ()->{
                                    pins.deployPins();//latch onto foundation
                                    return Unit.INSTANCE;
                                })
                                .splineTo(new Pose2d(-50,20)) //move foundation to stacking position under skybridge
                                .addMarker(2.5, ()->{
                                    pins.deployPins();//unlatch onto foundation
                                    return Unit.INSTANCE;
                                })
                                .setReversed(true)
                                .lineTo(new Vector2d(-87,20), new ConstantInterpolator(0))
                                .setReversed(false)
                                .splineTo(new Pose2d(-35,26, 0), new ConstantInterpolator(0))
                                .build()
                );
                sleep(10000);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }else if(valLeft >= 1 && valMid == 0 && valRight >= 1){
                telemetry.addData("Skystone Position: ", "center");
                telemetry.update();
                isCenter = true;
                autonArm.OpenClaw();
                drive.followTrajectorySync( //move to first skystone
                        drive.trajectoryBuilder()
                                .lineTo(new Vector2d(-4,28), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.ArmDown(); //collect skystone 1
                sleep(300);
                autonArm.CloseClaw();
                sleep(300);
                autonArm.ArmCarry();
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()//get to foundation
                                .setReversed(true)
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-87,29), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.OpenClaw();
                drive.followTrajectorySync( //go to second skystone
                        drive.trajectoryBuilder()
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(19,28.5), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.ArmDown(); //collect claw from position
                sleep(300);
                autonArm.CloseClaw();
                sleep(300);
                autonArm.ArmCarry();
                drive.followTrajectorySync( // head back to foundation, but the back corner, this is denoted by -90 vs 76
                        drive.trajectoryBuilder()
                                .setReversed(true)
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-96,30), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.OpenClaw();
                /////////////////3rd skystone/////////////////
                drive.followTrajectorySync( //go to third skystone
                        drive.trajectoryBuilder()
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-12,28.5), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.ArmDown(); //collect claw from position
                sleep(300);
                autonArm.CloseClaw();
                sleep(300);
                autonArm.ArmUp();
                drive.followTrajectorySync( // head back to foundation, but the back corner, this is denoted by -90 vs 76
                        drive.trajectoryBuilder()
                                .setReversed(true)
                                .lineTo(new Vector2d(-15,22), new ConstantInterpolator(toRadians(0)))
                                .lineTo(new Vector2d(-78,31), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                autonArm.OpenClaw(); //drop third stone on foundation

                /////////////////Foundation And 4th stone///////////////

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.5, ()->{ //start the intake release routine
                                    flywheelIntake.flywheelLeft.setPower(0.5);
                                    return Unit.INSTANCE;
                                })
                                .addMarker(1.0, ()->{ //reverse to guarantee it gets out
                                    flywheelIntake.flywheelLeft.setPower(-0.5);
                                    return Unit.INSTANCE;
                                })
                                .addMarker(1.4, ()->{ // start full intake, open claw and move arm down
                                    flywheelIntake.flywheelLeft.setPower(0.5);
                                    flywheelIntake.flywheelRight.setPower(0.5);
                                    masterStacker.clawServo.setPosition(0.4);
                                    masterStacker.stoneArm.setPosition(masterStacker.armIn);
                                    return Unit.INSTANCE;
                                })
                                .lineTo(new Vector2d(-10,20), new ConstantInterpolator(toRadians(0)))
                                .splineTo(new Pose2d(-4.5,44,0), new ConstantInterpolator(toRadians(0)))
                                .build()
                );
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .forward(9)
                                .addMarker(()->{
                                    masterStacker.stoneArm.setPosition(1);//bring arm down
                                    masterStacker.clawServo.setPosition(0);//close claw
                                    return Unit.INSTANCE;
                                })
                                .strafeRight(5)
                        .build()
                );
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                //travel to the foundation midway point
                                .setReversed(true)
                                .splineTo(new Pose2d(-20,22,toRadians(0)), new ConstantInterpolator(toRadians(0)))
                                .splineTo(new Pose2d(-76,38, toRadians(-90))) //turn into the foundation
                                .build()
                );
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(()->{
                                    flywheelIntake.flywheelRight.setPower(0.35); //turn intake to .3 power
                                    flywheelIntake.flywheelLeft.setPower(0.35);
                                    masterStacker.stoneArm.setPosition(0.25); //stack stone
                                    return Unit.INSTANCE;
                                })
                                .back(8) //get to a location where we can touch foundation
                                .addMarker(0.4, ()->{
                                    pins.deployPins();//latch onto foundation
                                    masterStacker.clawServo.setPosition(0.4);//open claw
                                    return Unit.INSTANCE;
                                })
                                .splineTo(new Pose2d(-50,25, toRadians(0)), new SplineInterpolator(toRadians(-90), toRadians(0))) //move foundation to stacking position in pit
                                .addMarker(2.5, ()->{
                                    pins.deployPins();//unlatch onto foundation
                                    masterStacker.stoneArm.setPosition(0.96); //bring arm back in
                                    return Unit.INSTANCE;
                                })
                                .setReversed(true)// make sure it goes in build site
                                .lineTo(new Vector2d(-87,23), new ConstantInterpolator(0))
                                .setReversed(false) //go park
                                .splineTo(new Pose2d(-35,26, 0), new ConstantInterpolator(0))
                                .build()
                );

                sleep(10000);
            }


        }
    }



















    //detection pipeline
    static class StageSwitchingPipeline extends OpenCvPipeline {

        Mat yCbCrChan2Mat = new Mat();
        Mat thresholdMat = new Mat();
        Mat all = new Mat();
        List<MatOfPoint> contoursList = new ArrayList<>();

        enum Stage {//color difference. greyscale
            detection,//includes outlines
            THRESHOLD,//b&w
            RAW_IMAGE,//displays raw view
        }

        private Stage stageToRenderToViewport = Stage.detection;
        private Stage[] stages = Stage.values();

        @Override
        public void onViewportTapped() {
            /*
             * Note that this method is invoked from the UI thread
             * so whatever we do here, we must do quickly.
             */


            int currentStageNum = stageToRenderToViewport.ordinal();

            int nextStageNum = currentStageNum + 1;

            if (nextStageNum >= stages.length) {
                nextStageNum = 0;
            }

            stageToRenderToViewport = stages[nextStageNum];
        }

        @Override
        public Mat processFrame(Mat input) {
            contoursList.clear();
            /*
             * This pipeline finds the contours of yellow blobs such as the Gold Mineral
             * from the Rover Ruckus game.
             */

            //color diff cb.
            //lower cb = more blue = skystone = white
            //higher cb = less blue = yellow stone = grey
            Imgproc.cvtColor(input, yCbCrChan2Mat, Imgproc.COLOR_RGB2YCrCb);//converts rgb to ycrcb
            Core.extractChannel(yCbCrChan2Mat, yCbCrChan2Mat, 2);//takes cb difference and stores

            //b&w
            Imgproc.threshold(yCbCrChan2Mat, thresholdMat, 102, 255, Imgproc.THRESH_BINARY_INV);

            //outline/contour
            Imgproc.findContours(thresholdMat, contoursList, new Mat(), Imgproc.RETR_LIST,
                    Imgproc.CHAIN_APPROX_SIMPLE);
            yCbCrChan2Mat.copyTo(all);//copies mat object
            //Imgproc.drawContours(all, contoursList, -1, new Scalar(255, 0, 0), 3, 8);//draws blue contours

            //get values from frame
            double[] pixMid = thresholdMat.get((int) (input.rows() * midPos[1]),
                    (int) (input.cols() * midPos[0]));//gets value at circle
            valMid = (int) pixMid[0];

            double[] pixLeft = thresholdMat.get((int) (input.rows() * leftPos[1]),
                    (int) (input.cols() * leftPos[0]));//gets value at circle
            valLeft = (int) pixLeft[0];

            double[] pixRight = thresholdMat.get((int) (input.rows() * rightPos[1]),
                    (int) (input.cols() * rightPos[0]));//gets value at circle
            valRight = (int) pixRight[0];

            //create three points
            Point pointMid = new Point((int) (input.cols() * midPos[0]),
                    (int) (input.rows() * midPos[1]));
            Point pointLeft = new Point((int) (input.cols() * leftPos[0]),
                    (int) (input.rows() * leftPos[1]));
            Point pointRight = new Point((int) (input.cols() * rightPos[0]),
                    (int) (input.rows() * rightPos[1]));

            //draw circles on those points
            Imgproc.circle(all, pointMid, 5, new Scalar(255, 0, 0), 1);//draws circle
            Imgproc.circle(all, pointLeft, 5, new Scalar(255, 0, 0), 1);//draws circle
            Imgproc.circle(all, pointRight, 5, new Scalar(255, 0, 0), 1);//draws circle

            //draw 3 rectangles
            Imgproc.rectangle(//1-3
                    all,
                    new Point(
                            input.cols() * (leftPos[0] - rectWidth / 2),
                            input.rows() * (leftPos[1] - rectHeight / 2)),
                    new Point(
                            input.cols() * (leftPos[0] + rectWidth / 2),
                            input.rows() * (leftPos[1] + rectHeight / 2)),
                    new Scalar(0, 255, 0), 3);
            Imgproc.rectangle(//3-5
                    all,
                    new Point(
                            input.cols() * (midPos[0] - rectWidth / 2),
                            input.rows() * (midPos[1] - rectHeight / 2)),
                    new Point(
                            input.cols() * (midPos[0] + rectWidth / 2),
                            input.rows() * (midPos[1] + rectHeight / 2)),
                    new Scalar(0, 255, 0), 3);
            Imgproc.rectangle(//5-7
                    all,
                    new Point(
                            input.cols() * (rightPos[0] - rectWidth / 2),
                            input.rows() * (rightPos[1] - rectHeight / 2)),
                    new Point(
                            input.cols() * (rightPos[0] + rectWidth / 2),
                            input.rows() * (rightPos[1] + rectHeight / 2)),
                    new Scalar(0, 255, 0), 3);

            switch (stageToRenderToViewport) {
                case THRESHOLD: {
                    return thresholdMat;
                }

                case detection: {
                    return all;
                }

                case RAW_IMAGE: {
                    return input;
                }

                default: {
                    return input;
                }
            }
        }

    }


    /////////////////////Controls///////////////////////

/**
    double getBatteryVoltage() {
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0) {
                result = Math.min(result, voltage);
            }
        }
        return result;
    }**/

}
