package com.testdynamicviews;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;

public class TestDynamicActivity extends Activity {

    private static final String TAG = TestDynamicActivity.class.getSimpleName();

    private LinearLayout mRootView;
    private LinearLayout mBigView;
    private Button mButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootView = (LinearLayout)findViewById(R.id.rootView);
        mBigView = (LinearLayout)findViewById(R.id.containerBigView);
        mButton = (Button)findViewById(R.id.button);

        ViewTreeObserver vto = mRootView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListenerImpl());
    }

    /*private Pair<Integer,Integer> measureButton(){
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.EXACTLY);
        mButton.measure(widthMeasureSpec, heightMeasureSpec);
        int h = mButton.getMeasuredHeight();
        int w = mButton.getMeasuredWidth();
        Log.d(TAG, "height of button is :"+h+", width is :"+w);
        return new Pair<Integer, Integer>(w,h);
    } */

    private int measureRequiredHeight(LinearLayout rootView, int childFrom, int childTo) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.EXACTLY);
        int cumulativeHeight = 0;
        View childView;
        for(int i = childFrom; i < childTo; i++) {
            childView  = rootView.getChildAt(i);
            childView.measure(widthMeasureSpec, heightMeasureSpec);
            int height = mButton.getMeasuredHeight();
            Log.d(TAG, "height of view "+i+" is :"+height);
            cumulativeHeight += height;
        }
        return cumulativeHeight;
    }

    private class OnGlobalLayoutListenerImpl implements ViewTreeObserver.OnGlobalLayoutListener {



        private void resizeBigView(){

            int indexOfBigView = mRootView.indexOfChild(mBigView);
            int totalNumberOFChildren = mRootView.getChildCount();

            int heightOfButton = measureRequiredHeight(mRootView, indexOfBigView + 1, totalNumberOFChildren);


            int bigViewOriginalHeight = mBigView.getHeight();
            int bigViewBottom = mBigView.getBottom();

            int rootViewHeight = mRootView.getHeight();

            int pixelsToRemove = 0;
            //if big view covers the button (even partially!)
            if(bigViewBottom > rootViewHeight - heightOfButton) {
                pixelsToRemove = bigViewBottom - (rootViewHeight - heightOfButton);
                Log.d(TAG, "pixels to remove: "+pixelsToRemove);
            }

            //setup view with the same height as before but changing height from match_parent to precise value
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mBigView.getLayoutParams();
            params.height = bigViewOriginalHeight - pixelsToRemove;
            mBigView.setLayoutParams(params);
        }

        @Override
        public void onGlobalLayout() {

            resizeBigView();


            if (android.os.Build.VERSION.SDK_INT < 16) // Use old, deprecated method
                mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            else // Use new method
                mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);




        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_dynamic, menu);
        return true;
    }
    
}
