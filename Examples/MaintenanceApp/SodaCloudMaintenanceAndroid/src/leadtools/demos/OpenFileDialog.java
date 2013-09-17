package leadtools.demos;

import java.io.File;
import java.io.FilenameFilter;

import android.content.Context;

public class OpenFileDialog extends FileBrowserDialog {

   public interface OnFileSelectedListener {
      void onFileSelected(String fileName);
   }

   private OnFileSelectedListener mCallBack;

   public OpenFileDialog(Context context, FilenameFilter filter, OnFileSelectedListener callBack) {
      super(context, filter);
      
      mCallBack = callBack;
   }

   @Override
   protected void onFileSelected(String fileName) {
      File file = new File(fileName);

      if (file.isFile()) {
         if(mCallBack != null)
            mCallBack.onFileSelected(fileName);

         dismiss();
      } else {
         super.onFileSelected(fileName);
      }
   }
}
