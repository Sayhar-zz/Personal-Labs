package com.saharmassachi.droid.compass;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PointerView extends View{
	private static final String TAG = "pointerview";
	Bitmap borig;
	Bitmap b2;
	Bitmap brot;
	int w;
	int h;
	float rot;
	Rect r;


	
	static private int IMG = R.drawable.arrow;
	
	public PointerView(Context context, AttributeSet attrs){
		super(context, attrs);
		init();
	}
	
	public PointerView(Context context){
		super(context);
		init();
	}
	
	private void init(){
		borig = BitmapFactory.decodeResource(getResources(), IMG);
		rot = 0;
		
		setFocusable(true);
	}
	
	
	@Override 
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);	
		b2 = Bitmap.createScaledBitmap(borig, w/4, h/4, false);
	}
	
	@Override 
	protected void onDraw(Canvas canvas) {
		// Draw the background...
		Paint background = new Paint();
		
		//The compass will be on a while background.
		//We publish the bitmap on the canvas, then rotate the canvas.
		background.setColor(Color.WHITE);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		getResources().getDrawable(R.drawable.arrow).draw(canvas);
		
		
		//w and h are the width and height of the arrow
		w = b2.getScaledWidth(canvas);
		h = b2.getScaledHeight(canvas);
		
		//cw and ch are the width and height of the view
		int cw = getWidth();
		int ch = getHeight();
		Matrix mtx = new Matrix();
		
		
		//don't need this anymore - relic from when we rotated the bitmap instead of the canvas.
		//brot = Bitmap.createBitmap(b2, 0, 0, w, h, mtx, false);
		
		
		//r is the rectangle we place the arrow in. It is w wide, h high, and centered. 
		r = new Rect((cw-w)/2, (ch - h)/2 , (cw+w)/2, (ch + h)/2 );
		
		//rotate the canvas about it's center.
		canvas.rotate(rot, cw/2,ch/2);
		canvas.drawBitmap(b2, null, r, null);
		
	}
	
	
	public void rotate(float val){
		//when CompassActivity calls rotate, change rot (which is the instruction for how much to rotate) Then invalidate.
		rot = val % 360;
		if(w>0){
			invalidate();	
		}
		
		
	}
}
