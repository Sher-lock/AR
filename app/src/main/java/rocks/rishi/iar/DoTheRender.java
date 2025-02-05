package rocks.rishi.iar;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import java.util.ArrayList;

import rajawali.RajawaliActivity;

/**
 * Created by prasad on 10/22/15.
 */
public class DoTheRender extends RajawaliActivity{

    //to do the rendering stuff
    private Renderer mRenderer;
    private String text;
    private ArrayList<android.graphics.Rect> rect,line_rect;
    //private String translateText;
    private ArrayList<String> listOfText;
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
        Bitmap bitmap =MainActivity.getBitmap();
        text=intent.getStringExtra("string");
        rect=MainActivity.getRectangles();
        line_rect=MainActivity.getLineRectangles();
        listOfText=MainActivity.getListOfTexts();
        Log.e("rect values",rect.toString());
        Log.e("line rect value",line_rect.toString());

        mRenderer = new Renderer(this,bitmap,text);
        mRenderer.setSurfaceView(mSurfaceView);

        super.setRenderer(mRenderer);
        //id for translation
        Log.e("Yo dude"," its working===========");
        Translate.setClientId("IndiAR");
        Translate.setClientSecret("63KlfJGAQAW8i/q1RWJjm+1qFzgYXQ3gBt7PSjhQqKs=");

        //take each rectangle and then perform the operation of rendering
        for(int i=0;i<line_rect.size();i++) {
            new RenderTask(i).execute();
        }


    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        listOfText.clear();
    }
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    private class RenderTask extends AsyncTask<Void, Void,Integer> {
        int rectangleNumber;
        //text view that to be added to the parent view
        private TextView custText;

        // child layout
        private RelativeLayout childRelativeLayout;

        RenderTask(Integer rectNo){
            rectangleNumber=rectNo;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                Log.e("before translation= ", "" + listOfText.toString());
                for(int i=0;i<listOfText.size();i++)
                    listOfText.set(i,Translate.execute(listOfText.get(i), Language.HINDI,Language.ENGLISH));
                Log.e("after translation= ",""+listOfText.toString());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("==error==",""+e.toString());

            }

            return null;
        }
        @Override
        protected void onPostExecute(Integer x){
            //here we create a child view and add it on the parent view which already contains surface view
            //as seen in Rajawali activity which has its own frame layout which confines the surface view
            //so the rendering stuff can still be carried out on surface view while we make multiple child element
            //and it to the parent element
            Log.e("in post execute"," its working===========");
            Log.e("rect ",rect.toString());
            Log.e("line of rect",line_rect.toString());
            Log.e("list of text", listOfText.get(rectangleNumber).toString());

            //here we create a child view and add it on the parent view which already contains surface view
            //as seen in Rajawali activity which has its own frame layout which confines the surface view
            //so the rendering stuff can still be carried out on surface view while we make multiple child element
            //and it to the parent element
            View child = getLayoutInflater().inflate(R.layout.custom_view, null);

            //get the child out the parents custom layout to modify them and then again them
            //to the parent layout

            childRelativeLayout=(RelativeLayout)child.findViewById(R.id.child_rect);
            custText=(TextView)child.findViewById(R.id.cust_text);
            RelativeLayout.LayoutParams rlp=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

            custText.setText(listOfText.get(rectangleNumber));
            custText.setTextSize(pxToDp((line_rect.get(rectangleNumber).bottom - line_rect.get(rectangleNumber).top)));

            rlp.setMargins((line_rect.get(rectangleNumber).left), (line_rect.get(rectangleNumber).top), (line_rect.get(rectangleNumber).right),
                    (line_rect.get(rectangleNumber).bottom));
            //childRelativeLayout.setPadding();
            rlp.setMarginStart((line_rect.get(rectangleNumber).left));

            //childRelativeLayout.getLayoutParams().height=(line_rect.get(0).bottom-line_rect.get(0).top);
            //  childRelativeLayout.getLayoutParams().width=(line_rect.get(0).right-line_rect.get(0).left);
            childRelativeLayout.setBackgroundColor(Renderer.getBackgroundColor(line_rect.get(rectangleNumber)));
            custText.setTextColor(Color.BLACK);

            //Log.e("Renderer back check", Renderer.getBackgroundColor() + "");
            //Log.e("Renderer front check", Renderer.colorForText()+ "");

            if(childRelativeLayout.getLayoutParams().width<(line_rect.get(rectangleNumber).right-line_rect.get(rectangleNumber).left))
                childRelativeLayout.setMinimumWidth(line_rect.get(rectangleNumber).right-line_rect.get(rectangleNumber).left);

            if(childRelativeLayout.getTop()<(line_rect.get(rectangleNumber).top))
                childRelativeLayout.setMinimumHeight((line_rect.get(rectangleNumber).top - childRelativeLayout.getTop()) + line_rect.get(rectangleNumber).bottom - line_rect.get(rectangleNumber).top);


            //childRelativeLayout.setBackgroundColor(Color.BLACK);
            childRelativeLayout.setLayoutParams(rlp);
            child.invalidate();
            mLayout.addView(child);
        }

    }
}

