package test;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import java.util.ArrayList;
import java.util.List;

import org.team1635.vision.*;

public class TestUpperLeft {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("Welcome to OpenCV " + Core.VERSION);

		// Mat mat = Mat.eye(240, 320, CvType.CV_8UC1); //grayscale, but don't
		// know how to draw circles TODO
		Mat img = Mat.zeros(240, 320, CvType.CV_8UC3);
		// img.put(50, 100, new Scalar(0, 0, 255));

		Scalar red = new Scalar(0, 0, 255);
		Scalar green = new Scalar(0, 255, 0);
		Scalar blue = new Scalar(255, 0, 0);
		Scalar white = new Scalar(255, 255, 255);

		Point redPoint = new Point(100, 50);
		// hidden point should be 50, 50
		Point greenPoint1 = new Point(50, 70);
		Point greenPoint2 = new Point(70, 50);
		Point bluePoint = new Point(50, 100);
		Point whitePoint = new Point(100, 100);

		Imgproc.circle(img, redPoint, 3, red);
		Imgproc.circle(img, greenPoint1, 3, green);
		Imgproc.circle(img, greenPoint2, 3, green);
		Imgproc.circle(img, bluePoint, 3, blue);
		Imgproc.circle(img, whitePoint, 3, white);

		// Imgproc.line(img, redPoint, greenPoint, blue);

		MatOfPoint myPoly = new MatOfPoint();
		List<Point> myList = new ArrayList<Point>();
		myList.add(redPoint);
		myList.add(greenPoint1);
		myList.add(greenPoint2);
		myList.add(bluePoint);
		myList.add(whitePoint);
		myPoly.fromList(myList);

		Quadrilateral myQuad = new Quadrilateral();
		myQuad.fromMatOfPoint(myPoly);
		int height = myQuad.getHeight();

		System.out.println("Height = " + height);
		myQuad.printOrientedCorners();

		// Imgproc.fillPoly( img, myList, white);

		ImageViewer imageViewer = new ImageViewer();
		imageViewer.createJFrame("Drawn Image");
		imageViewer.loadImage(img);
	}
}
