package leadtools.annotationsdemo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import leadtools.LeadEvent;
import leadtools.LeadRectD;
import leadtools.LeadStreamFactory;
import leadtools.RasterImage;
import leadtools.annotations.automation.AnnAutomation;
import leadtools.annotations.automation.AnnAutomationManager;
import leadtools.annotations.automation.AnnAutomationObjects;
import leadtools.annotations.automation.ImageViewerAutomationControl;
import leadtools.annotations.core.AnnAudioObject;
import leadtools.annotations.core.AnnCodecs;
import leadtools.annotations.core.AnnContainer;
import leadtools.annotations.core.AnnDesignerOperationStatus;
import leadtools.annotations.core.AnnEditTextEvent;
import leadtools.annotations.core.AnnEditTextListener;
import leadtools.annotations.core.AnnEventListener;
import leadtools.annotations.core.AnnFormat;
import leadtools.annotations.core.AnnMediaObject;
import leadtools.annotations.core.AnnObject;
import leadtools.annotations.core.AnnObjectCollection;
import leadtools.annotations.core.AnnPicture;
import leadtools.annotations.core.AnnResources;
import leadtools.annotations.core.AnnRubberStampType;
import leadtools.annotations.core.AnnRunDesignerEvent;
import leadtools.annotations.core.AnnRunDesignerListener;
import leadtools.annotations.core.AnnTextObject;
import leadtools.annotations.core.AnnUserMode;
import leadtools.annotations.rendering.AnnAndroidRenderingEngine;
import leadtools.codecs.CodecsLoadAsyncCompletedEvent;
import leadtools.codecs.CodecsLoadAsyncCompletedListener;
import leadtools.codecs.RasterCodecs;
import leadtools.controls.ImageViewerPanZoomInteractiveMode;
import leadtools.controls.RasterImageViewer;
import leadtools.demos.DeviceUtils;
import leadtools.demos.Messager;
import leadtools.demos.OpenFileDialog;
import leadtools.demos.Progress;
import leadtools.demos.SplashScreen;
import leadtools.demos.Utils;

import org.magnum.soda.example.maint.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

public class AnnotationsDemoActivity extends Activity {

   private RasterImageViewer mViewer;
   private AnnAutomationManager mAutomationManager;
   private AnnAutomation mActiveAutomation;
   private ImageViewerAutomationControl mImageViewerAutomationControl;
   private ColorMatrixColorFilter mScaleColorFilter;
   private ArrayList<CharSequence> mImagesList = new ArrayList<CharSequence>();
   private ProgressDialog mProgressDlg;

   private String mLastAudioUri;
   private MediaPlayer mAudioPlayer;
   
