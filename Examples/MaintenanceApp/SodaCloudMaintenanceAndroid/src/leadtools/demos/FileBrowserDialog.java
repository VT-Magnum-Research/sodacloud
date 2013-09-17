package leadtools.demos;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.magnum.soda.example.maint.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FileBrowserDialog extends Dialog {

   private static final String CURRENT_LOCATION = "Current Location:";

   private FilenameFilter mFileFilter;
   private List<String> mViewPathList;
   private List<String> mPathList;
   private String mRootDirectory = "/sdcard";
   private String mCurrentDir = "";
   private Button mHomeButton;
   private Button mUpButton;
   private TextView mCurrentPathTxtView;
   private ListView mFilesListView;

   public FileBrowserDialog(Context context, FilenameFilter filter) {
      super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
      mFileFilter = filter;
   }

   private void enumDir(String dirPath) {
      mCurrentDir = dirPath;
      mCurrentPathTxtView.setText(String.format("%s %s", CURRENT_LOCATION, dirPath));

      mViewPathList.clear();
      mPathList.clear();

      File file = new File(dirPath);
      File[] fileArray = file.listFiles(mFileFilter);
      Arrays.sort(fileArray);

      if (dirPath.equals(mRootDirectory)) {
         mHomeButton.setEnabled(false);
         mUpButton.setEnabled(false);
         mHomeButton.setTextColor(Color.DKGRAY);
         mUpButton.setTextColor(Color.DKGRAY);
      } else {
         mHomeButton.setTag(mRootDirectory);
         mUpButton.setTag(file.getParent());
         mHomeButton.setEnabled(true);
         mUpButton.setEnabled(true);
         mHomeButton.setTextColor(Color.WHITE);
         mUpButton.setTextColor(Color.WHITE);
      }

      for (int i = 0; i < fileArray.length; i++) {
         file = fileArray[i];
         mPathList.add(file.getPath());
         if (file.isDirectory())
            mViewPathList.add(file.getName() + "/");
         else
            mViewPathList.add(file.getName());
      }

      mFilesListView.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.file_browser_element, mViewPathList));
   }
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.file_browser_dialog);
      
      mCurrentPathTxtView = (TextView) findViewById(R.id.txtview_current_path);
      
      mHomeButton = (Button) findViewById(R.id.btn_home);
      mHomeButton.setOnClickListener(new View.OnClickListener() {         
         @Override
         public void onClick(View view) {
            enumDir((String) view.getTag());
         }
      });

      mUpButton = (Button) findViewById(R.id.btn_up);
      mUpButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            enumDir((String) view.getTag());            
         }
      });

      mFilesListView = (ListView) findViewById(R.id.list_elements);
      mFilesListView.setOnItemClickListener(new OnItemClickListener() {

         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String filePath = mPathList.get(position);
            onFileSelected(filePath);
         }
      });

      mViewPathList = new ArrayList<String>();
      mPathList = new ArrayList<String>();
      
      enumDir(mRootDirectory);
   }
   
   protected String getCurrentDirectory() {
      return mCurrentDir + "/";
   }
   
   protected List<String> getCurrentFilesList() {
      return mPathList;
   }

   protected void onFileSelected(String fileName) {
      File file = new File(fileName);

      if (file.isDirectory()) {
         if(file.canRead())
            enumDir(fileName);
         else
            Messager.showError(getContext(), "Cannot open Read-Only directory", "Read-Only Directory");
      }
   }
}
