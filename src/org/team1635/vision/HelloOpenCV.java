package org.team1635.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import java.util.ArrayList;

public class HelloOpenCV {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("Welcome to OpenCV " + Core.VERSION);

		FileLoader fileLoader = new FileLoader();
		try {
//			Mat fileImage = fileLoader
//					.openFile("C:\\dev\\2017VisionExample\\Vision Images\\BogdanRobot\\RobotCamera.jpg");
//					.openFile("C:\\temp\\image_09in_touching.jpg");

			ImageViewer imageViewer = new ImageViewer();
			imageViewer.createJFrame("View Processing Results", true, true);
//			imageViewer.loadImage(fileImage);
			
			//System.out.println( "Distance = " + pipeline.getDistance());

		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Exiting program");
			System.exit(1);
		}
	}
		
}
