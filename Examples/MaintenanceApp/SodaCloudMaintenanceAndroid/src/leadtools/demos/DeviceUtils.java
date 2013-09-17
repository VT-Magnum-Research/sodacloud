package leadtools.demos;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

public class DeviceUtils {
   
   public static boolean hasCamera(Context context) {
      return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
   }
   
   public static boolean isMediaMounted() {
      return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
   }
}
