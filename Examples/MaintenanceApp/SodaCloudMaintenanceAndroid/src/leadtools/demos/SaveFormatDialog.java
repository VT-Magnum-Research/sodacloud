package leadtools.demos;

import java.util.ArrayList;
import java.util.Arrays;

import org.magnum.soda.example.maint.R;

import leadtools.RasterImageFormat;
import leadtools.codecs.CodecsJpeg2000CompressionControl;
import leadtools.codecs.RasterCodecs;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SaveFormatDialog extends Dialog {
   public interface OnSaveFormat {
      public void onSaveFormat(RasterCodecs codecs, RasterImageFormat format, int bitsPerPixel);
   }

   private OnSaveFormat mCallback;

   private int mBitsPerPixel;
   private RasterImageFormat mFormat;
   private int mQFactor;

   private RasterCodecs mCodecs;

   private Spinner mFormatSpinner;
   private Spinner mBPPSpinner;
   private Spinner mSubFormatSpinner;
   private SeekBar mQFSeekBar;
   private TextView mQFValueTxtView;

   public SaveFormatDialog(Context context, RasterCodecs codecs, OnSaveFormat callBack) {
      super(context);
      
      getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
      getWindow().setWindowAnimations(R.style.DialogAnimation);

      setTitle("Save Format");

      mCallback = callBack;
      mCodecs = codecs;
      mQFactor = 2;
   }
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      this.setContentView(R.layout.save_format_dialog);

      // Format Spinner
      ArrayAdapter<SaveFormat> formatsArrayAdapter = new ArrayAdapter<SaveFormat>(this.getContext(), android.R.layout.simple_spinner_item, SaveFormats);
      formatsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      mFormatSpinner = (Spinner) findViewById(R.id.spnr_saveformat);
      mFormatSpinner.setAdapter(formatsArrayAdapter);
      mFormatSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateSaveFormat(true, false);
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {            
         }
      });
      
      // BPP Spinner
      mBPPSpinner = (Spinner) findViewById(R.id.spnr_bits_per_pixel);
      mBPPSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mBitsPerPixel = (Integer) mBPPSpinner.getItemAtPosition(position);
            updateSaveFormat(false, true);
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {            
         }
      });
      
      // Sub-Format Spinner
      mSubFormatSpinner = (Spinner) findViewById(R.id.spnr_subformat);
      mSubFormatSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateSaveFormat(false, false);
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {            
         }
      });
      
      // QF Text Value
      mQFValueTxtView = (TextView) findViewById(R.id.txtview_quality_factor_value);
      mQFValueTxtView.setText(Integer.toString(mQFactor));

      // QF SeekBar
      mQFSeekBar = (SeekBar) findViewById(R.id.seekbar_quality_factor);
      mQFSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
         @Override
         public void onStopTrackingTouch(SeekBar seekBar) {
            updateSaveFormat(false, false);
         }
         
         @Override
         public void onStartTrackingTouch(SeekBar seekBar) {            
         }
         
         @Override
         public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(mFormat == RasterImageFormat.PNG)
               // 0 to 9
               mQFactor = progress;
            else
               // 2 to 255
               mQFactor = progress + 2;
         }
      });
      
      // OK Button
      Button okButton = (Button) findViewById(R.id.btn_ok);
      okButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if(mCallback != null)
               mCallback.onSaveFormat(mCodecs, mFormat, mBitsPerPixel);
            
            dismiss();
         }
      });

      // Cancel Button
      Button cancelButton = (Button) findViewById(R.id.btn_cancel);
      cancelButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            dismiss();
         }
      });
   }

   private void updateSaveFormat(boolean formatChanged, boolean bitsChanged) {
      SaveFormat fmt = (SaveFormat) mFormatSpinner.getSelectedItem();

      if (formatChanged) {
         updateBitsPerPixel(fmt.getAllBitsPerPixel());
      }

      if (bitsChanged && mBPPSpinner.getAdapter().getCount() > 0) {
         SubFormat oldSubformat = null;

         if(mSubFormatSpinner.getSelectedItem() != null)
            oldSubformat = (SubFormat) mSubFormatSpinner.getSelectedItem();

         if (fmt.getSubFormats() == null) {
            mSubFormatSpinner.setEnabled(false);
            // Set an empty adapter
            ArrayAdapter<SubFormat> subFormatArrayAdapter = new ArrayAdapter<SubFormat>(this.getContext(), android.R.layout.simple_spinner_item, new SubFormat[0]);
            mSubFormatSpinner.setAdapter(subFormatArrayAdapter);
         }
         else {
            mSubFormatSpinner.setEnabled(true);
            ArrayList<SubFormat> subFormatsList = new ArrayList<SubFormat>();
            int oldSelectedIndex = -1;
            for (SubFormat sub: fmt.getSubFormats()) {
               if (sub.getBitsPerPixel() != null && Arrays.asList(sub.getBitsPerPixel()).contains(mBPPSpinner.getSelectedItem())) {
                  subFormatsList.add(sub);
                  if (oldSubformat != null && sub == oldSubformat) {
                     oldSelectedIndex = subFormatsList.indexOf(sub);
                  }
               }
            }

            ArrayAdapter<SubFormat> subFormatArrayAdapter = new ArrayAdapter<SubFormat>(this.getContext(), android.R.layout.simple_spinner_item, subFormatsList);
            subFormatArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSubFormatSpinner.setAdapter(subFormatArrayAdapter);

            if(oldSelectedIndex == -1)
               mSubFormatSpinner.setSelection(0);
            else
               mSubFormatSpinner.setSelection(oldSelectedIndex);
         }
      }

      if (mBitsPerPixel == 16 && (mFormat == RasterImageFormat.JPEG || mFormat == RasterImageFormat.TIF_JPEG)) {
         mQFSeekBar.setEnabled(false);
         mQFactor = 0;
      }
      else {
         if (mSubFormatSpinner.getSelectedItem() != null) {
            SubFormat sub = (SubFormat) mSubFormatSpinner.getSelectedItem();
            mQFSeekBar.setEnabled(sub.getUseQFactor());
         }
         else {
            mQFSeekBar.setEnabled(fmt.getUseQFactor());
         }
      }

      if (!mQFSeekBar.isEnabled()) {
         mQFactor = 0;
         mQFSeekBar.setProgress(mQFactor);
      }
      else {
         if (mFormat == RasterImageFormat.PNG) {
            if(mQFactor > 9)
               mQFactor = 9;
            // 0 to 9
            mQFSeekBar.setMax(9);
            mQFSeekBar.setProgress(mQFactor);
         }
         else {
            if(mQFactor < 2)
               mQFactor = 2;
            // 2 to 255
            mQFSeekBar.setMax(253);
            mQFSeekBar.setProgress(mQFactor - 2);
         }
      }

      if (fmt.getFormat() != RasterImageFormat.UNKNOWN) {
         mFormat = fmt.getFormat();
         if (fmt.getFormat() == RasterImageFormat.PNG)
         {
            if (mQFactor > 9)
               mQFactor = 9;

            mCodecs.getOptions().getPng().getSave().setQualityFactor(mQFactor);
         }
         else
            fmt.setOptions(mCodecs, mQFactor);
      }
      else {
         SubFormat sub = (SubFormat) mSubFormatSpinner.getSelectedItem();
         if(sub != null) {
            mFormat = sub.getFormat();
            if (mQFactor == 0 &&
                  (sub.getFormat() == RasterImageFormat.RAS_PDF_JPEG_411 ||
                  sub.getFormat() == RasterImageFormat.RAS_PDF_JPEG ||
                  sub.getFormat() == RasterImageFormat.RAS_PDF_JPEG_422))
               sub.setOptions(mCodecs, 2);
            else
               sub.setOptions(mCodecs, mQFactor);
         }
      }
      
      // Update quality factor text value
      mQFValueTxtView.setText(Integer.toString(mQFactor));
   }
   
   private void updateBitsPerPixel(Integer[] bits) {
      mBPPSpinner.setAdapter(null);
      if (bits != null) {
         ArrayAdapter<Integer> bppArrayAdapter = new ArrayAdapter<Integer>(this.getContext(), android.R.layout.simple_spinner_item, bits);
         bppArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         mBPPSpinner.setAdapter(bppArrayAdapter);
         mBPPSpinner.setSelection(bits.length - 1);
      }

      mBPPSpinner.setEnabled(mBPPSpinner.getAdapter().getCount() > 1);
   }

   private SaveFormat[] SaveFormats = new SaveFormat[] {
         new SaveFormat("LEAD CMP", RasterImageFormat.UNKNOWN, new Integer[] { 8, 24 }, true, false, new SubFormat[] { 
               new CmpFormat(false), new CmpFormat(true), }),

         new SaveFormat("JPEG", RasterImageFormat.UNKNOWN, new Integer[] { 8, 12, 16, 24 }, true, false, new SubFormat[] {
               new JpegFormat(RasterImageFormat.JPEG, null, false, false),
               new JpegFormat(RasterImageFormat.JPEG, null, true, false),
               new JpegFormat(RasterImageFormat.JPEG, null, false, true),
               new JpegFormat(RasterImageFormat.JPEG_RGB, null, false, false),
               new JpegFormat(RasterImageFormat.JPEG_RGB, null, true, false),
               new JpegFormat(RasterImageFormat.JPEG_411, null, false, false),
               new JpegFormat(RasterImageFormat.JPEG_411, null, true, false),
               new JpegFormat(RasterImageFormat.JPEG_LAB_411, null, false, false),
               new JpegFormat(RasterImageFormat.JPEG_422, null, false, false),
               new JpegFormat(RasterImageFormat.JPEG_422, null, true, false),
               new JpegFormat(RasterImageFormat.JPEG_LAB_422, null, false, false),
               new JpegFormat(RasterImageFormat.JPEG_LAB, null, false, false), }), 

         new SaveFormat("PDF", RasterImageFormat.UNKNOWN, new Integer[] { 1, 2, 4, 8, 24 }, false, true, new SubFormat[]{
               new PdfFormat(RasterImageFormat.RAS_PDF, false),
               new PdfFormat(RasterImageFormat.RAS_PDF_G3_1DIM, false),
               new PdfFormat(RasterImageFormat.RAS_PDF_G3_2DIM, false),
               new PdfFormat(RasterImageFormat.RAS_PDF_G4, false),
               new PdfFormat(RasterImageFormat.RAS_PDF_JPEG, false),
               new PdfFormat(RasterImageFormat.RAS_PDF_JPEG, true),
               new PdfFormat(RasterImageFormat.RAS_PDF_JPEG_422, false),
               new PdfFormat(RasterImageFormat.RAS_PDF_JPEG_411, false)}),
                     
         new SaveFormat("TIFF", RasterImageFormat.UNKNOWN, new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 12, 16, 24, 32, 48, 64 }, false, true, new SubFormat[] {
               new TiffFormat(RasterImageFormat.TIF),
               new TiffFormat(RasterImageFormat.TIF_CMYK),
               new TiffFormat(RasterImageFormat.TIF_YCC),
               new TiffFormat(RasterImageFormat.TIF_PACKBITS),
               new TiffFormat(RasterImageFormat.TIF_PACKBITS_CMYK),
               new TiffFormat(RasterImageFormat.TIF_PACKBITS_YCC),
               new TiffFormat(RasterImageFormat.TIFLZW),
               new TiffFormat(RasterImageFormat.TIFLZW_CMYK),
               new TiffFormat(RasterImageFormat.TIFLZW_YCC),
               new TiffFormat(RasterImageFormat.TIF_JPEG),
               new TiffFormat(RasterImageFormat.TIF_JPEG_411),
               new TiffFormat(RasterImageFormat.TIF_JPEG_422),
               new TiffFormat(RasterImageFormat.TIF_CMP),
               new TiffFormat(RasterImageFormat.TIF_JBIG),
               new TiffFormat(RasterImageFormat.TIF_JBIG2),
               new TiffFormat(RasterImageFormat.TIF_J2K),
               new TiffFormat(RasterImageFormat.CCITT),
               new TiffFormat(RasterImageFormat.CCITT_GROUP3_1DIM),
               new TiffFormat(RasterImageFormat.CCITT_GROUP3_2DIM),
               new TiffFormat(RasterImageFormat.CCITT_GROUP4), }),

         new SaveFormat("GeoTIFF", RasterImageFormat.GEO_TIFF, new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 12, 16, 24, 32, 48, 64 }, false, true, null),

         new SaveFormat("EXIF", RasterImageFormat.UNKNOWN, new Integer[] { 24 }, false, false, new SubFormat[] {
               new ExifFormat(RasterImageFormat.EXIF),
               new ExifFormat(RasterImageFormat.EXIF_YCC),
               new ExifFormat(RasterImageFormat.EXIF_JPEG_411),
               new ExifFormat(RasterImageFormat.EXIF_JPEG_422), }),

         new SaveFormat("JPEG 2000", RasterImageFormat.UNKNOWN, new Integer[] { 8, 12, 16, 24, 32, 48, 64 }, true, false, new SubFormat[] {
               new Jpeg2000Format(RasterImageFormat.J2K, false),
               new Jpeg2000Format(RasterImageFormat.J2K, true),
               new Jpeg2000Format(RasterImageFormat.JP2, false),
               new Jpeg2000Format(RasterImageFormat.JP2, true) }),

         new SaveFormat("JBIG", RasterImageFormat.JBIG, new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 12, 16, 24, 32 }, false, false, null),
         new SaveFormat("JBIG2", RasterImageFormat.JBIG2, new Integer[] { 1 }, false, false, null),

         new SaveFormat("Win Bitmap", RasterImageFormat.UNKNOWN, new Integer[] { 1, 4, 8, 16, 24, 32 }, false, false, new SubFormat[] { 
               new WinBitmapFormat(RasterImageFormat.BMP),
               new WinBitmapFormat(RasterImageFormat.BMP_RLE) }),

         new SaveFormat("OS2", RasterImageFormat.UNKNOWN, new Integer[] { 1, 4, 8, 16, 24 }, true, false, new SubFormat[] {
               new OS2Format(RasterImageFormat.OS2),
               new OS2Format(RasterImageFormat.OS2_2) }),

         new SaveFormat("GIF", RasterImageFormat.GIF, new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8 }, false, true, null),

         new SaveFormat("JPEG Extended Range", RasterImageFormat.UNKNOWN, new Integer[] { 1, 8, 16, 24, 32, 48, 64 }, false, false, new SubFormat[] {
               new SubFormat("JXR Black and White", RasterImageFormat.JXR, new Integer[] { 1 }, null, true, false),
               new SubFormat("JXR RGB", RasterImageFormat.JXR, new Integer[] {16, 32, 48, 64 }, null, true, false),
               new SubFormat("JXR Gray", RasterImageFormat.JXR_GRAY, new Integer[] { 8, 16 }, null, true, false),
               new SubFormat("JXR YUV 4:4:4", RasterImageFormat.JXR, new Integer[] { 24 }, null, true, false),
               new SubFormat("JXR YUV 4:2:2", RasterImageFormat.JXR_422, new Integer[] { 24 }, null, true, false),
               new SubFormat("JXR YUV 4:2:0", RasterImageFormat.JXR_420, new Integer[] { 24 }, null, true, false) }),

         new SaveFormat("JPEG LS", RasterImageFormat.UNKNOWN, new Integer[] { 8, 12, 16, 24 }, false, false, new SubFormat[] {
               new SubFormat("Lossless", RasterImageFormat.JLS, new Integer[] { 8, 12, 16 }, null, false, false), 
               new SubFormat("Near Lossless", RasterImageFormat.JLS, new Integer[] { 8, 12, 16 }, null, true, false),
               new SubFormat("Interleave Mode None - Lossless", RasterImageFormat.JLS, new Integer[] { 24 }, null, false, false),
               new SubFormat("Interleave Mode None - Near Lossless", RasterImageFormat.JLS, new Integer[] { 24 }, null, true, false),
               new SubFormat("Interleave Mode Line - Lossless", RasterImageFormat.JLS_LINE, new Integer[] { 24 }, null, false, false),
               new SubFormat("Interleave Mode Line - Near Lossless", RasterImageFormat.JLS_LINE, new Integer[] { 24 }, null, true, false),
               new SubFormat("Interleave Mode Sample - Lossless", RasterImageFormat.JLS_SAMPLE, new Integer[] { 24 }, null, false, false),
               new SubFormat("Interleave Mode Sample - Near Lossless", RasterImageFormat.JLS_SAMPLE, new Integer[] { 24 }, null, true, false) }),

         new SaveFormat("PSD", RasterImageFormat.PSD, new Integer[] { 1, 8, 24 }, false, false, null),

         new SaveFormat("PNG", RasterImageFormat.PNG, new Integer[] { 1, 4, 8, 24, 32, 48, 64 }, true, false, null),

   };

   /* SaveFormat Class */
   private class SaveFormat {
      private RasterImageFormat mFormat;
      public RasterImageFormat getFormat() {
         return mFormat;
      }

      private Integer[] mAllbitsPerPixel;
      public Integer[] getAllBitsPerPixel() {
         return mAllbitsPerPixel;
      }

      private boolean mUseQFactor;
      public boolean getUseQFactor() {
         return mUseQFactor;
      }
      public void setUseQFactor(boolean useQFactor) {
         mUseQFactor = useQFactor;
      }

      private String mDisplay;
      public String getDisplay() {
         return mDisplay;
      }
      public void setDisplay(String display) {
         mDisplay = display;
      }

      private SubFormat[] mSubFormats;
      public SubFormat[] getSubFormats() {
         return mSubFormats;
      }

      public SaveFormat(String display, RasterImageFormat fmt, Integer[] bits, boolean useQFactor, boolean multiPage, SubFormat[] subFormats) {
         mDisplay = display;
         mFormat = fmt;
         mAllbitsPerPixel = bits;
         mUseQFactor = useQFactor;
         mSubFormats = subFormats;
      }

      @Override
      public String toString() {
         return mDisplay;
      }

      public void setOptions(RasterCodecs codecs, int qFactor) {
         codecs.getOptions().getJpeg().getSave().setQualityFactor(qFactor);
      }
   }
   
   /* SubFormat Class */
   private class SubFormat extends SaveFormat {
      private Integer[] mBitsPerPixel;
      public Integer[] getBitsPerPixel() {
         return mBitsPerPixel;
      }
      public void setBitsPerPixel(Integer[] bitsPerPixel) {
         mBitsPerPixel = bitsPerPixel;
      }

      public SubFormat(String display, RasterImageFormat fmt, Integer[] bits, Integer[] allbits, boolean qFactor, boolean multiPage) {
         super(display, fmt, allbits, qFactor, multiPage, null);
         setBitsPerPixel(bits);
      }
   }

   /* CmpFormat Class */
   private class CmpFormat extends SubFormat {
      private int mPasses = 1;
      public CmpFormat(boolean progressive) {
         super((progressive ? "Progressive" : "Non-Progressive"), RasterImageFormat.CMP, new Integer[] { 8, 24 }, null, true, false);
         if (progressive) {
            mPasses = 10;
         }
      }

      @Override
      public void setOptions(RasterCodecs codecs, int qFactor) {
         codecs.getOptions().getJpeg().getSave().setPasses(mPasses);
         codecs.getOptions().getJpeg().getSave().setQualityFactor(qFactor);
         super.setOptions(codecs, qFactor);
      }
   }

   /* JpegFormat Class */
   private class JpegFormat extends SubFormat {
      private int mPasses = 1;

      public JpegFormat(RasterImageFormat format, Integer[] allbits, boolean progressive, boolean lossLess) {
         super("Jpeg", format, null, allbits, true, false);
         switch (format) {
         case JPEG:
            if (lossLess) {
               setBitsPerPixel(new Integer[] { 8, 12, 24 });
               setDisplay(String.format("%1$s %2$s", "Lossless", getDisplay()));
            } else {
               setBitsPerPixel(new Integer[] { 8, 12, 16, 24 });
               setDisplay(String.format("%1$s %2$s", getDisplay(), "YUV 4:4:4"));
            }
            break;
         case JPEG_411:
            setBitsPerPixel(new Integer[] { 8, 24 });
            setDisplay(String.format("%1$s %2$s", getDisplay(), "YUV 4:1:1"));
            break;
         case JPEG_422:
            setBitsPerPixel(new Integer[] { 8, 24 });
            setDisplay(String.format("%1$s %2$s", getDisplay(), "YUV 4:2:2"));
            break;
         case JPEG_LAB:
            setBitsPerPixel(new Integer[] { 24 });
            setDisplay(String.format("%1$s %2$s", getDisplay(), "LAB 4:4:4"));
            break;
         case JPEG_LAB_411:
            setBitsPerPixel(new Integer[] { 24 });
            setDisplay(String.format("%1$s %2$s", getDisplay(), "LAB 4:1:1"));
            break;
         case JPEG_LAB_422:
            setBitsPerPixel(new Integer[] { 24 });
            setDisplay(String.format("%1$s %2$s", getDisplay(), "LAB 4:2:2"));
            break;
         case JPEG_RGB:
            setBitsPerPixel(new Integer[] { 24 });
            setDisplay(String.format("%1$s %2$s", getDisplay(), "RGB 4:4:4"));
            break;
         default:
            throw new IllegalArgumentException("Invalid file format");
         }

         if (lossLess) {
            setUseQFactor(false);
         }

         if (progressive) {
            mPasses = 10;
            setDisplay(String.format("%1$s %2$s", getDisplay(), "Progressive"));
         }
      }

      @Override
      public void setOptions(RasterCodecs codecs, int qFactor) {
         codecs.getOptions().getJpeg().getSave().setPasses(mPasses);
         codecs.getOptions().getJpeg().getSave().setQualityFactor(qFactor);
         super.setOptions(codecs, qFactor);
      }
   }
   
   private class Jpeg2000Format extends SubFormat {
      public Jpeg2000Format(RasterImageFormat format, boolean lossLess) {
         super(null, format, new Integer[] { 8, 12, 16, 24, 32, 48, 64 }, null, true, false);
         switch (format) {
         case J2K:
            setDisplay("JPEG 2000");
            break;
         case JP2:
            setDisplay("JP2");
            break;
         default:
            throw new RuntimeException("Invalid file format");
         }

         if (lossLess) {
            setDisplay(String.format("%1$s %2$s", "Lossless", getDisplay()));
         } else {
            setDisplay(String.format("%1$s %2$s", "Lossy", getDisplay()));
         }

         if (lossLess) {
            setUseQFactor(false);
         } else {
            setUseQFactor(true);
         }
      }

      @Override
      public void setOptions(RasterCodecs codecs, int qFactor) {
         codecs.getOptions().getJpeg().getSave().setQualityFactor(qFactor);
         codecs.getOptions().getJpeg2000().getSave().setCompressionControl(CodecsJpeg2000CompressionControl.QUALITY_FACTOR);
         super.setOptions(codecs, qFactor);
      }
   }
   
   private class ExifFormat extends SubFormat {
      public ExifFormat(RasterImageFormat format) {
         super("", format, new Integer[] { 24 }, null, false, false);
         switch (format) {
         case EXIF:
            setDisplay("Uncompressed RGB");
            break;
         case EXIF_YCC:
            setDisplay("Uncompressed YCC");
            break;
         case EXIF_JPEG_411:
            setDisplay("JPEG 4:1:1");
            break;
         case EXIF_JPEG_422:
            setDisplay("JPEG 4:2:2");
            break;
         default:
            throw new UnsupportedOperationException("Invalid file format");
         }
      }

      @Override
      public void setOptions(RasterCodecs codecs, int qFactor) {
         codecs.getOptions().getJpeg().getSave().setQualityFactor(qFactor);
         super.setOptions(codecs, qFactor);
      }
   }

   private class WinBitmapFormat extends SubFormat {
      public WinBitmapFormat(RasterImageFormat format) {
         super("", format, null, null, false, false);
         switch (format) {
         case BMP:
            setDisplay("Uncompressed");
            setBitsPerPixel(new Integer[] { 1, 4, 8, 16, 24, 32 });
            break;
         case BMP_RLE:
            setDisplay("RLE Compressed");
            setBitsPerPixel(new Integer[] { 4, 8});
            break;
         default:
            throw new UnsupportedOperationException("Invalid file format");
         }
      }
   }

   private class OS2Format extends SubFormat {
      public OS2Format(RasterImageFormat format) {
         super("", format, new Integer[] { 1, 4, 8, 16, 24 }, null, false, false);
         switch (format) {
         case OS2:
            setDisplay("Version 1.0");
            break;
         case OS2_2:
            setDisplay("Version 2.0");
            break;
         default:
            throw new UnsupportedOperationException("Invalid file format");
         }
      }
   }

   private class TiffFormat extends SubFormat {
      public TiffFormat(RasterImageFormat format) {
         super("", format, null, null, false, true);
         switch (format) {
         case TIF:
            setDisplay("Uncompressed RGB");
            setBitsPerPixel(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 12, 16, 24, 32, 48, 64 });
            setUseQFactor(false);
            break;
         case TIF_CMYK:
            setDisplay("Uncompressed CMYK");
            setBitsPerPixel(new Integer[] { 24, 32 });
            setUseQFactor(false);
            break;
         case TIF_YCC:
            setDisplay("Uncompressed YCC");
            setBitsPerPixel(new Integer[] { 24 });
            setUseQFactor(false);
            break;
         case TIF_PACKBITS:
            setDisplay("Packbits Compressed RGB");
            setBitsPerPixel(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 16, 24, 32 });
            setUseQFactor(false);
            break;
         case TIF_PACKBITS_CMYK:
            setDisplay("Packbits Compressed CMYK");
            setBitsPerPixel(new Integer[] { 24, 32 });
            setUseQFactor(false);
            break;
         case TIF_PACKBITS_YCC:
            setDisplay("Packbits Compressed YCC");
            setBitsPerPixel(new Integer[] { 24 });
            setUseQFactor(false);
            break;
         case TIFLZW:
            setDisplay("LZW Compressed RGB");
            setBitsPerPixel(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 16, 24, 32, 48, 64 });
            setUseQFactor(false);
            break;
         case TIFLZW_CMYK:
            setDisplay("LZW Compressed CMYK");
            setBitsPerPixel(new Integer[] { 24, 32 });
            setUseQFactor(false);
            break;
         case TIFLZW_YCC:
            setDisplay("LZW Compressed YCC");
            setBitsPerPixel(new Integer[] { 24 });
            setUseQFactor(false);
            break;
         case TIF_JPEG:
            setDisplay("JPEG 4:4:4");
            setBitsPerPixel(new Integer[] { 8, 12, 16, 24 });
            setUseQFactor(false);
            break;
         case TIF_JPEG_411:
            setDisplay("JPEG 4:1:1");
            setBitsPerPixel(new Integer[] { 8, 24 });
            setUseQFactor(true);
            break;
         case TIF_JPEG_422:
            setDisplay("JPEG 4:2:2");
            setBitsPerPixel(new Integer[] { 8, 24 });
            setUseQFactor(true);
            break;
         case TIF_CMP:
            setDisplay("LEAD CMP");
            setBitsPerPixel(new Integer[] { 8, 24 });
            setUseQFactor(true);
            break;
         case TIF_JBIG:
            setDisplay("JBIG");
            setBitsPerPixel(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 12, 16, 24, 32 });
            setUseQFactor(false);
            break;
         case TIF_JBIG2:
            setDisplay("JBIG2");
            setBitsPerPixel(new Integer[] { 1 });
            setUseQFactor(false);
            break;
         case TIF_J2K:
            setDisplay("JPEG 2000");
            setBitsPerPixel(new Integer[] { 8, 12, 16, 24 });
            setUseQFactor(true);
            break;
         case CCITT_GROUP3_1DIM:
            setDisplay("CCITT Group3 1D FAX");
            setBitsPerPixel(new Integer[] { 1 });
            setUseQFactor(false);
            break;
         case CCITT:
            setDisplay("CCITT Group3 1D FAX (Modified, no EOL)");
            setBitsPerPixel(new Integer[] { 1 });
            setUseQFactor(false);
            break;
         case CCITT_GROUP3_2DIM:
            setDisplay("CCITT Group3 2D FAX");
            setBitsPerPixel(new Integer[] { 1 });
            setUseQFactor(false);
            break;
         case CCITT_GROUP4:
            setDisplay("CCITT Group4 FAX");
            setBitsPerPixel(new Integer[] { 1 });
            setUseQFactor(false);
            break;
         default:
            throw new UnsupportedOperationException("Invalid file format");
         }
      }

      @Override
      public void setOptions(RasterCodecs codecs, int qFactor) {
         codecs.getOptions().getJpeg().getSave().setQualityFactor(qFactor);
         super.setOptions(codecs, qFactor);
      }
   }
   
   private class PdfFormat extends SubFormat {
      public PdfFormat(RasterImageFormat format, boolean isLoosless) {
         super("", format, null, null, false, true);
         switch (format) {
            case RAS_PDF:
               setDisplay("Uncompressed");
               setBitsPerPixel(new Integer[] { 1, 2, 4, 8, 24 });
               setUseQFactor(false);
               break;
            case RAS_PDF_G3_1DIM:
               setDisplay("CCITT Group3 1D");
               setBitsPerPixel(new Integer[] { 1 });
               setUseQFactor(false);
               break;
            case RAS_PDF_G3_2DIM:
               setDisplay("CCITT Group3 2D");
               setBitsPerPixel(new Integer[] { 1 });
               setUseQFactor(false);
               break;
            case RAS_PDF_G4:
               setDisplay("CCITT Group4");
               setBitsPerPixel(new Integer[] { 1 });
               setUseQFactor(false);
               break;
            case RAS_PDF_JPEG:
               if (isLoosless) {
                  setDisplay("Lossless JPEG");
                  setBitsPerPixel(new Integer[] { 8 });
                  setUseQFactor(false);
               } else {
                  setDisplay("JPEG YUV 4:4:4");
                  setBitsPerPixel(new Integer[] { 24 });
                  setUseQFactor(true);
               }
               break;
            case RAS_PDF_JPEG_422:
               setDisplay("JPEG YUV 4:2:2");
               setBitsPerPixel(new Integer[] { 24 });
               setUseQFactor(true);
               break;
            case RAS_PDF_JPEG_411:
               setDisplay("JPEG YUV 4:1:1");
               setBitsPerPixel(new Integer[] { 24 });
               setUseQFactor(true);
               break;
            default:
               throw new UnsupportedOperationException("Invalid file format");
         }
      }

      @Override
      public void setOptions(RasterCodecs codecs, int qFactor) {
         codecs.getOptions().getJpeg().getSave().setQualityFactor(qFactor);
         super.setOptions(codecs, qFactor);
      }
   }
}
