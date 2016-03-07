package rocks.rishi.iar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.core.Rect;

import java.util.ArrayList;

import rajawali.RajawaliActivity;

/**
 * Created by prasad on 10/22/15.
 */
public class DoTheRender extends RajawaliActivity{
    private Renderer mRenderer;
    private String text,FirstRectangle;
    private ArrayList<android.graphics.Rect> rect,line_rect;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bitmap bitmap =MainActivity.getBitmap();
        text=intent.getStringExtra("string");
        rect=MainActivity.getRectangles();
        line_rect=MainActivity.getLineRectangles();
        FirstRectangle=intent.getStringExtra("FirstRectangle");
        mRenderer = new Renderer(this,bitmap,text,rect,line_rect);
        mRenderer.setSurfaceView(mSurfaceView);
        super.setRenderer(mRenderer);

        }
    }

