package rocks.rishi.iar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.opencv.core.Rect;

import java.util.ArrayList;

import rajawali.RajawaliActivity;

/**
 * Created by prasad on 10/22/15.
 */
public class DoTheRender extends RajawaliActivity{

    //to do the rendering stuff
    private Renderer mRenderer;
    private String text,FirstRectangle;
    private ArrayList<android.graphics.Rect> rect,line_rect;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        Intent intent = getIntent();
        //customView=(RelativeLayout)findViewById(R.id.cust_view);
        Bitmap bitmap =MainActivity.getBitmap();
        text=intent.getStringExtra("string");
        rect=MainActivity.getRectangles();
        line_rect=MainActivity.getLineRectangles();
        FirstRectangle=intent.getStringExtra("FirstRectangle");
        mRenderer = new Renderer(this,bitmap,text,rect,line_rect);
        mRenderer.setSurfaceView(mSurfaceView);
        //mLayout.addView(customView);

//        LayoutInflater inflater = getLayoutInflater();
//        getWindow().addContentView(inflater.inflate(R.layout.custom_view, null),
//                new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.FILL_PARENT,
//                        ViewGroup.LayoutParams.FILL_PARENT));

        super.setRenderer(mRenderer);

        //here we create a child view and add it on the parent view which already contains surface view
        //as seen in rajawali activity which has its own frame layout which confines the surface view
        //so the rendering stuff can still be carried out on surface view while we make multiple child element
        //and it to the parent element
        View child = getLayoutInflater().inflate(R.layout.custom_view, null);
        mLayout.addView(child);

    }
    }

