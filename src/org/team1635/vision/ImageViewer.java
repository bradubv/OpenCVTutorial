package org.team1635.vision;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
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
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageViewer {
	private JFrame frame;
	private JLabel imageView;
	private JLabel resultView;
	private JButton loadButton;
	private JButton prevButton;
	private JButton nextButton;
	
	private Mat image;
	private boolean allowImageLoad;
	private boolean processImage;

	private JTextField targetCount;
	private JCheckBox targetAcquired;
	private JTextField distance;
	private JTextField error;
	
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
			//VisionPipeline pipeline = new VisionPipeline();
			DebugPipeline pipeline = new DebugPipeline();
			pipeline.setAreaOfInterest(23, 39, 32, 65);
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
			targetAcquired.setSelected(pipeline.getTargetAcquired());
			distance.setText(Integer.toString(pipeline.getDistance()));
			error.setText(Integer.toString(pipeline.getError()));
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
			loadButton = new JButton("Load Image");
			loadButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					final JFileChooser fc = new JFileChooser(imgPath);

					int returnVal = fc.showOpenDialog(loadButton);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						image = loadMatFromFile(file.getAbsolutePath());
						loadImage();
					}

				}
			});
			buttonPane.add(loadButton, BorderLayout.NORTH);

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
			buttonPane.add(prevButton, BorderLayout.WEST);
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
			
			JPanel valuesPane = new JPanel();
			valuesPane.setLayout(new GridLayout(0,4));

			JLabel targetCountLabel = new JLabel("Candidate Poly Count: ");
			targetCountLabel.setHorizontalAlignment(SwingConstants.TRAILING);
			valuesPane.add(targetCountLabel);
			targetCount = new JTextField();
			targetCount.setText("N/A");
			targetCount.setEditable(false);
			valuesPane.add(targetCount);

			JLabel targetAcquiredLabel = new JLabel("Target Acquired: ");
			targetAcquiredLabel.setHorizontalAlignment(SwingConstants.TRAILING);
			valuesPane.add(targetAcquiredLabel);
			targetAcquired = new JCheckBox();
			targetAcquired.setSelected(false);
			targetAcquired.setEnabled(false);
			valuesPane.add(targetAcquired);

			JLabel distanceLabel = new JLabel("Distance: ");
			distanceLabel.setHorizontalAlignment(SwingConstants.TRAILING);
			valuesPane.add(distanceLabel);
			distance = new JTextField();
			distance.setText("N/A");
			distance.setEditable(false);
			valuesPane.add(distance);

			JLabel errorLabel = new JLabel("Distance from Center: ");
			errorLabel.setHorizontalAlignment(SwingConstants.TRAILING);
			valuesPane.add(errorLabel);
			error = new JTextField();
			error.setText("N/A");
			error.setEditable(false);
			valuesPane.add(error);

			buttonPane.add(valuesPane, BorderLayout.SOUTH);
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
