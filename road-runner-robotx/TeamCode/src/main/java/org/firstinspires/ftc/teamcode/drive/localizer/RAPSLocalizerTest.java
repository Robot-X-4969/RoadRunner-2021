package org.firstinspires.ftc.teamcode.drive.localizer;

import android.support.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.acmerobotics.roadrunner.localization.TwoTrackingWheelLocalizer;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Arrays;
import java.util.List;

/*
 * Sample tracking wheel localizer implementation assuming the standard configuration:
 *
 *    /--------------\
 *    |     ____     |
 *    |     gyro     |
 *    |           || |
 *    |           || |
 *    |              |
 *    |              |
 *    \--------------/
 *
 * Note: this could be optimized significantly with REV bulk reads
 */
@Config
public class RAPSLocalizerTest extends TwoTrackingWheelLocalizer {
    public static double TICKS_PER_REV = 1440;
    public static double WHEEL_RADIUS = 0.98425; // in
    public static double GEAR_RATIO = 1; //0.2526475591; // output (wheel) speed / input (encoder) speed

    //12.261

    public static double LATERAL_DISTANCE = 6.8712; // in; distance between the left and right wheels
    public static double FORWARD_OFFSET = 3.7344; // in; offset of the lateral wheel

    private DcMotor xEncoder, yEncoder;
    private BNO055IMU imu;


    public RAPSLocalizerTest(HardwareMap hardwareMap) {
        super(Arrays.asList(
                new Pose2d(0, -LATERAL_DISTANCE / 2, 0), // right
                new Pose2d(FORWARD_OFFSET, 0, Math.toRadians(90)) // front
        ));

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.RADIANS;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "gyroSensor");
        imu.initialize(parameters);

        xEncoder = hardwareMap.dcMotor.get("flywheelRight"); //xAxis
        yEncoder = hardwareMap.dcMotor.get("stoneArm"); //yAxis
    }

    public static double encoderTicksToInches(int ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
                encoderTicksToInches(-xEncoder.getCurrentPosition()),
                encoderTicksToInches(-yEncoder.getCurrentPosition()));
    }

    @Override
    public double getHeading() {

        return imu.getAngularOrientation().firstAngle;
    }
}
