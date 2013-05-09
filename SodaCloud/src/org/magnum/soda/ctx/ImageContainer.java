package org.magnum.soda.ctx;

import java.awt.image.BufferedImage;



public class ImageContainer {

	private  BufferedImage qrBufCodeImage_;	
	private  Bitmap qrBitCodeImage_;
	
	public ImageContainer()
	{
		qrBufCodeImage_=null;
		qrBitCodeImage_=null;
	}
	public  BufferedImage getQrBufCodeImage_() {
		return qrBufCodeImage_;
	}
	public void setQrBufCodeImage_(BufferedImage qrBufCodeImage_) {
		this.qrBufCodeImage_ = qrBufCodeImage_;
	}
	public Bitmap getQrBitCodeImage_() {
		return qrBitCodeImage_;
	}
	public void setQrBitCodeImage_(Bitmap qrBitCodeImage_) {
		this.qrBitCodeImage_ = qrBitCodeImage_;
	}

}
