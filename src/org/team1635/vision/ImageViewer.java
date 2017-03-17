package org.team1635.vision;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageViewer {
	private JFrame frame;
	private JLabel imageView;
	private JLabel resultView;
	private JButton button;
	private JButton prevButton;
	private JButton nextButton;
	private JTextPane targetCount;
	
	private Mat image;
	private boolean allowImageLoad;
	private boolean processImage;

	private String imgPath;
	private List<File> imageFiles;
	private int currentImageIdx;

	public ImageViewer() {
		imageFiles = new ArrayList<File>();
		currentImageIdx = -1;
	}

	public Mat loadMatFromFile(String fileName) {
		Mat newImage = Imgcodecs.imread(fileName);
		if (newImage.dataAddr() == 0) {
			frame.setTitle("Couldn't open file " + fileName);
		}
		return newImage;
	}

	public void incImageIdx() {
		currentImageIdx++;
		if (currentImageIdx == imageFiles.size() - 1) {
			nextButton.setEnabled(false); // setEnabled doesn't seem to work
		} else {
			nextButton.setEnabled(true); // setEnabled doesn't seem to work
			if (currentImageIdx > 0) {
				prevButton.setEnabled(true);
			}
		}
	}

	public void decImageIdx() {
		currentImageIdx--;
		if (currentImageIdx == 0) {
			prevButton.setEnabled(false); // setEnabled doesn't seem to work
		} else {
			prevButton.setEnabled(true); // setEnabled doesn't seem to work
			if (currentImageIdx < imageFiles.size() - 1) {
				nextButton.setEnabled(true);
			}
		}
	}

	public void loadImage(Mat image) {
		this.image = image;
		loadImage();
	}

	public void setDirectory(String path) {
		this.imgPath = path;
		loadFiles();
	}

	private void loadFiles() {
		File folder = new File(this.imgPath);
		File[] listOfFiles = folder.listFiles();
		imageFiles.clear();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				imageFiles.add(listOfFiles[i]);
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName()
						+ " skipped");
			}
		}
	}

	private void loadImage() {
		Image loadedImage = toBufferedImage(image);
		imageView.setIcon(new ImageIcon(loadedImage));

		if (processImage) {
			PipelineThree pipeline = new PipelineThree();
			// pipeline.setAreaOfInterest(22, 37, 32, 66);
			pipeline.setAreaOfInterest(23, 39, 32, 65);
			// from aoi(23,39,32,66)
			double[] hueRange = { 0.0, 173.0 };
			double[] satRange = { 0.0, 32.0 };
			double[] valRange = { 249.0, 255.0 };

			pipeline.setFilter(hueRange, satRange, valRange);

			pipeline.process(image);
			Mat resultImage = pipeline.hsvThresholdOutput();
			Image tmpImage = toBufferedImage(resultImage);
			resultView.setIcon(new ImageIcon(tmpImage));

			Image inputImage = toBufferedImage(pipeline.getEnhancedInput());
			imageView.setIcon(new ImageIcon(inputImage));

			targetCount.setText(Integer.toString(pipeline.getTargetCandidateCount()));
		}
	}

	public void createJFrame(String windowName) {
		createJFrame(windowName, false, false);
	}

	public void createJFrame(String windowName, boolean allowImageLoad,
			boolean processImage) {
		this.allowImageLoad = allowImageLoad;
		this.processImage = processImage;

		frame = new JFrame(windowName);
		imageView = new JLabel();
		final JScrollPane imageScrollPane = new JScrollPane(imageView);
		imageScrollPane.setPreferredSize(new Dimension(330, 250));
		frame.add(imageScrollPane, BorderLayout.WEST);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		if (allowImageLoad) {
			resultView = new JLabel();
			final JScrollPane resultScrollPane = new JScrollPane(resultView);
			resultScrollPane.setPreferredSize(new Dimension(330, 250));
			frame.add(resultScrollPane, BorderLayout.EAST);

			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new BorderLayout());
			button = new JButton("Load Image");
			button.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					final JFileChooser fc = new JFileChooser(imgPath);

					int returnVal = fc.showOpenDialog(button);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						image = loadMatFromFile(file.getAbsolutePath());
						loadImage();
					}

				}
			});
			buttonPane.add(button, BorderLayout.NORTH);

			prevButton = new JButton("Previous");
			prevButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					decImageIdx();
					File file = imageFiles.get(currentImageIdx);
					image = loadMatFromFile(file.getAbsolutePath());
					loadImage();
					frame.setTitle(file.getName());
				}
			});
			buttonPane.add(prevButton, BorderLayout.CENTER);
			prevButton.setEnabled(false);

			nextButton = new JButton("Next");
			nextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					incImageIdx();
					File file = imageFiles.get(currentImageIdx);
					image = loadMatFromFile(file.getAbsolutePath());
					loadImage();
					frame.setTitle(file.getName());
				}
			});
			buttonPane.add(nextButton, BorderLayout.EAST);
			
			targetCount = new JTextPane();
			targetCount.setText("N/A");
			targetCount.setEditable(false);
			//targetCount.setEnabled(false); //makes it barely readable
			buttonPane.add(targetCount, BorderLayout.SOUTH);

			frame.add(buttonPane, BorderLayout.SOUTH);
		}

		if (processImage) {
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
		BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(),
				type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster()
				.getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
		return image;
	}
}
