package rocks.rishi.iar;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import org.opencv.imgproc.Imgproc;

import javax.microedition.khronos.opengles.GL10;

import rajawali.Camera2D;
import rajawali.materials.SimpleMaterial;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;

/**
 * Created by prasad on 10/13/15.
 */

public class Renderer extends RajawaliRenderer {
    private Bitmap newBit;
    String text;

    public Bitmap textAsBitmap(String text, int textsize, int color) {
        Paint paint = new Paint();
        paint.setTextSize(textsize);
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.CENTER);

        Bitmap newbi=Bitmap.createBitmap(newBit,0,0,newBit.getWidth(),newBit.getHeight());
        Bitmap image=newbi.copy(Bitmap.Config.ARGB_8888, true);
        image.eraseColor(Color.rgb(255,255,255));

        Canvas canvas = new Canvas(image);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(text,newBit.getWidth()/2, newBit.getHeight()/2, paint);

        return image;
    }

    public Renderer(Context context,Bitmap bit,String text1) {
        super(context);
        setFrameRate(60);
        newBit=bit;
        setCamera(new Camera2D());
        //  setFrameRate(30);
        text=text1;
       // Log.e("text in render error==",""+text.toString());
        mTime = 0;
    }

    private float mTime;

    Plane back,front;
    protected void initScene() {

        Translate.setClientId("IndiAR");
        Translate.setClientSecret("63KlfJGAQAW8i/q1RWJjm+1qFzgYXQ3gBt7PSjhQqKs=");


        String translatedText = "";

        try {
            translatedText=Translate.execute(text,Language.HINDI,Language.ENGLISH);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("==error==",""+e.toString());

        }


        Log.e("translated==",""+translatedText.toString());

        SimpleMaterial background = new SimpleMaterial();
        SimpleMaterial foreground = new SimpleMaterial();

        //size of the text is static
        Bitmap fg = textAsBitmap(translatedText,40,Color.rgb(0,0,0));
        Bitmap bg = newBit;



        back = new Plane(1, 1, 1, 1, 1);
        front = new Plane(1, 1, 1, 1, 1);

        //currently scaling is static
        front.setScale(.8f, .3f, 0f);

        back.setMaterial(background);
        front.setMaterial(foreground);

        front.addTexture(mTextureManager.addTexture(fg));
        back.addTexture(mTextureManager.addTexture(bg));

        addChild(front);
        addChild(back);
  //      mCamera.setZ(5f);
    }

    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);
        mTime += .2f;
    }

}