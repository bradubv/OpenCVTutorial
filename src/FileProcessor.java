import org.opencv.core.Mat;
import org.opencv.imgcodecs.*;

public class FileProcessor {

	public Mat openFile(String fileName) throws Exception {
		Mat newImage = Imgcodecs.imread(fileName);
		if (newImage.dataAddr() == 0) {
			throw new Exception("Couldn't open file " + fileName);
		}
		return newImage;
	}

}
