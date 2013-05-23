package leadtools.demos;

import android.app.ProgressDialog;
import android.content.Context;

public class Progress {
   public static ProgressDialog show(Context context) {
      return show(context, false);
   }

   public static ProgressDialog show(Context context, boolean horizontalProgress) {
      return show(context, null, null, horizontalProgress);
   }

   public static ProgressDialog show(Context context, String title, String message) {
      return show(context, title, message, false);
   }

   public static ProgressDialog show(Context context, String title, String message, boolean horizontalProgress) {
      ProgressDialog progressDialog = create(context, title, message, horizontalProgress);
      progressDialog.show();

      return progressDialog;
   }
   
   public static ProgressDialog create(Context context, String title, String message, boolean horizontalProgress) {
      ProgressDialog progressDialog = new ProgressDialog(context);
      progressDialog.setTitle(title);
      progressDialog.setMessage(message);
      progressDialog.setCancelable(false);
      progressDialog.setCanceledOnTouchOutside(false);
      if(horizontalProgress) {
         progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
         progressDialog.setMax(100);
      }
      
      return progressDialog;
   }
   
   public static void close(ProgressDialog progressDlg) {
      if(progressDlg != null && progressDlg.isShowing())
         progressDlg.dismiss();
   }
}
