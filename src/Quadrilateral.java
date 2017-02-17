import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class Quadrilateral {

	private Point vertices[];
//	private int topLeft;
//	private int topRight;
//	private int bottomRight;
//	private int bottomLeft;
	
	private Rect boundingRect;

	public Quadrilateral(MatOfPoint2f poly) {
		MatOfPoint poly2 = new MatOfPoint();
		poly.convertTo(poly2, CvType.CV_32S);
		this.boundingRect = Imgproc.boundingRect(poly2);
		
		vertices = new Point[4];
		
		for (int vertCnt = 0; vertCnt < poly.rows(); vertCnt++) {
			Point point = new Point(poly.get(vertCnt, 0));
			vertices[vertCnt] = point;
		}
	}
	
	public int getHeight() {
		System.out.println("boundingRect.height = " + boundingRect.height);
		return boundingRect.height;
	}
}
