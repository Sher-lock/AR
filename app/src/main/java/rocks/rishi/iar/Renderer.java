package rocks.rishi.iar;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

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
    Camera2D camera2D;
    private float screenH, screenW;
    private Display display;
    private WindowManager windowManager;
    private ArrayList<Rect> rectArrayList,line_rect_list;

    public Bitmap textAsBitmap(String text, int textsize, int color) {
        Paint paint = new Paint();
        paint.setTextSize(textsize);
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.CENTER);

        Bitmap newbi=Bitmap.createBitmap(newBit, 0,0,line_rect_list.get(0).width(),line_rect_list.get(0).height());   //height,width of rect
        Bitmap image=newbi.copy(Bitmap.Config.ARGB_8888, true);
        //image.eraseColor(Color.rgb(R,G,B));
        image.eraseColor(Color.rgb(255,255,255));

        Canvas canvas = new Canvas(image);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(text, newbi.getWidth()/2, newbi.getHeight()/2, paint);

        return image;
    }

    public Renderer(Context context,Bitmap bit,String text1,ArrayList<Rect> rect,ArrayList<Rect> line_rect) {
        super(context);
        setFrameRate(60);
        camera2D = new Camera2D();
        this.setCamera(this.camera2D);
        newBit=bit;
        windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        screenW = (float) size.x;
        screenH = (float) size.y;

       // pixel = bit.getPixel(Integer.parseInt(Rectangle.substring(6,Rectangle.indexOf(',')))-5,
        //        Integer.parseInt(Rectangle.substring(Rectangle.indexOf(' ')+1,Rectangle.indexOf('-')-1)));
        //Log.e("------RECTANGLE",""+Integer.parseInt(Rectangle.substring(Rectangle.indexOf(' ')+1,
         //       Rectangle.indexOf('-')-1)));

        pixel=bit.getPixel(rect.get(0).left + 1, rect.get(0).top + 1);
        R = (pixel & 0xff0000) >> 16;
        G = (pixel & 0xff00) >> 8;
        B = pixel & 0xff;

        setCamera(new Camera2D());
//        camera2D.setProjectionMatrix((int)screenW,(int)screenH);
//        camera2D.setUseRotationMatrix(true);
//        camera2D.setRotationMatrix(camera2D.getViewMatrix());
        text=text1;
        rectArrayList=rect;
        line_rect_list=line_rect;
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

        Log.e("translated==",""+translatedText.toString()+"  "+rectArrayList.toString()+line_rect_list.toString()+(float)(0.001*line_rect_list.get(0).top));
        Log.e("pixel is ",""+pixel);
        SimpleMaterial background = new SimpleMaterial();
        SimpleMaterial foreground = new SimpleMaterial();
        //size of the text is static
        Bitmap fg = textAsBitmap(translatedText,30,Color.rgb(0,0,0));
        Bitmap bg = newBit;

        //this.camera2D.setZ(50f);

        back = new Plane(1, 1, 1, 1, 1);
        Log.e("--##dim",""+bg.getWidth()+" "+bg.getHeight());
        front = new Plane(1, 1, 1, 1, 1);

        float x1 = (float)line_rect_list.get(0).left-(screenW/2.0f);
        float y1 = (float)screenH / 2.0f - line_rect_list.get(0).top;
        Log.e("@@@positions", "W=" + screenW + " H=" + "BW="+line_rect_list.get(0).left+" BH="+line_rect_list.get(0).top+" "+ screenH + " " + x1 + " " + y1);
        front.setScale((float) line_rect_list.get(0).width() / bg.getWidth(), (float) line_rect_list.get(0).height() / bg.getHeight(), 0);
       // front.setPosition(0.001f * (screenW-line_rect_list.get(0).left), 0.001f * ( line_rect_list.get(0).top), 0);
        //front.setScreenCoordinates(0.679f,0.180f,(int)screenW,(int)screenH,0);

        //Log.e("view matrix value ==========",camera2D.getViewMatrix().toString());
        //Log.e("projection matrix",camera2D.getProjectionMatrix().toString());

        front.setPosition(0, 0, 0);
        back.setMaterial(background);
        front.setMaterial(foreground);
        front.addTexture(mTextureManager.addTexture(fg));
        back.addTexture(mTextureManager.addTexture(bg));
        addChild(front);
        addChild(back);
    }

    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);
        mTime += .2f;
    }

}