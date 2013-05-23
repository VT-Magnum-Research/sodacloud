package leadtools.demos;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import leadtools.LeadRect;
import leadtools.LeadSize;
import leadtools.RasterImage;
import leadtools.RasterPaintAlignMode;
import leadtools.RasterPaintSizeMode;
import leadtools.RasterViewPerspective;
import leadtools.codecs.CodecsImageInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Utils {

   private static final int MAX_IMAGE_WIDTH = 1500;
   private static final int MAX_IMAGE_HEIGHT = 1500;

   public final static String[] mSupportedImagesFormats = new String[]
   {
      ".bmp", ".cmp", ".gif", ".jpg", ".jpeg", ".j2k", ".jp2", ".jbg", ".jb2", ".jxr", ".jls", ".tif", ".tiff", ".pdf", ".png", ".psd", ".mng"
   };

   // Return the image size with perspective 
   public static LeadSize getImageSize(CodecsImageInfo imageInfo) {
      int viewPerspectiveValue = imageInfo.getViewPerspective().getValue();
      boolean isRotated = (viewPerspectiveValue == RasterViewPerspective.TOP_LEFT_90.getValue() || viewPerspectiveValue == RasterViewPerspective.TOP_LEFT_270.getValue() || viewPerspectiveValue == RasterViewPerspective.BOTTOM_LEFT_90.getValue() || viewPerspectiveValue == RasterViewPerspective.BOTTOM_LEFT_270.getValue());
      int imageWidth = (isRotated ? imageInfo.getHeight() : imageInfo.getWidth());
      int imageHeight = (isRotated ? imageInfo.getWidth() : imageInfo.getHeight());
      
      if(imageWidth > MAX_IMAGE_WIDTH || imageHeight > MAX_IMAGE_HEIGHT) {
         LeadRect destRect = new LeadRect(0, 0, MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
         destRect = RasterImage.calculatePaintModeRectangle(imageWidth, imageHeight, destRect, RasterPaintSizeMode.FIT, RasterPaintAlignMode.NEAR, RasterPaintAlignMode.NEAR);
         imageWidth = destRect.getWidth();
         imageHeight = destRect.getHeight();
      }
      
      return new LeadSize(imageWidth, imageHeight);
   }

   // Return the folder in which the shared libraries are stored
   public static String getSharedLibsPath(Context context) {
      return String.format("%s/lib/", context.getApplicationInfo().dataDir); 
   }

   public static String getGalleryPathName(ContentResolver contentResolver, Uri uri) {
      String[] proj = { MediaStore.Images.Media.DATA };
      Cursor cursor = contentResolver.query(uri, proj, null, null, null);
      if(cursor == null)
         return null;
      
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
  }

   public static boolean createDirectory(String path) {
      File file = new File(path);
      return file.mkdirs();
   }
   
   public static File getExtFile(String prefix, String suffix, String directoryPath) {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
      String currentTime = simpleDateFormat.format(new Date());
      File file = new File(String.format("%s%s%s%s", directoryPath, prefix, currentTime, suffix));
      return file;
   }

   public static Uri getExtFileUri(String prefix, String suffix, String directoryPath) {
      return Uri.fromFile(getExtFile(prefix, suffix, directoryPath));
   }

   public static FilenameFilter getSupportedImagesFormatFilter() {
      return new FileFilter(mSupportedImagesFormats);
   }
   
   public static FilenameFilter getFileFilter(String[] filter) {
      return new FileFilter(filter);
   }
   
   static class FileFilter implements FilenameFilter {
      private String[] mFilter;

      public FileFilter(String[] filter) {
         mFilter = filter;
      }

      @Override
      public boolean accept(File dir, String filename) {
         if(mFilter == null)
            return true;

         // If directory 
         File file = new File(dir, filename);
         if(file.isDirectory())
            return true;

         int index = -1;
         if((index = filename.lastIndexOf(".")) != -1) {
            String ext = filename.substring(index);
            
            for(String filter: mFilter)
               if(ext.compareToIgnoreCase(filter) == 0)
                  return true;
         }

         return false;
      }
      
   }
}
