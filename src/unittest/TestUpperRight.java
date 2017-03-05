package unittest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.team1635.vision.Quadrilateral;

public class TestUpperRight {

	Quadrilateral myQuad;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	@Before
	public void setUp() throws Exception {

		Point redPoint1 = new Point(80, 50);
		Point redPoint2 = new Point(100, 70);
		//hidden point should be 50, 50
		Point greenPoint = new Point(50, 70);
		Point bluePoint = new Point(50, 100);
		Point whitePoint = new Point(100, 100);

	    MatOfPoint myPoly = new MatOfPoint();
	    List<Point> myList = new ArrayList<Point>();
	    myList.add(redPoint1);
	    myList.add(redPoint2);	    
	    myList.add(greenPoint);
	    myList.add(bluePoint);
	    myList.add(whitePoint);
	    myPoly.fromList(myList);
	    
	    myQuad = new Quadrilateral();
	    myQuad.fromMatOfPoint(myPoly);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLowerRight() {
		assertEquals(75, myQuad.getCenter().x, 0.001f);
		assertEquals(75, myQuad.getCenter().y, 0.001f);
		assertEquals(51, myQuad.getHeight());
		assertEquals(100, myQuad.getTopRight().x, 0.001f);
		assertEquals(50, myQuad.getTopRight().y, 0.001f);		
	}

}