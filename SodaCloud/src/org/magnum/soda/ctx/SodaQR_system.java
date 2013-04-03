/*****************************************************************************
 * Copyright [2013] [Jules White]                                            *
 *                                                                           *
 *  Licensed under the Apache License, Version 2.0 (the "License");          *
 *  you may not use this file except in compliance with the License.         *
 *  You may obtain a copy of the License at                                  *
 *                                                                           *
 *      http://www.apache.org/licenses/LICENSE-2.0                           *
 *                                                                           *
 *  Unless required by applicable law or agreed to in writing, software      *
 *  distributed under the License is distributed on an "AS IS" BASIS,        *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 *  See the License for the specific language governing permissions and      *
 *  limitations under the License.                                           *
 ****************************************************************************/
package org.magnum.soda.ctx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import org.magnum.soda.SodaContext;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

public class SodaQR_system extends SodaQR implements SodaContext {

	private static final int qrSize = 125;

	private BufferedImage qrCodeImage_;
	
	

	// variable used in overriding hashcode for this object
	private volatile int ctxHashCode_ = 0;

	/**
	 * Constructors
	 */
	private SodaQR_system() {
		this.qrCodeImage_=new BufferedImage(qrSize, qrSize,
				BufferedImage.TYPE_INT_RGB);
	}
	

	/**
	 * @param name
	 * @return
	 */
	public static SodaQR_system create() {

		SodaQR_system ctxQR = new SodaQR_system();

		try {
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(
					2);
			hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode("TestData",
					BarcodeFormat.QR_CODE, qrSize, qrSize, hints);
			// Make the BufferedImage that are to hold the QRCode
			BufferedImage image = populateBufImg(byteMatrix, ctxQR.qrCodeImage_);

			// To check the generated qrcode in file
			
			/* File qrFile = new File("test.png"); ImageIO.write(image, "png",
			  qrFile);*/
			
			ctxQR.qrCodeImage_ = image;

		} catch (Exception e) {
			throw new RuntimeException();
		}
		return ctxQR;
	}

	/**
	 * @param Data
	 * @return
	 */
	public static SodaQR_system create(String Data) {

		SodaQR_system ctxQR = new SodaQR_system();

		try {
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(
					2);
			hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(Data,
					BarcodeFormat.QR_CODE, qrSize, qrSize, hints);
			// Make the BufferedImage that are to hold the QRCode
			BufferedImage image = populateBufImg(byteMatrix, ctxQR.qrCodeImage_);

			// To check the generated qrcode in file
			/*
			 * File qrFile = new File("test.png"); ImageIO.write(image, "png",
			 * qrFile);
			 */
			ctxQR.qrCodeImage_ = image;

		} catch (WriterException e) {
			throw new RuntimeException();
		}
		return ctxQR;
	}

	/**
	 * @param data
	 * @return
	 */
	public static SodaQR_system fromImageData(byte[] b) {


		SodaQR_system newQR = new SodaQR_system();
		try {

			String data;
			data = new String(b, "ISO-8859-1");
			// get a byte matrix for the data
			BitMatrix matrix = null;

			com.google.zxing.Writer writer = new MultiFormatWriter();
			try {
				Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(
						2);
				hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
				matrix = writer.encode(data,
						com.google.zxing.BarcodeFormat.QR_CODE, qrSize, qrSize,
						hints);
			} catch (com.google.zxing.WriterException e) {
			}

			BufferedImage image = populateBufImg(matrix, newQR.qrCodeImage_);

			if (image != null) {
				newQR.qrCodeImage_ = image;
				return newQR;
			}

		} catch (Exception e) {
			if (true) {
				throw new RuntimeException();
			}
		}

		return null;
	}

	/**
	 * @return
	 */
	public byte[] getImageData() {
		try {

			BufferedImage originalImage = this.qrCodeImage_;
			
			Result result=getDecodedResult(originalImage);
			
			return result.getText().getBytes();

		} catch (Exception e) {
			throw new RuntimeException();
		}

	}
	
	private Result getDecodedResult(BufferedImage originalImage)
	{
		LuminanceSource source = new BufferedImageLuminanceSource(
				originalImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		// decode the barcode
		QRCodeReader reader = new QRCodeReader();

		Result result = null;
		try {
			result = reader.decode(bitmap);
		} catch (ReaderException e) {
		}
		return result;

	}

	private static BufferedImage populateBufImg(BitMatrix byteMatrix,
			BufferedImage image	) {
		
		image.createGraphics();

		int matrixWidth = byteMatrix.getWidth();
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);
		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}

		return image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SodaQR_system)) {
			return false;
		}
		SodaQR_system qr = (SodaQR_system) obj;
		

		int width;
		int height;
		boolean imagesEqual = true;

		if (this.qrCodeImage_.getWidth() == (width = qr.qrCodeImage_.getWidth())
				&& this.qrCodeImage_.getHeight() == (height = qr.qrCodeImage_
						.getHeight())) {

			for (int x = 0; imagesEqual == true && x < width; x++) {
				for (int y = 0; imagesEqual == true && y < height; y++) {
					if (this.qrCodeImage_.getRGB(x, y) != qr.qrCodeImage_
							.getRGB(x, y)) {
						imagesEqual = false;
					}
				}
			}
		} else {
			imagesEqual = false;
		}
		return imagesEqual;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int multiplier = 23;
		if (ctxHashCode_ == 0) {
			int code = 133;
			code = multiplier * code 
					+ this.getDecodedResult(this.qrCodeImage_).getText().hashCode();
			ctxHashCode_ = code;
		}
		return ctxHashCode_;
	}


	@Override
	public ImageContainer getImg_() {
		// TODO Auto-generated method stub
		super.img_.setQrBufCodeImage_(this.qrCodeImage_);
		return super.img_;
	
	}


	@Override
	public void setImg_(ImageContainer img_) {
		// TODO Auto-generated method stub
		
	}

}