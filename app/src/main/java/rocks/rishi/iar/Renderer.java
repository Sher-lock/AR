package rocks.rishi.iar;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
    int pixel,R,G,B;
    String text;
    private ArrayList<Rect> rectArrayList;

    public Bitmap textAsBitmap(String text, int textsize, int color) {
        Paint paint = new Paint();
        paint.setTextSize(textsize);
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.CENTER);

        Bitmap newbi=Bitmap.createBitmap(newBit, 0,0,135,28);   //height,width of rect
        Bitmap image=newbi.copy(Bitmap.Config.ARGB_8888, true);
        image.eraseColor(Color.rgb(R,G,B));
        //image.eraseColor(Color.rgb(255,255,255));

        Canvas canvas = new Canvas(image);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(text, newbi.getWidth()/2, newbi.getHeight()/2, paint);

        return image;
    }

    public Renderer(Context context,Bitmap bit,String text1,ArrayList<Rect> rect) {
        super(context);
        setFrameRate(60);
        newBit=bit;
       // pixel = bit.getPixel(Integer.parseInt(Rectangle.substring(6,Rectangle.indexOf(',')))-5,
        //        Integer.parseInt(Rectangle.substring(Rectangle.indexOf(' ')+1,Rectangle.indexOf('-')-1)));
        //Log.e("------RECTANGLE",""+Integer.parseInt(Rectangle.substring(Rectangle.indexOf(' ')+1,
         //       Rectangle.indexOf('-')-1)));
        pixel=bit.getPixel(rect.get(0).left+1,rect.get(0).top+1);
        R = (pixel & 0xff0000) >> 16;
        G = (pixel & 0xff00) >> 8;
        B = pixel & 0xff;
        setCamera(new Camera2D());

        text=text1;
        rectArrayList=rect;

        mTime = 0;
    }

    private float mTime;

    Plane back,front;
    protected void initScene() {

        Translate.setClientId("IndiAR");
        Translate.setClientSecret("63KlfJGAQAW8i/q1RWJjm+1qFzgYXQ3gBt7PSjhQqKs=");


        String translatedText = "";

        try {
            Log.e("before translation= ",""+text);

            translatedText=Translate.execute(text,Language.HINDI,Language.ENGLISH);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("==error==",""+e.toString());

        }

        Log.e("translated==",""+translatedText.toString()+"  "+rectArrayList.toString());
        Log.e("pixel is ",""+pixel);
        SimpleMaterial background = new SimpleMaterial();
        SimpleMaterial foreground = new SimpleMaterial();

        //size of the text is static
        Bitmap fg = textAsBitmap(translatedText,15,Color.rgb(0,0,0));
        Bitmap bg = newBit;

        back = new Plane(0.001f*bg.getWidth(),0.001f*bg.getHeight(), 1, 1);
        //front = new Plane(1, 1, 1, 1, 1);
        front = new Plane(0.135f, .028f, 1, 1);      //height,width of rect

        front.setX(0.018f);
        front.setY(0.106f);
        //currently scaling is static
   //     front.setScale(.8f,.3f, 0f);
//        front.setX(55);
        //front.setPosition(.8f,.3f, 0f);
//        front.setY(116);
//        front.setZ(0);
        //front.setScreenCoordinates(55,116,50,60,0);
        back.setMaterial(background);
        front.setMaterial(foreground);

        front.addTexture(mTextureManager.addTexture(fg));
        back.addTexture(mTextureManager.addTexture(bg));
      //  mCamera.setZ(-5f);
        addChild(front);
        addChild(back);
    }

    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);
        mTime += .2f;
    }

}