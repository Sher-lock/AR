package rocks.rishi.iar;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

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

    static int pixel,R,G,B;
    String text;
    private static int backColour;
    Camera2D camera2D;
    private float screenH, screenW;
    private static ArrayList<Rect> rectArrayList,line_rect_list;
    //static int textColor;
    private static Bitmap colorbit;

    public static int  getBackgroundColor(Rect rectArrayList){
        pixel=colorbit.getPixel(rectArrayList.left - 4, rectArrayList.top - 4);
        R = (pixel & 0xff0000) >> 16;
        G = (pixel & 0xff00) >> 8;
        B = pixel & 0xff;
        backColour=Color.rgb(R,G,B);
        return Color.rgb(R,G,B);
    }

    public Renderer(Context context,Bitmap bit,String text1) {
        super(context);
        setFrameRate(60);
        camera2D = new Camera2D();
        this.setCamera(this.camera2D);
        newBit=bit;
        colorbit=bit;
        screenH = MainActivity.Get_height();
        screenW = MainActivity.Get_width();
        setCamera(new Camera2D());
        text=text1;
//        rectArrayList=rect;
//        line_rect_list=line_rect;
        mTime = 0;
    }

    private float mTime;

    Plane back;
    protected void initScene() {
        Log.e("pixel is ",""+pixel);
        SimpleMaterial background = new SimpleMaterial();
//        SimpleMaterial foreground = new SimpleMaterial();
        Bitmap bg = newBit;
        back = new Plane(1, 1, 1, 1, 1);
        back.setMaterial(background);
        back.addTexture(mTextureManager.addTexture(bg));
        addChild(back);
    }

    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);
        mTime += .2f;
    }

}
  /*  public Bitmap textAsBitmap(String text, int textsize, int color) {
        Paint paint = new Paint();
        paint.setTextSize(textsize);
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.CENTER);


        Bitmap newbi=Bitmap.createBitmap(newBit, 0,0,line_rect_list.get(0).width(),line_rect_list.get(0).height());   //height,width of rect
        Bitmap image=newbi.copy(Bitmap.Config.ARGB_8888, true);
        image.eraseColor(Color.rgb(R,G,B));
        //image.eraseColor(Color.rgb(255,255,255));

        Canvas canvas = new Canvas(image);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(text, newbi.getWidth()/2, newbi.getHeight()/2, paint);

        return image;
    }*/

   /* public static int colorForText(){
        for(int i=rectArrayList.get(0).left;i<rectArrayList.get(0).right ;i++){
            for(int j=rectArrayList.get(0).top;j<rectArrayList.get(0).bottom;j++){

                pixel=colorbit.getPixel(i,j);
                R = (pixel & 0xff0000) >> 16;
                G = (pixel & 0xff00) >> 8;
                B = pixel & 0xff;
                Log.e("redddddddddddddd",R+" "+G+" "+B);
                //colorbit.setPixel(i,j,Color.BLACK);
                //Log.e("the colour matrix",i+" "+j);
                //Log.e("color",Color.rgb(R,G,B)+" "+backColour);
                if(backColour!=Color.rgb(R,G,B)){
                    Log.e("redddddddddddddd",R+" "+G+" "+B);
                    return Color.rgb(R,G,B);
                }

            }
        }
        return backColour;
    }*/
