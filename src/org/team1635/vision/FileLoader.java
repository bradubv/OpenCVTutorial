package org.team1635.vision;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.imgcodecs.*;

public class FileLoader {

	public Mat openFile(String fileName) throws Exception {
		Mat newImage = Imgcodecs.imread(fileName); 
		if (newImage.dataAddr() == 0) {
			throw new Exception("Couldn't open file " + fileName);
		}
		return newImage;
	}

}
