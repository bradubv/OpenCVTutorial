package org.team1635.vision;

import java.util.ArrayList;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class DebugPipeline extends VisionPipeline {
	//Inputs
	private Rect areaOfInterest;

	//Outputs
	private double[][] ranges;
	private Mat enhancedInput;

	public DebugPipeline() {
		super();
		ranges = new double[3][2];
	}

	@Override
	public void process(Mat inputImage) {
		super.process(inputImage);
		enhanceInput(inputImage);
		// Draw aproxPoly vertices onto
		drawPolyVertices(hsvThresholdOutput, aproxPolysOutput);
	}
	
	private double[][] getRanges() {
		return ranges;
	}

	public void showRanges() {
		System.out.print("Area: x from " + areaOfInterest.tl().x + " to " + areaOfInterest.br().x);
		System.out.println(", y from " + areaOfInterest.tl().y + " to " + areaOfInterest.br().y);
		System.out.println("Hue = " + ranges[0][0] + " to " + ranges[0][1]);
		System.out.println("Sat = " + ranges[1][0] + " to " + ranges[1][1]);
		System.out.println("Val = " + ranges[2][0] + " to " + ranges[2][1]);
		System.out.println("------------");
	}
	
	@Override
	protected void setRanges(Mat img) {
		int startX = (int) areaOfInterest.tl().x;
		int endX = (int) areaOfInterest.br().x;
		int startY = (int) areaOfInterest.tl().y;
		int endY = (int) areaOfInterest.br().y;
		
		setRanges(img, startX, endX, startY, endY);
	}

	private void setRanges(Mat img, int startX, int endX, int startY, int endY) {
		byte[] unsignedPoint = new byte[3];
		int minHue = 180, maxHue = 0;
		int minSat = 255, maxSat = 0;
		int minVal = 255, maxVal = 0;

		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				img.get(y, x, unsignedPoint);
				int hue = unsignedPoint[0] & 0xff;
				int sat = unsignedPoint[1] & 0xff;
				int val = unsignedPoint[2] & 0xff;

				if (hue > maxHue)
					maxHue = hue;
				if (hue < minHue)
					minHue = hue;
				if (sat > maxSat)
					maxSat = sat;
				if (sat < minSat)
					minSat = sat;
				if (val > maxVal)
					maxVal = val;
				if (val < minVal)
					minVal = val;
			}
		}

		double[] hueRange = new double[2];
		hueRange[0] = minHue;
		hueRange[1] = maxHue;
		double[] satRange = new double[2];
		satRange[0] = minSat;
		satRange[1] = maxSat;
		double[] valRange = new double[2];
		valRange[0] = minVal;
		valRange[1] = maxVal;
		ranges[0] = hueRange;
		ranges[1] = satRange;
		ranges[2] = valRange;
	}

	public void showFilter() {
		double[] hueRange = filter[0];
		double[] satRange = filter[1];
		double[] valRange = filter[2];
		System.out.println("Filter: Hue: " + hueRange[0] + " " + hueRange[1]); 
		System.out.println("Filter: Sat: " + satRange[0] + " " + satRange[1]);
		System.out.println("Filter: Val: " + valRange[0] + " " + valRange[1]);
	}
	
	private void showVals(Mat img, int startX, int endX, int startY, int endY) {
		byte[] unsignedPoint = new byte[3];
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				img.get(y, x, unsignedPoint);
				int hue = unsignedPoint[0] & 0xff;
				int sat = unsignedPoint[1] & 0xff;
				int val = unsignedPoint[2] & 0xff;
				System.out.print("x = " + x + "; y = " + y + "; Hue = " + hue);
				System.out.println("; Sat = " + sat + "; Val = " + val);
				System.out.print("x = " + x + "; y = " + y + "; Hue = " + unsignedPoint[0]);
				System.out.println("; Sat = " + unsignedPoint[1] + "; Val = " + unsignedPoint[2]);
			}
		}
		System.out.println("------------");
	}

	private void showOutVals(Mat img, int startX, int endX, int startY, int endY) {
		byte[] unsignedPoint = new byte[1];
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				img.get(y, x, unsignedPoint);
				int val = unsignedPoint[0] & 0xff;
				System.out.println("x = " + x + "; y = " + y + "; Val = " + val);
			}
		}
		System.out.println("------------");
	}

	private String showMatInfo(Mat mat) {
		return "Type of mat is " + CvType.typeToString(mat.type()) + "; size is " + mat.cols() + " x " + mat.rows();
	} // debug

	private void enhanceInput(Mat input) {
		enhancedInput = input.clone();
		Imgproc.rectangle(enhancedInput, areaOfInterest.tl(), areaOfInterest.br(), new Scalar(0, 255, 0), 2); // debug
	}

	public void setAreaOfInterest(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY) {
		Point topLeft = new Point(topLeftX, topLeftY);
		Point bottomRight = new Point(bottomRightX, bottomRightY);
		areaOfInterest = new Rect(topLeft, bottomRight);
	}
	
	public Mat getEnhancedInput() {
		return enhancedInput;
	}
	
	private void drawPolyVertices(Mat img, ArrayList<MatOfPoint2f> polys) {
		for (int i = 0; i < polys.size(); i++) {
			MatOfPoint2f poly = polys.get(i);
			for (int vertCnt = 0; vertCnt < poly.rows(); vertCnt++) {
				Point point = new Point(poly.get(vertCnt, 0));
				Imgproc.circle(img, point, 3, new Scalar(255, 0, 0));
			}
		}

		// Put here anything you want to draw on the output.
		// Draw a line
		// Point startPoint = new Point(showValStartX, showValStartY); //debug
		// Point endPoint = new Point(showValEndX, showValEndY); //debug
		Point startPoint = new Point(158, 200); // debug
		Point endPoint = new Point(162, 230); // debug
		// Imgproc.line(img, startPoint, endPoint, new Scalar(60, 0, 0));
		// //debug
		// Draw a rectangle
		// Imgproc.rectangle(img, areaOfInterest.tl(), areaOfInterest.br(), new
		// Scalar(100, 0, 0), 2); //debug
		Imgproc.rectangle(img, startPoint, endPoint, new Scalar(100, 0, 0), 2); // debug
	}

	// History of hsv values we tried
	//
	// double[] hsvThresholdHue = { 0.0, 178.4641638225256 };
	// double[] hsvThresholdHue = { 19.0, 22.0 };
	// double[] hsvThresholdHue = { 11.0, 15.0 }; //from Row 45 cols from 23
	// to 34: buggy x swap y
	// double[] hsvThresholdHue = { 52.0, 150.0 }; //from Row 45 cols from
	// 23 to 34
	// double[] hsvThresholdHue = { 0.0, 173.0 }; //from aoi(23,39,32,66)
	//
	// double[] hsvThresholdSaturation = { 0.0, 41.774744027303754 };
	// double[] hsvThresholdSaturation = { 118.0, 140.0 };
	// double[] hsvThresholdSaturation = { 131.0, 168.0 }; //from Row 45
	// cols from 23 to 34: buggy x swap y
	// double[] hsvThresholdSaturation = { 3.0, 28.0 }; //from Row 45 cols
	// from 23 to 34
	// double[] hsvThresholdSaturation = { 0.0, 32.0 }; //from
	// aoi(23,39,32,66)
	//
	// double[] hsvThresholdValue = { 210.97122302158272, 255.0 };
	// double[] hsvThresholdValue = { 155.0, 185.0 };
	// double[] hsvThresholdValue = { 128.0, 150.0 }; //from Row 45 cols
	// from 23 to 34
	// double[] hsvThresholdValue = { 245.0, 255.0 }; //from Row 45 cols
	// from 23 to 34
	// double[] hsvThresholdValue = { 249.0, 255.0 }; //from
	// aoi(23,39,32,66)
}
