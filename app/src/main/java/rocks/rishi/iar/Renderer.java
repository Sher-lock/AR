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
    private String text;
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

    public Renderer(Context context,Bitmap bit,String text) {
        super(context);
        setFrameRate(60);
        newBit=bit;
        setCamera(new Camera2D());
        //  setFrameRate(30);
        this.text=text;
        mTime = 0;
    }

    private float mTime;

    Plane cube;
    protected void initScene() {

        Translate.setClientId("IndiAR"); //Change this
        Translate.setClientSecret("63KlfJGAQAW8i/q1RWJjm+1qFzgYXQ3gBt7PSjhQqKs="); //change


        String translatedText = "";

        try {
            translatedText=Translate.execute(text,Language.HINDI,Language.ENGLISH);
        } catch (Exception e) {
            e.printStackTrace();
        }


        SimpleMaterial material = new SimpleMaterial();
        Bitmap bg = textAsBitmap(translatedText,30,Color.rgb(0,0,0));
        cube = new Plane(1, 1, 1, 1, 1);
        cube.setMaterial(material);
        cube.setScale(1f, 0.8f, 0f);

        cube.addTexture(mTextureManager.addTexture(bg));

        addChild(cube);
        mCamera.setZ(5f);
    }

    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);
        mTime += .2f;
    }

}