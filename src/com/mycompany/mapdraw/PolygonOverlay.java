package com.mycompany.mapdraw;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PolygonOverlay extends Overlay {
	private Projection _projection;  
	private List _geoPoints;
	
	public PolygonOverlay(Projection projection, List geoPoints) {
		_projection = projection;
		_geoPoints = geoPoints;
	}
	
	public void draw(Canvas canvas, MapView mapview, boolean shadow) {
		super.draw(canvas, mapview, shadow);
		
		if(!shadow) {
			Paint mPaint = new Paint(); 
	        mPaint.setDither(true); 
	        mPaint.setColor(Color.RED); 
	        mPaint.setStyle(Paint.Style.FILL_AND_STROKE); 
	        mPaint.setStrokeJoin(Paint.Join.ROUND); 
	        mPaint.setStrokeCap(Paint.Cap.ROUND); 
	        mPaint.setStrokeWidth(4); 

	        Path path = new Path();
	        
	        Point lastPoint = new Point();
	        
	        if(_geoPoints.size() > 1) {
	        	for(int i = 0; i < _geoPoints.size(); i++) {
	        		GeoPoint geoP = (GeoPoint) _geoPoints.get(i);
	        		Point point = new Point();
	        		_projection.toPixels(geoP, point);
	        		
	        		if(i == 0) { // if it is the first just add a circle
	        			path.moveTo(point.x, point.y);
	        			path.addCircle(point.x, point.y, 3, Direction.CW);
	        			lastPoint = point;
	        		}
	        		else {
	        			_projection.toPixels((GeoPoint) _geoPoints.get(i-1), lastPoint);
	        			path.moveTo(lastPoint.x, lastPoint.y);
	        			path.lineTo(point.x, point.y); 
	        		}
	        	}
	        	
	        	canvas.drawPath(path, mPaint); 
	             }
	       }
	}
}
