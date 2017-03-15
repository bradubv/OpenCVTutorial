package org.team1635.vision;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class ImageViewer {
	private JFrame frame;
	private JLabel imageView;
	private JLabel resultView;
	private JButton button;
	
	private Mat image;
	private boolean allowImageLoad;
	private boolean processImage;
	
	private String imgPath = "C:\\Users\\Bogdan\\git\\OpenCVTutorial\\imgs\\";

	public void loadImage(Mat image) {
		this.image = image;
		loadImage();
	}

	private void loadImage() {
		Image loadedImage = toBufferedImage(image);
		imageView.setIcon(new ImageIcon(loadedImage));
		
		if (processImage) {
			PipelineThree pipeline = new PipelineThree();
			pipeline.setAreaOfInterest(22, 37, 32, 66); 
			pipeline.process(image);
			Mat resultImage = pipeline.hsvThresholdOutput();
			Image tmpImage = toBufferedImage(resultImage);
			resultView.setIcon(new ImageIcon(tmpImage));

			Image inputImage = toBufferedImage(pipeline.getEnhancedInput());
			imageView.setIcon(new ImageIcon(inputImage));

			System.out.println("Processing Results: Target Candidate Count = " + pipeline.getTargetCandidateCount());
		}
	}

	public void createJFrame(String windowName) {
		createJFrame(windowName, false, false);
	}

	public void createJFrame(String windowName, boolean allowImageLoad, boolean processImage) {
		this.allowImageLoad = allowImageLoad;
		this.processImage = processImage;
		
		frame = new JFrame(windowName);
		imageView = new JLabel();
		final JScrollPane imageScrollPane = new JScrollPane(imageView);
		imageScrollPane.setPreferredSize(new Dimension(330, 250));
		frame.add(imageScrollPane, BorderLayout.WEST);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		if (allowImageLoad) {
			button = new JButton("Load Image");
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					final JFileChooser fc = new JFileChooser(imgPath);

					int returnVal = fc.showOpenDialog(button);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
			            File file = fc.getSelectedFile();
						FileLoader fileLoader = new FileLoader();
						try {
							image = fileLoader.openFile(file.getAbsolutePath());
							//		.openFile("C:\\dev\\2017VisionExample\\Vision Images\\BogdanRobot\\RobotCamera.jpg");
							// .openFile("C:\\temp\\image_09in_touching.jpg");
							loadImage();
							// show();
						} catch (Exception ex) {
							System.out.println(ex.getMessage());
						}
			        }
					
				}
			});
		}
		
		if (processImage) {
			frame.add(button, BorderLayout.SOUTH);
			resultView = new JLabel();
			final JScrollPane resultScrollPane = new JScrollPane(resultView);
			resultScrollPane.setPreferredSize(new Dimension(330, 250));
			frame.add(resultScrollPane, BorderLayout.EAST);
		}
		
		setSystemLookAndFeel();		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	private void setSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public Image toBufferedImage(Mat matrix) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (matrix.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
		byte[] buffer = new byte[bufferSize];
		matrix.get(0, 0, buffer); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
		return image;
	}
}
