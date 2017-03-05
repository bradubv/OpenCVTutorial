package org.team1635.vision;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class Quadrilateral {

	private MatOfPoint poly; // The polygon given to us.
	private Point vertices[]; // The quad that approximates
	private Point topLeft;
	private Point topRight;
	private Point bottomRight;
	private Point bottomLeft;

	private Rect boundingRect;

	public enum PolyDirection {
		TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT
	}

	/**
	 * After calling the constructor you must
	 *  call either fromMatOfPoint or fromMatOfPoint2f
	 */
	public Quadrilateral() {
		vertices = new Point[4];
	}
	
	public void fromMatOfPoint2f(MatOfPoint2f quadCandidate2f) {
		MatOfPoint quadCandidate = new MatOfPoint();
		quadCandidate2f.convertTo(quadCandidate, CvType.CV_32S);
		fromMatOfPoint(quadCandidate);
	}
	
	public void fromMatOfPoint(MatOfPoint quadCandidate) {

//		if (quadCandidate.rows() > 4) {
			poly = healPoly(quadCandidate);
//		} else {
//			poly = quadCandidate;
//		}

		for (int vertCnt = 0; vertCnt < poly.rows(); vertCnt++) {
			Point point = new Point(poly.get(vertCnt, 0));
			vertices[vertCnt] = point;
		}
		
		this.boundingRect = Imgproc.boundingRect(this.poly);
	}

	public int getHeight() {
		return boundingRect.height;
	}

	public MatOfPoint healPoly(MatOfPoint polyIn) {
		List<Point> poly = new ArrayList<Point>();
		MatOfPoint polyOut = new MatOfPoint();

		List<Point> topLeft = new ArrayList<Point>();
		List<Point> topRight = new ArrayList<Point>();
		List<Point> bottomRight = new ArrayList<Point>();
		List<Point> bottomLeft = new ArrayList<Point>();

		Rect boundingRect = Imgproc.boundingRect(polyIn);
		Point center = new Point(boundingRect.x + boundingRect.width / 2, boundingRect.y + boundingRect.height / 2);

		for (int vertIdx = 0; vertIdx < polyIn.rows(); vertIdx++) {
			Point currVertex = new Point(polyIn.get(vertIdx, 0));

			if (currVertex.x > center.x) { // this point is on the right
				if (currVertex.y > center.y) { // this point is on the bottom
					bottomRight.add(currVertex);
				} else {                       // this point is on the top
					topRight.add(currVertex);
				}
			} else { // this point is on the left
				if (currVertex.y > center.y) { // this point is on bottom
					bottomLeft.add(currVertex);
				} else {                       // this point is on top
					topLeft.add(currVertex);
				}
			}
		}
		
		if (topLeft.size() == 1) {
			poly.add(topLeft.get(0));
			this.topLeft = topLeft.get(0);
		} else {
			Point hiddenPoint = findHiddenPoint(topLeft, PolyDirection.TOP_LEFT);
			poly.add(hiddenPoint);
			this.topLeft = hiddenPoint;
		}
		if (topRight.size() == 1) {
			poly.add(topRight.get(0));
			this.topRight = topRight.get(0);
		} else {
			Point hiddenPoint = findHiddenPoint(topRight, PolyDirection.TOP_RIGHT);
			poly.add(hiddenPoint);
			this.topRight = hiddenPoint;
		}
		if (bottomRight.size() == 1) {
			poly.add(bottomRight.get(0));
			this.bottomRight = bottomRight.get(0);
		} else {
			Point hiddenPoint = findHiddenPoint(bottomRight, PolyDirection.BOTTOM_RIGHT);
			poly.add(hiddenPoint);
			this.bottomRight = hiddenPoint;
		}
		if (bottomLeft.size() == 1) {
			poly.add(bottomLeft.get(0));
			this.bottomLeft = bottomLeft.get(0);
		} else {
			Point hiddenPoint = findHiddenPoint(bottomLeft, PolyDirection.BOTTOM_LEFT);
			poly.add(hiddenPoint);
			this.bottomLeft = hiddenPoint;
		}

		polyOut.fromList(poly);

		return polyOut;
	}

	/**
	 *   0 2   6 8  <= x
	 * 0 . . | . .
	 *       |       y of the smaller y, x of the larger y
	 * 2 .   |   .
	 * ------+-----
	 * 4 .   |   .
	 *       |       y of the bigger y, x of the smaller y
	 * 6 . . | . .
	 *
	 * ^
	 * y
	 * 
	 * @param points
	 * @param dir
	 * @return
	 */
	private Point findHiddenPoint(List<Point> points, PolyDirection dir) {
		double x, y;

		// if you are in the top left corner or the top right corner you
		// want the x of the low point and the y of the high point.
		if ((dir == PolyDirection.TOP_LEFT) || 
				(dir == PolyDirection.TOP_RIGHT)) {
			if (points.get(0).y > points.get(1).y) { // 0 has larger y
				x = points.get(0).x;
				y = points.get(1).y;
			} else {                                 // 1 has larger y
				x = points.get(1).x;
				y = points.get(0).y;
			}
		} else {
			if (points.get(0).y > points.get(1).y) { // 0 has larger y
				x = points.get(1).x;
				y = points.get(0).y;
			} else {                                 // 1 has larger y
				x = points.get(0).x;
				y = points.get(1).y;
			}			
		}

		return new Point(x, y);
	}
	
	public void printPoly() {
		for (int vertCnt = 0; vertCnt < poly.rows(); vertCnt++) {
			Point point = new Point(poly.get(vertCnt, 0));
			System.out.println("Debug: Point : x = " + point.x + ", y = " + point.y);
		}
	}
	
	public void printOrientedCorners() {
		System.out.println("Debug: topLeft : x = " + getTopLeft().x + ", y = " + getTopLeft().y);
		System.out.println("Debug: topRight : x = " + getTopRight().x + ", y = " + getTopRight().y);
		System.out.println("Debug: bottomRight : x = " + getBottomRight().x + ", y = " + getBottomRight().y);
		System.out.println("Debug: bottomLeft : x = " + getBottomLeft().x + ", y = " + getBottomLeft().y);		
	}
	
	public Point getTopLeft() {
		return topLeft;
	}

	public Point getTopRight() {
		return topRight;
	}
	
	public Point getBottomRight() {
		return bottomRight;
	}
	
	public Point getBottomLeft() {
		return bottomLeft;
	}
	
	public Point getCenter() {
		return new Point(boundingRect.x + boundingRect.width / 2, boundingRect.y + boundingRect.height / 2);
	}
}
