package rocks.rishi.iar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import java.util.Date;
import java.util.Iterator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import org.opencv.core.Scalar;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.googlecode.tesseract.android.TessBaseAPI;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends ActionBarActivity {
    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    static Bitmap bitmap;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView tv;
    private TessBaseAPI baseAPI;
    private Uri imageUri;
    private Mat originalMat;
    private Bitmap currentBitmap,newBitmap;
    //private ImageView img;
    private static float screenH, screenW;
    private Display display;
    private WindowManager windowManager;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static ArrayList<Rect> rect=new ArrayList<Rect>(),line_rect=new ArrayList<Rect>();
    private static ArrayList<String> listOfTexts=new ArrayList<String>();

    private Uri fileUri; // file url to store image/video
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    //public static String Rectangles;
    private Mat grayMat;
    private Mat heirarchy;
    private Mat nextMat;
    private BaseLoaderCallback mOpenCVCallBack = new
            BaseLoaderCallback(this) {
                @Override
                public void onManagerConnected(int status) {
                    switch (status) {
                        case LoaderCallbackInterface.SUCCESS:
                            grayMat = new Mat();
                            heirarchy=new Mat();
                            nextMat=new Mat();

                            break;
                        default:
                            super.onManagerConnected(status);
                            break;
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,
                MainActivity.this, mOpenCVCallBack)) {
            Log.e("TEST", "Cannot connect to OpenCV Manager");
        }
        getSupportActionBar().hide();
        windowManager = (WindowManager) getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        screenW = (float) size.x;
        screenH = (float) size.y;


        //tv=(TextView)findViewById(R.id.marathi);
        baseAPI = new TessBaseAPI();
        File externalStorageDirectory = Environment.getExternalStorageDirectory();

        //to increase the accuracy of tesseract OEM_TESSERACT_CUBE_COMBINED is used
         //baseAPI.init(externalStorageDirectory+"/tesseract/", "hin",TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
        baseAPI.init("/storage/9016-4EF8/tesseract/", "hin",TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
        //baseAPI.init("/storage/sdcard1/tesseract/", "hin",TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);//,
        Log.e("in Mainactivity", "on create");

        //this if the user chooses a photo from gallery
        findViewById(R.id.choose_from_gallery).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                Log.e("in Mainactivity", "on click event of gallery is called");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });

        //this if the user uses his camera to capture the image
        findViewById(R.id.take_a_photo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*String filename = System.currentTimeMillis() + ".jpg";

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CAMERA);*/
                captureImage();
            }
        });
       /* findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inspectFromBitmap();
            }
        });*/
       // img=(ImageView)findViewById(R.id.inter);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Bitmap getBitmap()
    {
        return bitmap;
    }

    public static ArrayList<Rect> getRectangles(){
        //return Rectangles;
        return rect;
    }

    public static ArrayList<Rect> getLineRectangles(){
        //return Rectangles;
        return line_rect;
    }

    public static float Get_height(){
        //return screen height
        return screenH;
    }

    public static float Get_width(){
        //return screen height
        return screenW;
    }


    public static ArrayList<String> getListOfTexts(){
        return listOfTexts;
    }


    private void inspectFromBitmap() {

        Log.e("in Mainactivity", "inspect from bitmap");

        //this tells how much processing the tesseract will have to perform berfore recognizing the text
        baseAPI.setPageSegMode(100);//100
        baseAPI.setPageSegMode(3);
        baseAPI.setImage(currentBitmap);
        //tv.setText(baseAPI.getUTF8Text());
        //Log.e("this is the text", "" + baseAPI.getUTF8Text());
        //Log.d("HINDI=====>>", tv.getText().toString());
//
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);

        Intent renderIntent=new Intent(MainActivity.this,DoTheRender.class);
        renderIntent.putExtra("string", listOfTexts.toString());


        rect=baseAPI.getWords().getBoxRects();
        line_rect=baseAPI.getTextlines().getBoxRects();
        Log.e("---RECTANGLES ARE ", "" + rect.toString());
        Log.e("---Line Rectangles ARE ", "" + line_rect.toString());
        for (int i = 0; i < line_rect.size(); i++) {
            baseAPI.setImage(currentBitmap);
            baseAPI.setRectangle(line_rect.get(i).left-5, line_rect.get(i).top-5, line_rect.get(i).width()+10, line_rect.get(i).height()+10);
            listOfTexts.add(baseAPI.getUTF8Text());
        }

        Log.e("---RECTANGLES ARE ", "" + listOfTexts.toString());

        startActivity(renderIntent);
    }

    public void preProcess(){
        ArrayList<MatOfPoint> contours;
//        Mat grayMat = new Mat();
//        Mat heirarchy=new Mat();
//        Mat nextMat=new Mat();

        Log.e("in Mainactivity", "in preprocessing filter");
        try {
            //Converting the image to grayscale
            Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
            Log.e("in Mainactivity", "feeling awesome");
            contours=new ArrayList<>();
            nextMat=grayMat;


            Log.e("in Mainactivity", "Here we go");
            //Imgproc.threshold(grayMat, grayMat, 50, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C);

            //Converting Mat back to Bitmap
            //This is the previous mat which is converted to bitmap
            //which provides binarized image
            //Utils.matToBitmap(grayMat, currentBitmap);

            //here we find the contours because if use this method before above method then tesseract
            //does not returns the bounding boxes
            Imgproc.findContours(grayMat, contours, heirarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            //this iterator is used to visit over each contour(region) recognised by contour

            Iterator<MatOfPoint> each=contours.iterator();

            Log.e("contours array list", "" + contours.size());

            //we remove the contours from the contour list which are not required
            //for that we compare the width with the height
            while(each.hasNext()){
                MatOfPoint wrapper =each.next();
                int height=wrapper.height();
                int width=wrapper.width();
                if(width<height){
                    each.remove();
                }
            }
            //after removing the contour not required we draw the contours on a new matrix
            for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
                Imgproc.drawContours(nextMat, contours, contourIdx, new Scalar(0 ,0 , 255), -1);
            }

            //and now if we try to do the same as we wanted to do above it gives error
            //THIS IS THE FUCKING CONTRADICTION WHICH IS NOT ALLOWING THE FURTHER DEVELOPEMENT
            // PLEASE SEE IF YOU CAN FUCK THIS PROBLEM
            //Imgproc.Canny(nextMat,nextMat,50,200);

            Log.e("SIZE", "HI");
            Imgproc.threshold(nextMat, nextMat, 30, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C);
            Log.e("SIZE", "HELLO");
            Utils.matToBitmap(nextMat, currentBitmap);
            Log.e("SIZE", "WOW");

            currentBitmap = Bitmap.createScaledBitmap(currentBitmap, (int) screenW, (int) screenH, true);

            Log.e("SIZE", currentBitmap.getHeight()+" "+currentBitmap.getWidth());
            //currentBitmap=Bitmap.createScaledBitmap(currentBitmap, (int) screenW, (int) screenH, true);
            //newBitmap=Bitmap.createScaledBitmap(newBitmap, (int) screenW, (int) screenH, true);
            //currentBitmap=newBitmap;

        }catch(Exception ex){

            Log.e("Error in pre processing",Log.getStackTraceString(ex));
        }
    }

    //for deskewing the image
    Mat deskew(Mat src, double angle) {
        Point center = new Point(src.width()/2, src.height()/2);
        Mat rotImage = Imgproc.getRotationMatrix2D(center, angle, 1.0);
        //1.0 means 100 % scale
        Size size = new Size(src.width(), src.height());
        Imgproc.warpAffine(src, src, rotImage, size, Imgproc.INTER_LINEAR + Imgproc.CV_WARP_FILL_OUTLIERS);
        return src;
    }


    //sobel
    public void sobel(Mat temp){
        Mat grayMat = temp;
        Mat sobel = new Mat(); //Mat to store the result
        //Mat to store gradient and absolute gradient respectively
        Mat grad_x = new Mat();
        Mat abs_grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_y = new Mat();

        //Calculating gradient in horizontal direction
        Imgproc.Sobel(grayMat, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0);
        //Calculating gradient in vertical direction
        Imgproc.Sobel(grayMat, grad_y, CvType.CV_16S, 0, 1, 3, 1, 0);
        //Calculating absolute value of gradients in both the direction
        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);
        //Calculating the resultant gradient
        Core.addWeighted(abs_grad_x, 0.5,
                abs_grad_y, 0.5, 1, sobel);
//Converting Mat back to Bitmap
        Utils.matToBitmap(sobel, currentBitmap);
       // img.setImageBitmap(currentBitmap);

    }
    private void inspect(Uri uri) {
        InputStream is = null;
        try {

            Log.e("in Mainactivity","inspect");

            is = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inPreferredConfig = Bitmap.Config.RGB_565;
            // options.inSampleSize = 2;
            options.inDither = true;
            options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
            bitmap = BitmapFactory.decodeStream(is, null, options);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

            bitmap=decoded;

            Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            originalMat = new Mat(tempBitmap.getHeight(), tempBitmap.getWidth(), CvType.CV_8U);
            Utils.bitmapToMat(tempBitmap, originalMat);
            currentBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) screenW, (int) screenH, true);

            try {
                //preProcess();
                currentBitmap = Bitmap.createScaledBitmap(currentBitmap, (int) screenW, (int) screenH, true);
            }
            catch(Exception ex){
                Log.e("YOUR ERROR IS\n\n\n\n\n\n ",Log.getStackTraceString(ex));
            }
            inspectFromBitmap();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    //do the operation after selecting the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    inspect(data.getData());
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (fileUri != null) {
                        inspect(fileUri);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
