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

public class TestLowerLeft {

	Quadrilateral myQuad;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	@Before
	public void setUp() throws Exception {

		Point redPoint = new Point(100, 50);
		Point greenPoint = new Point(50,50);
		//hidden point should be 50, 100
		Point bluePoint1 = new Point(50, 80);
		Point bluePoint2 = new Point(70, 100);
		Point whitePoint = new Point(100, 100);

	    MatOfPoint myPoly = new MatOfPoint();
	    List<Point> myList = new ArrayList<Point>();
	    myList.add(redPoint);
	    myList.add(greenPoint);
	    myList.add(bluePoint1);
	    myList.add(bluePoint2);
	    myList.add(whitePoint);
	    myPoly.fromList(myList);
	    
	    myQuad = new Quadrilateral();
	    myQuad.fromMatOfPoint(myPoly);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLowerLeft() {
		assertEquals(75, myQuad.getCenter().x, 0.001f);
		assertEquals(75, myQuad.getCenter().y, 0.001f);
		assertEquals(51, myQuad.getHeight());
		assertEquals(50, myQuad.getBottomLeft().x, 0.001f);
		assertEquals(100, myQuad.getBottomLeft().y, 0.001f);		
	}

}