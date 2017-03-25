package org.team1635.vision;

import org.opencv.core.Core;

public class HelloOpenCV {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("Welcome to OpenCV " + Core.VERSION);
		try {
			ImageViewer imageViewer = new ImageViewer();
			//imageViewer.setDirectory("C:\\Users\\Bogdan\\git\\OpenCVTutorial\\imgs\\HomeEvening");
//			imageViewer.setDirectory("C:\\Users\\Bogdan\\eclipse\\neon\\OpenCVTutorial\\imgs\\HomeEvening");
			imageViewer.setDirectory("C:\\Users\\Bogdan\\eclipse\\neon\\OpenCVTutorial\\imgs\\rcc_practice");
			imageViewer.createJFrame("View Processing Results", true, true);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Exiting program");
			System.exit(1);
		}
	}
		
}
