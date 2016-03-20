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
    static int textColor;
    private static Bitmap colorbit;
    public Bitmap textAsBitmap(String text, int textsize, int color) {
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
    }
    public static int  getBackgroundColor(){
        pixel=colorbit.getPixel(rectArrayList.get(0).left + 1, rectArrayList.get(0).top + 1);
        R = (pixel & 0xff0000) >> 16;
        G = (pixel & 0xff00) >> 8;
        B = pixel & 0xff;
        backColour=Color.rgb(R,G,B);
        return Color.rgb(R,G,B);
    }

    public Renderer(Context context,Bitmap bit,String text1,ArrayList<Rect> rect,ArrayList<Rect> line_rect) {
        super(context);
        setFrameRate(60);
        camera2D = new Camera2D();
        this.setCamera(this.camera2D);
        newBit=bit;
        colorbit=bit;
        screenH = MainActivity.Get_height();
        screenW = MainActivity.Get_width();


        // pixel = bit.getPixel(Integer.parseInt(Rectangle.substring(6,Rectangle.indexOf(',')))-5,
        //        Integer.parseInt(Rectangle.substring(Rectangle.indexOf(' ')+1,Rectangle.indexOf('-')-1)));
        //Log.e("------RECTANGLE",""+Integer.parseInt(Rectangle.substring(Rectangle.indexOf(' ')+1,
         //       Rectangle.indexOf('-')-1)));


        setCamera(new Camera2D());
//        camera2D.setProjectionMatrix((int)screenW,(int)screenH);
//        camera2D.setUseRotationMatrix(true);
//        camera2D.setRotationMatrix(camera2D.getViewMatrix());
        text=text1;
        rectArrayList=rect;
        line_rect_list=line_rect;
        mTime = 0;
    }
    public static int colorForText(){
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
    }

    private float mTime;

    Plane back;
    protected void initScene() {


        //Log.e("translated==",""+translatedText.toString()+"  "+rectArrayList.toString()+line_rect_list.toString()+(float)(0.001*line_rect_list.get(0).top));
        Log.e("pixel is ",""+pixel);
        SimpleMaterial background = new SimpleMaterial();
        SimpleMaterial foreground = new SimpleMaterial();
        //size of the text is static
        //Bitmap fg = textAsBitmap(translatedText,30,Color.rgb(0,0,0));
        Bitmap bg = newBit;

        //this.camera2D.setZ(50f);

        back = new Plane(1, 1, 1, 1, 1);
        Log.e("--##dim",""+bg.getWidth()+" "+bg.getHeight());
        //front = new Plane(1, 1, 1, 1, 1);

        float x1 = (float)line_rect_list.get(0).left-(screenW/2.0f);
        float y1 = screenH / 2.0f - line_rect_list.get(0).top;
        Log.e("@@@positions", "W=" + screenW + " H=" + "BW="+line_rect_list.get(0).left+" BH="+line_rect_list.get(0).top+" "+ screenH + " " + x1 + " " + y1);
        //front.setScale((float) line_rect_list.get(0).width() / bg.getWidth(), (float) line_rect_list.get(0).height() / bg.getHeight(), 0);
       // front.setPosition(0.001f * (screenW-line_rect_list.get(0).left), 0.001f * ( line_rect_list.get(0).top), 0);
        //front.setScreenCoordinates(0.679f,0.180f,(int)screenW,(int)screenH,0);

        //Log.e("view matrix value ==========",camera2D.getViewMatrix().toString());
        //Log.e("projection matrix",camera2D.getProjectionMatrix().toString());

        //front.setPosition(0, 0, 0);
        back.setMaterial(background);
        //front.setMaterial(foreground);
        //front.addTexture(mTextureManager.addTexture(fg));
        back.addTexture(mTextureManager.addTexture(bg));
        //addChild(front);
        addChild(back);
    }

    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);
        mTime += .2f;
    }

}