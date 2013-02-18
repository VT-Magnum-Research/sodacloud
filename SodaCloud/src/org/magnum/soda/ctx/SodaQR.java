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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import org.magnum.soda.SodaContext;

public class SodaQR implements SodaContext {

	// To store image and context mapping
	static Map<SodaQR, BufferedImage> ctxImages_;

	// Give SodaContext a Name. Can be a Unique identifier.
	private String ctxName_;

	public String getCtxName_() {
		return ctxName_;
	}

	public void setCtxName_(String ctxName_) {
		this.ctxName_ = ctxName_;
	}

	// variable used in overriding hashcode for this object
	private volatile int ctxHashCode_ = 0;

	static {
		ctxImages_ = new HashMap<SodaQR, BufferedImage>();
	}

	/**
	 * Constructors
	 */
	private SodaQR() {

	}

	private SodaQR(String name) {
		setCtxName_(name);
	}

	/**
	 * @param name
	 * @return
	 */
	public static SodaQR create() {

		SodaQR ctxQR = new SodaQR();

		BufferedImage img = null;
		try {
			// Image location can be changed to anything else
			img = ImageIO.read(new File("strawberry.jpg"));

		} catch (IOException e) {
			// create Dummy image for testing
			if (img == null) {
				BufferedImage dummy = new BufferedImage(4, 4,
						BufferedImage.TYPE_INT_RGB);
				Color c = Color.BLUE;

				int color = c.getRGB();
				for (int x = 0; x < dummy.getWidth(); x++) {
					for (int y = 0; y < dummy.getHeight(); y++) {
						dummy.setRGB(x, y, color);
					}
				}
				img = dummy;
			}
		}
		ctxImages_.put(ctxQR, img);
		return ctxQR;
	}

	/**
	 * @param data
	 * @return
	 */
	public static SodaQR fromImageData(byte[] data) {

		try {
			Iterator<SodaQR> itr = ctxImages_.keySet().iterator();

			while (itr.hasNext()) {
				SodaQR temp = itr.next();
				BufferedImage img = ctxImages_.get(temp);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(img, "PNG", baos);
				baos.flush();
				byte[] imageInByte = baos.toByteArray();
				baos.close();
				if (Arrays.equals(imageInByte, data)) {
					return temp;
				}
			}

		} catch (IOException e) {
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

			if (ctxImages_.containsKey(this)) {
				BufferedImage img = ctxImages_.get(this);
				// Create output stream
				final ByteArrayOutputStream out = new ByteArrayOutputStream();

				// Get image writer for format NOTE: We can get metadata from
				// image to exploit is properties like file format..
				final ImageWriter writer = (ImageWriter) ImageIO
						.getImageWritersByFormatName("PNG").next();

				// Write out image
				writer.setOutput(ImageIO.createImageOutputStream(out));
				writer.write(img);

				// Return the image data
				return out.toByteArray();

			}
		} catch (IOException e) {
			throw new RuntimeException();
		}

		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	// @Override
	/*
	 * public boolean equals(Object obj) { if (this == obj) { return true; } if
	 * (!(obj instanceof SodaQR)) { return false; } SodaQR qr = (SodaQR) obj;
	 * return ctxName_.equals(qr.getCtxName_());
	 * 
	 * }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	// @Override
	/*
	 * public int hashCode() { final int multiplier = 23; if (ctxHashCode_ == 0)
	 * { int code = 133; code = multiplier * code +
	 * this.hashCode();//this.ctxName_.hashCode(); ctxHashCode_ = code; } return
	 * ctxHashCode_; }
	 */

}
