package leadtools.demos;

import java.io.File;
import java.io.FilenameFilter;

import org.magnum.soda.example.maint.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SaveFileDialog extends FileBrowserDialog {

   public interface OnFileSelectedListener {
      void onFileSelected(String fileName);
   }

   public static final String SAVE_FILE_NAME = "SaveFileName";

   private OnFileSelectedListener mCallBack; 
   private EditText mFileNameEditTxt;
   private Button mSaveBtn;

   public SaveFileDialog(Context context, FilenameFilter filter, OnFileSelectedListener callBack) {
      super(context, filter);
      
      mCallBack = callBack;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      Context context = getContext();
      LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View saveElements = layoutInflater.inflate(R.layout.save_file_elements, null);

      LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearlayout_elements);
      linearLayout.addView(saveElements);

      mFileNameEditTxt = (EditText)saveElements.findViewById(R.id.txtview_save_file_name);
      mSaveBtn = (Button)saveElements.findViewById(R.id.btn_save_file);

      mFileNameEditTxt.addTextChangedListener(new TextWatcher() {         
         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
         }
         
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
         }
         
         @Override
         public void afterTextChanged(Editable s) {
            mSaveBtn.setEnabled(s.length() > 0);
         }
      });
      
      mSaveBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            final String fileName = getCurrentDirectory() + mFileNameEditTxt.getText();
            final File file = new File(fileName);
            if(file.exists()) {
               AlertDialog.Builder overwriteDlgBuilder = new AlertDialog.Builder(getContext());
               overwriteDlgBuilder.setMessage(String.format("%s already exist, do you want to replace it?", fileName));

               AlertDialog overwriteDlg = overwriteDlgBuilder.create();
               
               Dialog.OnClickListener overwriteDialogClickListener = new Dialog.OnClickListener() {            
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                     if(which == AlertDialog.BUTTON_POSITIVE) {
                        onFileNameSelected(fileName);                        
                     }
                  }
               };

               overwriteDlg.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", overwriteDialogClickListener);
               overwriteDlg.setButton(AlertDialog.BUTTON_NEGATIVE, "No", overwriteDialogClickListener);
               overwriteDlg.show();
            } else {
               onFileNameSelected(fileName);
            }
         }
      });
   }

   private void onFileNameSelected(String fileName) {
      if(mCallBack != null)
         mCallBack.onFileSelected(fileName);
      dismiss();
   }

   @Override
   protected void onFileSelected(String filePath) {
      File file = new File(filePath);

      if (file.isFile()) {
         mFileNameEditTxt.setText(file.getName());
      } else {
         super.onFileSelected(filePath);
      }
   }
}
