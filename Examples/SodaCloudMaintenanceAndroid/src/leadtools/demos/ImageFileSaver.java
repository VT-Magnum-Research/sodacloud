package leadtools.demos;

import leadtools.LeadStreamFactory;
import leadtools.RasterImage;
import leadtools.RasterImageFormat;
import leadtools.codecs.CodecsSavePageMode;
import leadtools.codecs.RasterCodecs;
import leadtools.demos.SaveFormatDialog.OnSaveFormat;
import android.content.Context;

public class ImageFileSaver implements OnSaveFormat, SaveFileDialog.OnFileSelectedListener {
   
   private RasterCodecs mCodecs;
   private RasterImage mImage;
   private RasterImageFormat mFormat;
   private int mBitsPerPixel;
   
   private SaveFormatDialog mImageFormatDlg;
   private SaveFileDialog mSaveFileDlg;

   public ImageFileSaver(Context context) {
      mCodecs = new RasterCodecs(Utils.getSharedLibsPath(context));

      mImageFormatDlg = new SaveFormatDialog(context, mCodecs, this);
      mSaveFileDlg = new SaveFileDialog(context, Utils.getSupportedImagesFormatFilter(), this);
   }
   
   public void save(RasterImage image) {
      mImage = image;
      
      
      
      mImageFormatDlg.show();
   }
   
   public void onSaveFormat(RasterCodecs codecs, RasterImageFormat format, int bitsPerPixel) {
      mCodecs = codecs;
      mFormat = format;
      mBitsPerPixel = bitsPerPixel;
      mSaveFileDlg.show();
   }

   public void onFileSelected(String fileName) {
      try {
         String extension = getExtension(mFormat);
         if(!fileName.endsWith(extension))
            fileName += extension;

         mCodecs.save(mImage, LeadStreamFactory.create(fileName), mFormat, mBitsPerPixel, 1, -1, 1, CodecsSavePageMode.APPEND);
      }
      catch (Exception ex) {
         Messager.showError(mSaveFileDlg.getContext(), ex.getMessage(), "Error saving file");
      }
   }
   
   
   private String getExtension(RasterImageFormat format) {
      String strExt = "";
      switch (format) {
      case CMP:
         strExt = ".cmp";
         break;
      case JPEG:
      case JPEG_411:
      case JPEG_422:
      case JPEG_LAB:
      case JPEG_LAB_411:
      case JPEG_LAB_422:
      case JPEG_RGB:
         strExt = ".jpg";
         break;
      case RAS_PDF:
      case RAS_PDF_JPEG:
      case RAS_PDF_JPEG_411:
      case RAS_PDF_JPEG_422:
      case RAS_PDF_G3_1DIM:
      case RAS_PDF_G3_2DIM:
      case RAS_PDF_G4:
         strExt = ".pdf";
         break;
      case FAX_G3_1DIM:
      case FAX_G3_1DIM_NOEOL:
      case FAX_G3_2DIM:
      case FAX_G4:
         strExt = ".fax";
         break;
      case TIF:
      case TIF_CMP:
      case TIF_CMYK:
      case TIF_J2K:
      case TIF_JBIG:
      case TIF_JBIG2:
      case TIF_JPEG:
      case TIF_JPEG_411:
      case TIF_JPEG_422:
      case TIFLZW:
      case TIFLZW_CMYK:
      case TIFLZW_YCC:
      case TIF_PACKBITS:
      case TIF_PACKBITS_CMYK:
      case TIF_PACKBITS_YCC:
      case CCITT:
      case CCITT_GROUP3_1DIM:
      case CCITT_GROUP3_2DIM:
      case CCITT_GROUP4:
      case TIF_YCC:
      case GEO_TIFF:
         strExt = ".tif";
         break;
      case EXIF:
      case EXIF_JPEG_411:
      case EXIF_JPEG_422:
      case EXIF_YCC:
         strExt = ".jpg";
         break;
      case GIF:
         strExt = ".gif";
         break;
      case J2K:
         strExt = ".j2k";
         break;
      case JP2:
         strExt = ".jp2";
         break;
      case JBIG:
         strExt = ".jbg";
         break;
      case JBIG2:
         strExt = ".jb2";
         break;
      case BMP:
      case BMP_RLE:
      case OS2:
      case OS2_2:
         strExt = ".bmp";
         break;
      case JXR:
      case JXR_420:
      case JXR_422:
      case JXR_GRAY:
         strExt = ".jxr";
         break;
      case JLS:
      case JLS_LINE:
      case JLS_SAMPLE:
         strExt = ".jls";
         break;
      case PNG:
         strExt = ".png";
         break;
      case PSD:
         strExt = ".psd";
         break;
      default:
         strExt = "";
         break;
      }
      
      return strExt;
   }
}
