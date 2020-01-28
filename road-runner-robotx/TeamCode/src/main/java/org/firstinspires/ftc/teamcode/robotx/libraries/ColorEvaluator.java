package org.firstinspires.ftc.teamcode.robotx.libraries;

import com.qualcomm.robotcore.hardware.ColorSensor;

import java.util.ArrayList;

import org.firstinspires.ftc.teamcode.robotx.modules.*;
import org.firstinspires.ftc.teamcode.robotx.libraries.*;
/**
 * Created by Nicholas on 1/8/2016.
 */
public class ColorEvaluator {

	public enum Material {
		GREY_FLOOR,
		RED_TAPE,
		BLUE_TAPE,
		WHITE_TAPE,
		OTHER;

		public String toString() {
			String output = "";
			if (this==GREY_FLOOR) {
				output = "GREY_FLOOR";
			} else if (this==RED_TAPE) {
				output = "RED_TAPE";
			} else if (this==BLUE_TAPE) {
				output = "BLUE_TAPE";
			} else if (this==WHITE_TAPE) {
				output = "WHITE_TAPE";
			} else if (this==OTHER) {
				output = "OTHER";
			}

			return output;
		}
	}
	enum LightLevel {
		LOW,
		MED,
		HIGH
	}

	ArrayList<Integer> dataList;

	public double mean = 0;
	public double stdDev = 1;

	private void calcMean() {
		double sum = 0;
		for (Object data : dataList.toArray()) {
			sum += ((Integer)data);
		}
		double mean = sum / (double)dataList.size();

		this.mean = mean;
	}


	private void calcStdDev() {
		double sum = 0;
		for (Object data : dataList.toArray()) {
			int x = ((Integer)data);
			sum += Math.pow((x - mean), 2);
		}
		double variance = sum / ((double)dataList.size()-1);
		double stdDev = Math.sqrt(variance);

		this.stdDev = stdDev;
	}

	public ColorEvaluator() {
		dataList = new ArrayList<Integer>();
	}

	public void addData(int data) {
		dataList.add(data);
		calcMean();
		calcStdDev();
	}
	public void addData(ColorSensor sensor) {
		addData(sensor.red());
		addData(sensor.green());
		addData(sensor.blue());
	}

	public double zScore(int data) {
		double zScore = (data - mean)/stdDev;

		return zScore;
	}

	public Material evaluateColor(int redValue, int greenValue, int blueValue) {
		LightLevel red = getLightLevel(redValue);
		LightLevel green = getLightLevel(greenValue);
		LightLevel blue = getLightLevel(blueValue);

		Material output = Material.OTHER;
		if (red== LightLevel.LOW &&green== LightLevel.LOW &&blue== LightLevel.LOW ) {
			output = Material.GREY_FLOOR;
		} else if (red== LightLevel.LOW &&green== LightLevel.LOW &&blue== LightLevel.MED ) {
			output = Material.BLUE_TAPE;
		} else if (red== LightLevel.LOW &&green== LightLevel.LOW &&blue== LightLevel.HIGH) {
			output = Material.BLUE_TAPE;
		} else if (red== LightLevel.LOW &&green== LightLevel.MED &&blue== LightLevel.LOW ) {
			output = Material.GREY_FLOOR;
		} else if (red== LightLevel.LOW &&green== LightLevel.MED &&blue== LightLevel.MED ) {
			output = Material.BLUE_TAPE;
		} else if (red== LightLevel.LOW &&green== LightLevel.MED &&blue== LightLevel.HIGH) {
			output = Material.BLUE_TAPE;
		} else if (red== LightLevel.LOW &&green== LightLevel.HIGH&&blue== LightLevel.LOW ) {
			output = Material.OTHER;
		} else if (red== LightLevel.LOW &&green== LightLevel.HIGH&&blue== LightLevel.MED ) {
			output = Material.OTHER;
		} else if (red== LightLevel.LOW &&green== LightLevel.HIGH&&blue== LightLevel.HIGH) {
			output = Material.BLUE_TAPE;
		} else if (red== LightLevel.MED &&green== LightLevel.LOW &&blue== LightLevel.LOW ) {
			output = Material.RED_TAPE;
		} else if (red== LightLevel.MED &&green== LightLevel.LOW &&blue== LightLevel.MED ) {
			output = Material.OTHER;
		} else if (red== LightLevel.MED &&green== LightLevel.LOW &&blue== LightLevel.HIGH) {
			output = Material.OTHER;
		} else if (red== LightLevel.MED &&green== LightLevel.MED &&blue== LightLevel.LOW ) {
			output = Material.RED_TAPE;
		} else if (red== LightLevel.MED &&green== LightLevel.MED &&blue== LightLevel.MED ) {
			output = Material.GREY_FLOOR;
		} else if (red== LightLevel.MED &&green== LightLevel.MED &&blue== LightLevel.HIGH) {
			output = Material.BLUE_TAPE;
		} else if (red== LightLevel.MED &&green== LightLevel.HIGH&&blue== LightLevel.LOW ) {
			output = Material.OTHER;
		} else if (red== LightLevel.MED &&green== LightLevel.HIGH&&blue== LightLevel.MED ) {
			output = Material.WHITE_TAPE;
		} else if (red== LightLevel.MED &&green== LightLevel.HIGH&&blue== LightLevel.HIGH) {
			output = Material.WHITE_TAPE;
		} else if (red== LightLevel.HIGH&&green== LightLevel.LOW &&blue== LightLevel.LOW ) {
			output = Material.RED_TAPE;
		} else if (red== LightLevel.HIGH&&green== LightLevel.LOW &&blue== LightLevel.MED ) {
			output = Material.OTHER;
		} else if (red== LightLevel.HIGH&&green== LightLevel.LOW &&blue== LightLevel.HIGH) {
			output = Material.OTHER;
		} else if (red== LightLevel.HIGH&&green== LightLevel.MED &&blue== LightLevel.LOW ) {
			output = Material.RED_TAPE;
		} else if (red== LightLevel.HIGH&&green== LightLevel.MED &&blue== LightLevel.MED ) {
			output = Material.RED_TAPE;
		} else if (red== LightLevel.HIGH&&green== LightLevel.MED &&blue== LightLevel.HIGH) {
			output = Material.WHITE_TAPE;
		} else if (red== LightLevel.HIGH&&green== LightLevel.HIGH&&blue== LightLevel.LOW ) {
			output = Material.RED_TAPE;
		} else if (red== LightLevel.HIGH&&green== LightLevel.HIGH&&blue== LightLevel.MED ) {
			output = Material.WHITE_TAPE;
		} else if (red== LightLevel.HIGH&&green== LightLevel.HIGH&&blue== LightLevel.HIGH) {
			output = Material.WHITE_TAPE;
		}

		return output;
	}
	public Material evaluateColor(ColorSensor sensor) {
		return evaluateColor(sensor.red(), sensor.green(), sensor.blue());
	}

	private LightLevel getLightLevel(int color) { // TODO These values work well.  Check if there are better ones.
		double zScore = zScore(color);
		LightLevel output = LightLevel.LOW;

		if (zScore < 3) {
			output = LightLevel.LOW;
		} else if (3 < zScore && zScore < 6) {
			output = LightLevel.MED;
		} else if (6 < zScore) {
			output = LightLevel.HIGH;
		}

		return output;
	}

}

