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

import org.magnum.soda.SodaContext;

public abstract class SodaQR implements SodaContext {

	protected ImageContainer img_;
	
	public SodaQR()
	{
		img_=new ImageContainer();
	}
		/**
	 * @param name
	 * @return
	 */
	public static SodaQR create() {

		SodaQR qr=null;
		 if(System.getProperty("java.vm.name").equalsIgnoreCase("Dalvik")){
			 qr= SodaQR_android.create();
			   } else {
	           qr= SodaQR_system.create();
	        }
		 return qr;
	}

	/**
	 * @param Data
	 * @return
	 */
	public static SodaQR create(String Data) {

		SodaQR qr=null;
		 if(System.getProperty("java.vm.name").equalsIgnoreCase("Dalvik")){
			 qr= SodaQR_android.create(Data);
	        } else {
	           qr= SodaQR_system.create(Data);
	        }
		 return qr;
	
			}

	/**
	 * @param data
	 * @return
	 */
	public static SodaQR fromImageData(byte[] b) {
		SodaQR qr=null;
		 if(System.getProperty("java.vm.name").equalsIgnoreCase("Dalvik")){
			 qr= SodaQR_android.fromImageData(b);
	        } else {
	           qr= SodaQR_system.fromImageData(b);
	        }
		 return qr;
		}

	public abstract byte[] getImageData();
	
	public abstract ImageContainer getImg_();
	
	public abstract void setImg_(ImageContainer img_);
	
	
}