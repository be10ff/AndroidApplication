package ru.tcgeo.application.gilib.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class GIBitmap 
{
	private GIBounds bounds;
	private Bitmap bitmap;
	private int width;
	private int height;
	

	public GIBitmap(GIBounds bounds, int width, int height)
	{
		this.width = width;
		this.height = height;
		System.gc();
		bitmap =  Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
		this.bounds = bounds;
	}
	
	public GIBitmap(GIBounds bounds, Bitmap bitmap)
	{
		
		this.bitmap =  bitmap;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		this.bounds = bounds;
	}
	public void Set(GIBounds bounds, Bitmap bitmap)
	{
		if(this.bitmap != null)
		{
//			bitmap.recycle();
			this.bitmap = bitmap;
		}
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		this.bounds = bounds;
	}
	public boolean Draw(Canvas canvas, GIBounds bounds)
	{
		if(bitmap == null)
		{
			return true;
		}
		if(bitmap.isRecycled())
		{
			return true;
		}
		boolean res = true;
		if(this.bounds.ContainsBounds(bounds))
		{
			res = false;
		}
		if(!this.bounds.Intersects(bounds))
		{
			return true;
		}
		
		int view_width = canvas.getWidth();
		int view_height = canvas.getHeight();
		
		double pixelWidth = bounds.width() / view_width; 
		double pixelHeight = bounds.height() / view_height;
		
		GILonLat LeftTop = this.bounds.TopLeft();
		GILonLat RightBottom = this.bounds.BottomRight();
		
		int left = (int)((LeftTop.lon() - bounds.left())/pixelWidth);
		int top = (int)((bounds.top() - LeftTop.lat())/pixelHeight);
		int right = (int)((RightBottom.lon() - bounds.left())/pixelWidth);
		int bottom = (int)((bounds.top() - RightBottom.lat())/pixelHeight);
		
		// ???
		//TODO re-draw only valid intersection for performance
		/*
 		Rect canvas_rect = new Rect(0, 0, view_width, view_height);
		Rect bitmap_rect = new Rect(left, top, right, bottom);
		//Rect bitmap_rect = new Rect(view_rect);
		//bitmap_rect.offset(left, top);
		Rect intersection_rect_in_canvas = new Rect();
		intersection_rect_in_canvas.setIntersect(canvas_rect, bitmap_rect);
		Rect intersection_rect_in_bitmap = new Rect(intersection_rect_in_canvas);
		intersection_rect_in_bitmap.set(intersection_rect_in_canvas.left - left, intersection_rect_in_canvas.top - top, intersection_rect_in_canvas.right - left + right, intersection_rect_in_canvas.bottom - top + bottom);
		//intersection_rect_in_bitmap.offset(-left, -top);
		//canvas.drawBitmap(bitmap, intersection_rect_in_bitmap, intersection_rect_in_canvas, null);
		 */
		
		canvas.drawBitmap(bitmap, new Rect(0, 0, width, height), new Rect(left, top, right, bottom), null);
		return res;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
}
