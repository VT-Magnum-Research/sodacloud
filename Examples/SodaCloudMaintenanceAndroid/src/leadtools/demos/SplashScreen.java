package leadtools.demos;

import org.magnum.soda.example.maint.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.splash_screen);
      

      Thread splashScreenTread = new Thread() {
         @Override
         public void run() {
            try {               
               sleep(2000);
            } catch (InterruptedException ex) {
            } finally {
               finish();
            }
         }
      };
      splashScreenTread.start();
   }
   
   public static void show(Context context) {
      Intent intent = new Intent();
      intent.setClass(context, SplashScreen.class);
      context.startActivity(intent);
   }
}
