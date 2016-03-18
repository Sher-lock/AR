package rocks.rishi.iar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    static Bitmap bitmap;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView tv;
    private TessBaseAPI baseAPI;
    private Uri imageUri;
    private Mat originalMat;
    private Bitmap currentBitmap;
    private ImageView img;
    private static float screenH, screenW;
    private Display display;
    private WindowManager windowManager;

    private static ArrayList<Rect> rect,line_rect;
    //public static String Rectangles;
    private BaseLoaderCallback mOpenCVCallBack = new
            BaseLoaderCallback(this) {
                @Override
                public void onManagerConnected(int status) {
                    switch (status) {
                        case LoaderCallbackInterface.SUCCESS:

                        break;
                        default:
                            super.onManagerConnected(status);
                            break;
                    }
                }
            };
//    static{
//        if (!OpenCVLoader.initDebug()) {
//            // Handle initialization error
//            Log.i(TAG, "OpenCVLoader Failed");
//        } else {
//            Log.i(TAG, "OpenCVLoader Succeeded");
//            System.loadLibrary("CameraVision");
//            System.loadLibrary("opencv_java3");
//        }
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,
                MainActivity.this, mOpenCVCallBack)) {
            Log.e("TEST", "Cannot connect to OpenCV Manager");
        }

        windowManager = (WindowManager) getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        screenW = (float) size.x;
        screenH = (float) size.y;



        tv=(TextView)findViewById(R.id.marathi);
        baseAPI = new TessBaseAPI();
        File externalStorageDirectory = Environment.getExternalStorageDirectory();

        //to increase the accuracy of tesseract OEM_TESSERACT_CUBE_COMBINED is used
<<<<<<< HEAD
        0baseAPI.init("/storage/9016-4EF8/tesseract/", "hin",TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
       //baseAPI.init("/storage/sdcard1/tesseract/", "hin",TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
=======
        baseAPI.init("/storage/9016-4EF8/tesseract/", "hin",TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
      // baseAPI.init("/storage/sdcard1/tesseract/", "hin",TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
>>>>>>> 025e6a86c77d856e27359989e73886ce7d1ef8bd
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
                String filename = System.currentTimeMillis() + ".jpg";

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, filename);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });
        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inspectFromBitmap();
            }
        });
        img=(ImageView)findViewById(R.id.inter);
    }

    public class Preprocessing extends AsyncTask<Void,Void,Void>{
        protected Void doInBackground(Void... urls)
        {
            preProcess();
            return null;
        }
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


    private void inspectFromBitmap() {

        Log.e("in Mainactivity", "inspect from bitmap");

        //this tells how much processing the tesseract will have to perform berfore recognizing the text
        baseAPI.setPageSegMode(100);//100
        baseAPI.setPageSegMode(3);
        baseAPI.setImage(currentBitmap);
        tv.setText(baseAPI.getUTF8Text());
        Log.e("this is the text",""+tv.getText().toString());
        Log.d("HINDI=====>>", tv.getText().toString());
//
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);

        Intent renderIntent=new Intent(MainActivity.this,DoTheRender.class);
        renderIntent.putExtra("string", tv.getText().toString());
        rect=baseAPI.getWords().getBoxRects();
        line_rect=baseAPI.getTextlines().getBoxRects();
        //Rectangles = baseAPI.getTextlines().getBoxRects().toString();
        Log.e("---RECTANGLES ARE ", "" + rect.toString());
        Log.e("---Line Rectangles ARE ", "" + line_rect.toString());

        //renderIntent.putExtra("Rectangles",Rectangles);


       // renderIntent.putStringArrayListExtra("Rectangles",baseAPI.getWords().getBoxRects());
        renderIntent.putExtra("FirstRectangle",baseAPI.getWords().getBoxRects().get(0).toString());
        startActivity(renderIntent);
    }

    public void preProcess(){
        Mat grayMat = new Mat();

        Log.e("in Mainactivity", "in preprocessing filter");
        try {
            //Converting the image to grayscale
            Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
            Log.e("in Mainactivity", "feeling awesome");

            //then we convert the image to binary by using adaptive thresholding
            //Core.multiply(grayMat, new Scalar(100), grayMat);
            Log.e("in Mainactivity", "Here we go");
            Imgproc.threshold(grayMat, grayMat, 50, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C);

            Imgproc.cvtColor(originalMat, grayMat, Imgproc.COLOR_BGR2GRAY);
            Log.e("in Mainactivity", "life is beutiful");
            //then we apply sobel filter to increase the accuracy of tesseract
           // sobel(grayMat);
            //sending the image to tessseract to extract the characters from the image
            Log.e("in Mainactivity", "live and let live");
           // inspectFromBitmap();
            Log.e("in Mainactivity", "Hello world!");
            //Converting Mat back to Bitmap
            Utils.matToBitmap(grayMat, currentBitmap);
            img.setImageBitmap(currentBitmap);
           // inspectFromBitmap();
        }catch(Exception ex){

            Log.e("Error in pre processing",Log.getStackTraceString(ex));
        }
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
        img.setImageBitmap(currentBitmap);

    }
    private void inspect(Uri uri) {
        InputStream is = null;
        try {

            Log.e("in Mainactivity","inspect");

            is = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 0;
            options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
            bitmap = BitmapFactory.decodeStream(is, null, options);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

            bitmap=decoded;

            Bitmap tempBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            originalMat = new Mat(tempBitmap.getHeight(), tempBitmap.getWidth(), CvType.CV_8U);
            Utils.bitmapToMat(tempBitmap, originalMat);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) screenW, (int) screenH, true);
            currentBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            //preProcess();
            try {
                //new Preprocessing().execute();
                preProcess();
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
                    if (imageUri != null) {
                        inspect(imageUri);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
