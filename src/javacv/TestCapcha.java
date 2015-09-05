/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javacv;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.opencv.core.CvType;
import org.opencv.core.Point;
import org.opencv.core.Core;
import org.opencv.core.Mat; 
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;


/**
 *
 * @author dmitry
 */
public class TestCapcha extends JPanel{

	private static final long serialVersionUID = 1L;
	private BufferedImage image;

	// Create a constructor method  
	public TestCapcha() throws IOException {
		super();
	}

	private BufferedImage getimage() {
		return image;
	}

	private void setimage(BufferedImage newimage) {
		image = newimage;
		return;
	}

	/**
	 * Converts/writes a Mat into a BufferedImage.
	 *
	 * @param matrix Mat of type CV_8UC3 or CV_8UC1
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
	 */
	public static BufferedImage matToBufferedImage(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
		byte[] data = new byte[cols * rows * elemSize];
		int type;
		matrix.get(0, 0, data);
		switch (matrix.channels()) {
		case 1:
			type = BufferedImage.TYPE_BYTE_GRAY;
			break;
		case 3:
			type = BufferedImage.TYPE_3BYTE_BGR;
			// bgr to rgb  
			byte b;
			for (int i = 0; i < data.length; i = i + 3) {
				b = data[i];
				data[i] = data[i + 2];
				data[i + 2] = b;
			}
			break;
		default:
			return null;
		}
		BufferedImage image2 = new BufferedImage(cols, rows, type);
		image2.getRaster().setDataElements(0, 0, cols, rows, data);
		return image2;
	}

	public void paintComponent(Graphics g) {
		BufferedImage temp = getimage();
		g.drawImage(temp, 10, 10, temp.getWidth(), temp.getHeight(), this);
	}    


	// Convert image to Mat
	public static Mat matify(BufferedImage im) {
		// Convert INT to BYTE
		//im = new BufferedImage(im.getWidth(), im.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		// Convert bufferedimage to byte array
		byte[] pixels = ((DataBufferByte) im.getRaster().getDataBuffer()).getData();

		// Create a Matrix the same size of image
		Mat image = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8UC3);
		// Fill Matrix with image values
		image.put(0, 0, pixels);

		return image;

	}


	/**
	 * @param args the command line arguments
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO code application logic here

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		CascadeClassifier faceDetector = new CascadeClassifier("./data/haarcascade_capcha0.xml");
		//CascadeClassifier faceDetector = new CascadeClassifier();

		File file = new File("data/capcha/capcha-visual.jpg");
		BufferedImage tempImg = ImageIO.read(file);
		//BufferedImage tempImg = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);


		JFrame frame = new JFrame("BasicPanel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);

		TestCapcha panel = new TestCapcha();

		frame.setContentPane(panel);
		frame.setVisible(true);

		panel.setimage(tempImg);
		panel.repaint();

		Mat webcam_image = new Mat();
		BufferedImage temp;

		//Graphics2D g = newImage.createGraphics();
		//g.drawImage(in, 0, 0, null);

		for (int i = 2;i<21;i++){

			webcam_image = matify(tempImg);

			frame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);

			MatOfRect faceDetections = new MatOfRect();
			faceDetector.detectMultiScale(webcam_image, faceDetections);

			System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

			// Draw a bounding box around each face.
			for (Rect rect : faceDetections.toArray()) {
				Core.rectangle(webcam_image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
			}


			temp = matToBufferedImage(webcam_image);
			panel.setimage(temp);
			panel.repaint();

			Thread.sleep(1000);
						
			file = new File(String.format("data/capcha/capcha-visual%d.jpg", i));
			System.out.println(String.format("load from file: %s", file.getPath()));
			tempImg = ImageIO.read(file);
			

		}
	}

}
