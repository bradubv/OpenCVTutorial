import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import java.util.ArrayList;

public class HelloOpenCV {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("Welcome to OpenCV " + Core.VERSION);

		FileProcessor processor = new FileProcessor();
		try {
			Mat fileImage = processor
					.openFile("C:\\dev\\2017VisionExample\\Vision Images\\BogdanRobot\\RobotCamera.jpg");

			// Hello Open CV
			// Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
			// System.out.println("mat = " + mat.dump());

			ImageViewer imageViewer = new ImageViewer();
			imageViewer.show(fileImage, "Loaded image");

			PipelineThree pipeline = new PipelineThree();
			pipeline.process(fileImage);

			ImageViewer resultViewer = new ImageViewer();
			//resultImage = pipeline.hsvThresholdOutput().clone();

			Mat resultImage = pipeline.hsvThresholdOutput();
			ArrayList<MatOfPoint2f> aproxConts = new ArrayList<MatOfPoint2f>();

			resultViewer.show(resultImage, "Processed image");
			
			System.out.println( "Distance = " + pipeline.getDistance());

		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Exiting program");
			System.exit(1);
		}
	}
		
}