   private Bitmap mImage;
   private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static File f = null;
	

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_annotations);

      
      leadtools.ltkrn lt=new leadtools.ltkrn();
      lt.Initialize(this);
     
      //Show Splash Screen
      SplashScreen.show(this);

      ColorMatrix cm = new ColorMatrix();
      cm.setScale(.75f, .75f, .75f, .75f);
      mScaleColorFilter = new ColorMatrixColorFilter(cm);

      mAudioPlayer = new MediaPlayer();

      mViewer = (RasterImageViewer) findViewById(R.id.imageviewer);
      mViewer.setUseDpi(true);

      DisplayMetrics metrics = new DisplayMetrics();
      getWindowManager().getDefaultDisplay().getMetrics(metrics);
      mViewer.setScreenDpiX(metrics.densityDpi);
      mViewer.setScreenDpiY(metrics.densityDpi);
      
      mAutomationManager = new AnnAutomationManager();
      mAutomationManager.createDefaultObjects();

      mImageViewerAutomationControl = new ImageViewerAutomationControl(mViewer);
      mViewer.setTouchInteractiveMode(mImageViewerAutomationControl);

      
      if(getIntent().hasExtra("byteArray")) {
    	  mImage = BitmapFactory.decodeByteArray(
    	        getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);        
    	  }
      
      mViewer.setImageBitmap(mImage);
      
		try {
			f = setUpPhotoFile("Original");
			f.getAbsolutePath();
		}
		catch(Exception e)
		{
			
		}
      mImagesList.add("Original.jpg");
      mActiveAutomation = createAutomation();
     

      AnnAutomationObjects automationObjects = mAutomationManager.getObjects();
      
      onImageChanged(0, false);
   }
   
   private File createImageFile(String name) throws IOException {
		String imageFileName = JPEG_FILE_PREFIX + name;
		File imageF = new File("/sdcard/" + imageFileName + JPEG_FILE_SUFFIX);
		if (!imageF.exists()) {
			imageF.createNewFile();
		}
		return imageF;
	}

	private File setUpPhotoFile(String name) throws IOException {

		File f = createImageFile(name);
		f.getAbsolutePath();

		return f;
	}

   @Override
   protected void onPause () {
      if(mAudioPlayer != null && mAudioPlayer.isPlaying())
         mAudioPlayer.stop();

      if(isFinishing()) {
         if(mActiveAutomation != null)
            mActiveAutomation.getContainer().getChildren().clear();
         if(mViewer != null)
            mViewer.setImage(null);
      }
      
      super.onPause();
   }

 
   private void onImageChanged(final int index, boolean showProgress) {
      try {
         // Show Progress
         if(showProgress)
            mProgressDlg = Progress.show(this, "Load Image", "Loading");

         RasterCodecs codecs = new RasterCodecs(Utils.getSharedLibsPath(this));
         codecs.addLoadAsyncCompletedListener(new CodecsLoadAsyncCompletedListener() {
            @Override
            public void onLoadAsyncCompleted(CodecsLoadAsyncCompletedEvent event) {
               Progress.close(mProgressDlg);
               if (event.getError() != null || event.getCancelled()) {
                  Messager.showError(AnnotationsDemoActivity.this, event.getError().getMessage(), "Error loading file");
               } else {
                  // Set Image
                  try {
                     mViewer.setImage(event.getImage());
                     if (mActiveAutomation != null)
                        mActiveAutomation.setActive(false);
                     mActiveAutomation = mAutomationManager.getAutomations().get(index);
                     mActiveAutomation.setActive(true);
                  } catch(Exception ex) {
                     Messager.showError(AnnotationsDemoActivity.this, ex.getMessage(), null);
                  }

               }
               updateToolbar();
            }
         });

         // Load image async
         ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
         mViewer.getImageBitmap().compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos); 
         byte[] bitmapdata = bos.toByteArray();
         ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
         codecs.loadAsync(LeadStreamFactory.create(bs, true), null);

      } catch (Exception ex) {
         Progress.close(mProgressDlg);
         Messager.showError(this, ex.getMessage(), "Error loading file");
      }
   }
   
   private AnnAutomation createAutomation() {
      AnnAutomation automation = new AnnAutomation(mAutomationManager, mImageViewerAutomationControl);
      automation.detach();
      automation.addEditTextListener(new AnnEditTextListener() {
         
         @Override
         public void onEditText(AnnEditTextEvent event) {
            onEditTextObject();
         }
      });

      automation.addRunDesignerListener(new AnnRunDesignerListener() {
         
         @Override
         public void onRunDesigner(AnnRunDesignerEvent event) {
            if (event.getOperationStatus() == AnnDesignerOperationStatus.START) {
               AnnObject annObject = event.getObject();
               String hyperlink = annObject.getHyperlink();

               int id = annObject.getId();
               if(id == AnnObject.MEDIA_OBJECT_ID) {
                  AnnMediaObject videoObj = (AnnMediaObject)annObject;
                  String mediaSrc = videoObj.getMedia().getSource1();
                  playVideo(mediaSrc);
               }
               else if(id == AnnObject.AUDIO_OBJECT_ID) {
                  AnnAudioObject audioObj = (AnnAudioObject)annObject;
                  String audioSrc = audioObj.getMedia().getSource1();
                  playAudio(audioSrc);
               } else if(hyperlink != null && hyperlink.length() > 0) {
                  String[] strings = hyperlink.split("//");
                  if (strings != null && strings.length < 2) {
                     hyperlink="http://" + hyperlink;
                  }

                  startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(hyperlink)));
               }
            }            
         }
      });
      automation.addSelectedObjectsChangedListener(new AnnEventListener() {
         
         @Override
         public void onAnnEvent(LeadEvent event) {
            updateToolbar();
         }
      });

      {
         AnnResources resources = new AnnResources();
         automation.getContainer().setResources(resources);
         HashMap<AnnRubberStampType, AnnPicture> rubberStampsResources = resources.getRubberStamps();
         List<AnnPicture> imagesResources = resources.getImages();

         Resources res = getResources();
         rubberStampsResources.put(AnnRubberStampType.STAMP_APPROVED, new AnnPicture(res, R.drawable.stamp_approved));
      /*   rubberStampsResources.put(AnnRubberStampType.STAMP_ASSIGNED, new AnnPicture(res, R.drawable.stamp_assigned));
         rubberStampsResources.put(AnnRubberStampType.STAMP_CLIENT, new AnnPicture(res, R.drawable.stamp_client));
         rubberStampsResources.put(AnnRubberStampType.STAMP_CHECKED, new AnnPicture(res, R.drawable.stamp_checked));
         rubberStampsResources.put(AnnRubberStampType.STAMP_COPY, new AnnPicture(res, R.drawable.stamp_copy));
         rubberStampsResources.put(AnnRubberStampType.STAMP_DRAFT, new AnnPicture(res, R.drawable.stamp_draft));
         rubberStampsResources.put(AnnRubberStampType.STAMP_EXTENDED, new AnnPicture(res, R.drawable.stamp_extended));
         rubberStampsResources.put(AnnRubberStampType.STAMP_FAX, new AnnPicture(res, R.drawable.stamp_fax));
         rubberStampsResources.put(AnnRubberStampType.STAMP_FAXED, new AnnPicture(res, R.drawable.stamp_faxed));
         rubberStampsResources.put(AnnRubberStampType.STAMP_IMPORTANT, new AnnPicture(res, R.drawable.stamp_important));
         rubberStampsResources.put(AnnRubberStampType.STAMP_INVOICE, new AnnPicture(res, R.drawable.stamp_invoice));
         rubberStampsResources.put(AnnRubberStampType.STAMP_NOTICE, new AnnPicture(res, R.drawable.stamp_notice));
         rubberStampsResources.put(AnnRubberStampType.STAMP_PAID, new AnnPicture(res, R.drawable.stamp_paid));

         rubberStampsResources.put(AnnRubberStampType.STAMP_OFFICIAL, new AnnPicture(res, R.drawable.stamp_official));
         rubberStampsResources.put(AnnRubberStampType.STAMP_ON_FILE, new AnnPicture(res, R.drawable.stamp_on_file));
         rubberStampsResources.put(AnnRubberStampType.STAMP_PASSED, new AnnPicture(res, R.drawable.stamp_passed));
         rubberStampsResources.put(AnnRubberStampType.STAMP_PENDING, new AnnPicture(res, R.drawable.stamp_pending));
         rubberStampsResources.put(AnnRubberStampType.STAMP_PROCESSED, new AnnPicture(res, R.drawable.stamp_processed));
         rubberStampsResources.put(AnnRubberStampType.STAMP_RECEIVED, new AnnPicture(res, R.drawable.stamp_received));
         rubberStampsResources.put(AnnRubberStampType.STAMP_REJECTED, new AnnPicture(res, R.drawable.stamp_rejected));
         rubberStampsResources.put(AnnRubberStampType.STAMP_RELEASE, new AnnPicture(res, R.drawable.stamp_release));
         rubberStampsResources.put(AnnRubberStampType.STAMP_SENT, new AnnPicture(res, R.drawable.stamp_sent));
         rubberStampsResources.put(AnnRubberStampType.STAMP_SHIPPED, new AnnPicture(res, R.drawable.stamp_shipped));
         rubberStampsResources.put(AnnRubberStampType.STAMP_TOP_SECRET, new AnnPicture(res, R.drawable.stamp_top_secret));
         rubberStampsResources.put(AnnRubberStampType.STAMP_URGENT, new AnnPicture(res, R.drawable.stamp_urgent));
         rubberStampsResources.put(AnnRubberStampType.STAMP_VOID, new AnnPicture(res, R.drawable.stamp_void));*/

         imagesResources.add(new AnnPicture(res, R.drawable.objects_point));
         imagesResources.add(new AnnPicture(res, R.drawable.objects_lock));
         imagesResources.add(new AnnPicture(res, R.drawable.objects_hotspot));
         imagesResources.add(new AnnPicture(res, R.drawable.objects_audio));
         imagesResources.add(new AnnPicture(res, R.drawable.objects_video));
         imagesResources.add(new AnnPicture(res, R.drawable.objects_encrypt_primary));
         imagesResources.add(new AnnPicture(res, R.drawable.objects_encrypt_secondary));
      }

      return automation;
   }
   
   private void playAudio(String uri) {
      if(mAudioPlayer.isPlaying())
         mAudioPlayer.stop();

      if(mLastAudioUri == uri || uri == "") {
         mLastAudioUri = "";
         return;
      }

      mLastAudioUri = uri;

      try {
         mAudioPlayer.reset();
         mAudioPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
               mp.start();
            }
         });
         mAudioPlayer.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
               Messager.showError(AnnotationsDemoActivity.this, "Sorry, this media file cannot be played", "");
               return true;
            }
         });

         mAudioPlayer.setDataSource(uri);
         mAudioPlayer.prepareAsync();
      } catch(Exception ex) {
         Messager.showError(this, ex.getMessage(), "");
      }
   }

   public void onObjectChanged(View v) {
      v.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
      int objectIndex = Integer.parseInt((String)v.getTag());
      if (objectIndex == 0) {
         mViewer.setTouchInteractiveMode(new ImageViewerPanZoomInteractiveMode());
      }
      else {
         if (mViewer.getTouchInteractiveMode() != mImageViewerAutomationControl)
           mViewer.setTouchInteractiveMode(mImageViewerAutomationControl);
         
         mActiveAutomation.getManager().setCurrentObjectId(objectIndex);
      }
   }

   public void onAction(View v) {
      v.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
      int id = v.getId();
      try {
         if (id == R.id.btn_select_image) {
            onSelectImageDialog();
         } else if (id == R.id.btn_save_image) {
            saveImage();
         } else if (id == R.id.btn_load_ann) {
            loadAnnotations();
         } else if (id == R.id.btn_save_ann) {
            saveAnnotations();
         } else if (id == R.id.btn_undo) {
            if(mActiveAutomation.canUndo())
               mActiveAutomation.undo();
         } else if (id == R.id.btn_redo) {
            if(mActiveAutomation.canRedo())
               mActiveAutomation.redo();
         } else if (id == R.id.btn_ann_copy) {
            if(mActiveAutomation.canCopy())
               mActiveAutomation.copy(this);
         } else if (id == R.id.btn_ann_paste) {
            if(mActiveAutomation.canPaste(this))
               mActiveAutomation.paste(this);
         } else if (id == R.id.btn_delete) {
            if(mActiveAutomation.canDeleteObjects())
               mActiveAutomation.deleteSelectedObjects();
         } else if (id == R.id.btn_lock) {
            if(mActiveAutomation.canLock())
               onPasswordDialog();
         } else if (id == R.id.btn_unlock) {
            if(mActiveAutomation.canUnlock())
               onPasswordDialog();
         } else if (id == R.id.btn_properties) {
            showAnnPropertiesDialog();
         } else if (id == R.id.btn_burn) {
            burnAnnotations();
         } else if (id == R.id.btn_apply_encrypt) {
            if(mActiveAutomation.canApplyEncryptor())
               mActiveAutomation.applyEncryptor();
         } else if (id == R.id.btn_apply_decrypt) {
            if(mActiveAutomation.canApplyDecryptor())
               mActiveAutomation.applyDecryptor();
         } else if (id == R.id.btn_realize) {
            if(mActiveAutomation.canRealizeRedaction())
               mActiveAutomation.realizeRedaction();
         } else if (id == R.id.btn_restore) {
            if(mActiveAutomation.canRestoreRedaction())
               mActiveAutomation.restoreRedaction();
            else
               Messager.showNotification(this, "The redaction object cannot be realized because it has already been realized or has been resized.");
         } else if (id == R.id.btn_run_mode) {
            if(mAutomationManager.getUserMode() != AnnUserMode.RUN)
               mAutomationManager.setUserMode(AnnUserMode.RUN);
         } else if (id == R.id.btn_design_mode) {
            if(mAutomationManager.getUserMode() != AnnUserMode.DESIGN)
               mAutomationManager.setUserMode(AnnUserMode.DESIGN);
         }

         if(mAudioPlayer.isPlaying())
            mAudioPlayer.stop();
      } catch(Exception ex) {
         Messager.showError(this, ex.getMessage(), "Error");
      }

      updateToolbar();
   }
   
   private void onSelectImageDialog() {
      AlertDialog.Builder imagesDialogBuilder = new AlertDialog.Builder(this);
      imagesDialogBuilder.setItems(mImagesList.toArray(new CharSequence[0]), new OnClickListener() {            
         @Override
         public void onClick(DialogInterface dialog, int which) {
            onImageChanged(which, true);
         }
      });
      
      AlertDialog imagesDialog = imagesDialogBuilder.create();
      imagesDialog.setCanceledOnTouchOutside(true);
      imagesDialog.show();
   }
   
   private void saveImage() {
      RasterImage image = mViewer.getImage();
      if(mViewer.getImage() == null) {
         Messager.showError(this, "Load an image first", null);
         return;
      }
      if(!DeviceUtils.isMediaMounted()) {
         Messager.showError(this, "The sdcard is not mounted", null);
         return;
      }
      Bitmap viewerBitmap = mViewer.getImageBitmap();
      
      
      ByteArrayOutputStream bs = new ByteArrayOutputStream();
      viewerBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
      
     Intent resultIntent = new Intent();
     resultIntent.putExtra("result", bs.toByteArray());
     setResult(Activity.RESULT_OK, resultIntent);
     finish();
     // ImageFileSaver saver = new ImageFileSaver(this);
      //saver.save(image);
   }

   private void loadAnnotations() {
      if(!DeviceUtils.isMediaMounted()) {
         Messager.showError(this, "The sdcard is not mounted", null);
         return;
      }
      OpenFileDialog.OnFileSelectedListener onFileSelectedListener = new OpenFileDialog.OnFileSelectedListener() {
         @Override
         public void onFileSelected(String fileName) {
            try {
               File file = new File(fileName);
               AnnCodecs codecs = new AnnCodecs();
               AnnContainer container = codecs.load(file, 1);

               AnnObjectCollection srcChildren = container.getChildren();

               if (srcChildren.size() > 0) {
                  AnnObjectCollection destChildren = mActiveAutomation.getContainer().getChildren();
                  destChildren.clear();

                  for (int i = 0; i < srcChildren.size(); i++) {
                     AnnObject child = srcChildren.get(i);
                     destChildren.add(child);
                  }
               }
               mActiveAutomation.getAutomationControl().automationInvalidate(LeadRectD.getEmpty());
                
            } catch (Exception ex) {
               Messager.showError(AnnotationsDemoActivity.this, "File does not contain valid LEADTOOLS annotation data", "Error Loading Annotation");
            }
         }
      };

      OpenFileDialog openDlg = new OpenFileDialog(this, Utils.getFileFilter(new String[] { ".xml" } ), onFileSelectedListener);         
      openDlg.show();
   }

   private void saveAnnotations() {
      if(!DeviceUtils.isMediaMounted()) {
         Messager.showError(this, "The sdcard is not mounted", null);
         return;
      }
      
     /* SaveFileDialog.OnFileSelectedListener onFileSelectedListener = new SaveFileDialog.OnFileSelectedListener() {
         @Override
         public void onFileSelected(String fileName) {
            try {
               if (!fileName.endsWith(".xml"))
                  fileName += ".xml";

               FileWriter fw = new FileWriter(fileName);
               AnnCodecs saveCodecs = new AnnCodecs();
               saveCodecs.save(fw, mActiveAutomation.getContainer(), AnnFormat.ANNOTATIONS, null, 1);
            } catch (Exception ex) {
               Messager.showError(AnnotationsDemoActivity.this, String.format("Error Saving File: %s", ex.getMessage()), null);
            }
         }
      };

      SaveFileDialog saveDlg = new SaveFileDialog(this, Utils.getFileFilter(new String[] { ".xml" } ), onFileSelectedListener);
      saveDlg.show();*/
      FileWriter fw;
	try {
		fw = new FileWriter(f.getAbsolutePath());
		 AnnCodecs saveCodecs = new AnnCodecs();
	      saveCodecs.save(fw, mActiveAutomation.getContainer(), AnnFormat.ANNOTATIONS, null, 1);
	     
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      }

   private void showAnnPropertiesDialog() {
      if (mActiveAutomation.canShowProperties()) {
         AnnObject editObject = mActiveAutomation.getCurrentEditObject();
         if (editObject.isLocked()) {
            Messager.showNotification(this, "Cannot change properties for a locked object");
            return;
         }
         if ((editObject.getId() == AnnObject.GROUP_OBJECT_ID) || (editObject.getId() == AnnObject.SELECT_OBJECT_ID)) {
            Messager.showNotification(this, "Cannot change properties for a group");
            return;
         }

      }
   }
   
   private void burnAnnotations() {
      if (mActiveAutomation != null && mActiveAutomation.getManager().getUserMode() == AnnUserMode.DESIGN) {
         Bitmap viewerBitmap = mViewer.getImageBitmap();
         Canvas canvas = new Canvas(viewerBitmap);
         AnnAndroidRenderingEngine renderingEngine = new AnnAndroidRenderingEngine(mActiveAutomation.getContainer(), canvas);
         renderingEngine.setRenderers(mAutomationManager.getRenderingEngine().getRenderers());
         if (renderingEngine != null) {
            double dpiX = mViewer.getScreenDpiX();
            double dpiY = mViewer.getScreenDpiY();
            double xRes = mViewer.getImageDpiX();
            double yRes = mViewer.getImageDpiY();
            renderingEngine.burnToRectWithDpi(LeadRectD.getEmpty(), dpiX, dpiY, xRes, yRes);
            if(mViewer instanceof RasterImageViewer)
               ((RasterImageViewer) mViewer).updateImageFromBitmap();
            mImageViewerAutomationControl.automationInvalidate(LeadRectD.getEmpty());
         }
      }
   }

   public void onEditTextObject() {
      final AnnTextObject textObject = (AnnTextObject)(mActiveAutomation.getCurrentEditObject() instanceof AnnTextObject ? mActiveAutomation.getCurrentEditObject() : null);
      if(textObject == null)
         return;

      final AlertDialog.Builder editTextBuilder = new AlertDialog.Builder(this);
      editTextBuilder.setTitle("Edit Text");
      final EditText editText = new EditText(this);
      editText.setText(textObject.getText());
      editText.setMinLines(4);
      editTextBuilder.setView(editText);
      
      AlertDialog editTextDialog = editTextBuilder.create();
      
      Dialog.OnClickListener editTextDialogClickListener = new Dialog.OnClickListener() {            
         @Override
         public void onClick(DialogInterface dialog, int which) {
            if(which == AlertDialog.BUTTON_POSITIVE) {
               textObject.setText(editText.getText().toString());
               mImageViewerAutomationControl.automationInvalidate(LeadRectD.getEmpty());
            }
         }
      };
      editTextDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", editTextDialogClickListener);
      editTextDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", editTextDialogClickListener);
      
      editTextDialog.show();
   }
   
   public void onPasswordDialog() {
      final AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(this);
      passwordDialogBuilder.setTitle("Enter Password");
      final EditText passwordText = new EditText(this);
      passwordText.setLines(1);
      passwordText.setTransformationMethod(new PasswordTransformationMethod());
      passwordText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
      passwordText.setHint("Password");
      passwordDialogBuilder.setView(passwordText);

      final AlertDialog passwordDialog = passwordDialogBuilder.create();

      Dialog.OnClickListener passwordDialogClickListener = new Dialog.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
               AnnObject object = mActiveAutomation.getCurrentEditObject();
               if(!object.isLocked())
                  object.lock(passwordText.getText().toString());
               else if(mActiveAutomation.canUnlock()) {
                  object.unlock(passwordText.getText().toString());
                  if(object.isLocked())
                     Messager.showNotification(AnnotationsDemoActivity.this, "Incorrect password");
               }
               mImageViewerAutomationControl.automationInvalidate(LeadRectD.getEmpty());
               updateToolbar();
            }
         }
      };
      passwordDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Submit", passwordDialogClickListener);
      passwordDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", passwordDialogClickListener);

      passwordDialog.show();
      
      //Disable "Submit" button
      passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
      //If password length > 0, enable "Submit" button
      passwordText.addTextChangedListener(new TextWatcher() {         
         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
         }
         
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
         }
         
         @Override
         public void afterTextChanged(Editable s) {
            passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() > 0);
         }
      });
   }
   
   private void playVideo(String uri) {
      Intent video = new Intent();
     // video.setClass(this, AnnVideoActivity.class);
     // video.putExtra(AnnVideoActivity.VIDEO_PATH_TAG, uri);
      startActivity(video);
   }
   
   private void enableToolbarButton(int resId, boolean enabled) {
      ImageButton button = (ImageButton)findViewById(resId);
      button.setEnabled(enabled);
      if(enabled)
         button.setColorFilter(null);
      else
         button.setColorFilter(mScaleColorFilter);  
   }
   
   private void updateToolbar() {
      AnnUserMode userMode = AnnUserMode.DESIGN;
      if(mActiveAutomation != null) {
         userMode = mActiveAutomation.getManager().getUserMode();
      }

      if(mActiveAutomation != null && userMode == AnnUserMode.RUN) {
         enableToolbarButton(R.id.btn_run_mode, false);
         enableToolbarButton(R.id.btn_design_mode, true);
      } else {
         enableToolbarButton(R.id.btn_run_mode, true);
         enableToolbarButton(R.id.btn_design_mode, false);
      }

      if(mActiveAutomation != null && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_save_image, true);
         enableToolbarButton(R.id.btn_select_image, true);
         enableToolbarButton(R.id.btn_save_ann, true);
         enableToolbarButton(R.id.btn_load_ann, true);
         enableToolbarButton(R.id.btn_burn, true);
      } else {
         enableToolbarButton(R.id.btn_save_image, false);
         enableToolbarButton(R.id.btn_select_image, false);
         enableToolbarButton(R.id.btn_save_ann, false);
         enableToolbarButton(R.id.btn_load_ann, false);
         enableToolbarButton(R.id.btn_burn, false);
      }

      if(mActiveAutomation != null && mActiveAutomation.canUndo() && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_undo, true);
      }
      else {
         enableToolbarButton(R.id.btn_undo, false);	      
      }

      if(mActiveAutomation != null && mActiveAutomation.canRedo() && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_redo, true);
      }
      else {
         enableToolbarButton(R.id.btn_redo, false);
      }

      if(mActiveAutomation != null && mActiveAutomation.canLock() && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_lock, true);
      }
      else {
         enableToolbarButton(R.id.btn_lock, false);
      }

      if(mActiveAutomation != null && mActiveAutomation.canUnlock() && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_unlock, true);
      }
      else {
         enableToolbarButton(R.id.btn_unlock, false);
      }

      if(mActiveAutomation != null && mActiveAutomation.canDeleteObjects() && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_delete, true);
      }
      else {
         enableToolbarButton(R.id.btn_delete, false);
      }
      
      if(mActiveAutomation != null && mActiveAutomation.canShowProperties() && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_properties, true);
      }
      else {
         enableToolbarButton(R.id.btn_properties, false);
      }

      // Copy\Paste
      if(mActiveAutomation != null && mActiveAutomation.canCopy() && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_ann_copy, true);
      }
      else {
         enableToolbarButton(R.id.btn_ann_copy, false);
      }
      if(mActiveAutomation != null && mActiveAutomation.canPaste(this) && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_ann_paste, true);
      }
      else {
         enableToolbarButton(R.id.btn_ann_paste, false);
      }

      // Encypt\Decrypt
      if(mActiveAutomation != null && mActiveAutomation.canApplyEncryptor() && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_apply_encrypt, true);
      }
      else {
         enableToolbarButton(R.id.btn_apply_encrypt, false);
      }
      if(mActiveAutomation != null && mActiveAutomation.canApplyDecryptor() && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_apply_decrypt, true);
      }
      else {
         enableToolbarButton(R.id.btn_apply_decrypt, false);
      }

      // Redaction - Realize\Restore      
      if(mActiveAutomation != null && mActiveAutomation.canRealizeRedaction() && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_realize, true);
      }
      else {
         enableToolbarButton(R.id.btn_realize, false);
      }
      if(mActiveAutomation != null && mActiveAutomation.canRestoreRedaction() && userMode == AnnUserMode.DESIGN) {
         enableToolbarButton(R.id.btn_restore, true);
      }
      else {
         enableToolbarButton(R.id.btn_restore, false);
      }
   }
}