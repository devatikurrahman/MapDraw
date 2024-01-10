package com.mycompany.mapdraw;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;

public class PolygonOnMapActivity implements LocationListener {
	
	private LocationManager locationManager;
	private MapController mapController;
	private List mapOverlays;
	private MyLocationOverlay compass;
	private Location _location;
	private List geoPoints;
	private Projection p;
	private PolygonOverlay polygonOverlay;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        geoPoints = new ArrayList();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        PolygonMapView mapView = (PolygonMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapView.setOnLongpressListener(new PolygonMapView.OnLongpressListener() {
			public void onLongpress(MapView view, GeoPoint longpressLocation) {
				addGeoPointToGeoPointList(longpressLocation);
    			
				runOnUiThread(new Runnable() { // update UI
					public void run() {
				    	mapOverlays.add(polygonOverlay);
						
						PolygonMapView mapView = (PolygonMapView) findViewById(R.id.mapview);
						mapView.invalidate();
					}					
				});
			}
		});
        
        mapController = mapView.getController();
        mapController.setZoom(16);
        
        mapOverlays = mapView.getOverlays();
        p = mapView.getProjection();
        compass = new MyLocationOverlay(this, mapView);
        mapOverlays.add(compass);
        
        startListening();
        
        polygonOverlay = new PolygonOverlay(p, geoPoints);
        
    }
    
    private void addGeoPointToGeoPointList(GeoPoint geoPoint) {
    	// in a polygon first position is also the last therefore we add them twice
    	if(geoPoints.size() == 0) {
    		geoPoints.add(geoPoint);
    		geoPoints.add(geoPoint);
    	}
    	else { // otherwise we insert them in between the first and the last
    		geoPoints.add(geoPoints.size()-1, geoPoint);
    	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	compass.enableCompass();
    	
    	startListening();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	compass.disableCompass();
    }
    
    @Override
    protected void onDestroy() {
    	stopListening();
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, 0, 0, "Exit");
    	menu.add(Menu.NONE, 1, 0, "Find me");
    	menu.add(Menu.NONE, 2, 0, "Done");
    	menu.add(Menu.NONE, 3, 0, "Clear");
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case 0:
    		finish();
    		return true;
    	case 1:
    		findMe();
    		return true;	
    	case 2:
    		Toast.makeText(getApplicationContext(), "Saving...", 20).show();
    		return true;
    	case 3:
    		PolygonMapView mapView = (PolygonMapView) findViewById(R.id.mapview);
			if(mapView.getOverlays().size() > 1) {
	    		mapView.getOverlays().remove(1);
				geoPoints.clear();
	    		mapView.invalidate();				
			}
			return true;
    	}
    	
    	return false;
    }
    
    @Override
    public void onBackPressed() {
	finish();
	super.onBackPressed();
    }
    
     /********************************************************************** 
     * helpers for starting/stopping monitoring of GPS changes below  
     **********************************************************************/ 
	private void startListening() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, this);
	}
	
	private void stopListening() {
		if(locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}
	
	private void findMe() {
		if(_location != null) {
			Double lat = _location.getLatitude()*1E6;
			Double lng = _location.getLongitude()*1E6;
			mapController.animateTo(new GeoPoint(lat.intValue(), lng.intValue()));
			
			PolygonMapView mapView = (PolygonMapView) findViewById(R.id.mapview);
			mapView.invalidate();
		}
	}
	
	public void onLocationChanged(Location location) {
		_location = location;
	}
	
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
																										
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
    }
}
