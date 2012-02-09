package com.saharmassachi.labs.newfellow;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class ItemizedContactOverlay extends ItemizedOverlay<OverlayItem> {
	
	Context mContext;
	private ArrayList<OverlayItem> mOverlays;
	
	public ItemizedContactOverlay(Drawable defaultMarker) {
		  super(boundCenterBottom(defaultMarker));
		  mOverlays = new ArrayList<OverlayItem>();
		}

	public ItemizedContactOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		mOverlays = new ArrayList<OverlayItem>();
	}

	

	public void addToOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	
	
	/**
	 * method to empty out overlay
	 */
	protected void emptyOverlay(){
		mOverlays.clear();
		setLastFocusedIndex(-1);
		populate();
	}
	
	
	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}

	@Override
	public int size() {
	  return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  AlertDialog.Builder dialogB = new AlertDialog.Builder(mContext);
	  //dialog.setTitle("hello");
	  dialogB.setTitle(item.getTitle());
	  dialogB.setMessage(item.getSnippet());
	  Dialog dialog = dialogB.create();
	  dialog.setCanceledOnTouchOutside(true);
	  dialog.show();
	  return true;
	}
	
	
	
}
