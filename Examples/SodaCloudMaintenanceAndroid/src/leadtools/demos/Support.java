package leadtools.demos;

import java.io.InputStream;

import leadtools.RasterKernelType;
import leadtools.RasterSupport;

import android.content.Context;
import android.util.Log;

public class Support {
   public static boolean isKernelExpired() {
      return RasterSupport.getKernelExpired();
   }

   public static void setLicense(Context context) {
      // add your licence file to the raw folder
      int licenseFileId = 0;//R.raw.YourLicense;
      String developerKey = "";
      try {
         InputStream stream = context.getResources().openRawResource(licenseFileId);
         RasterSupport.setLicense(stream, developerKey);
         Log.w("RasterSupport", RasterSupport.getKernelType().toString());
      } catch(Exception ex) {
         Log.w("RasterSupport", ex.getMessage());
      } finally {
         if(RasterSupport.getKernelType() != RasterKernelType.RELEASE && !isKernelExpired())
            RasterSupport.initialize(context);
      }
   }
}
