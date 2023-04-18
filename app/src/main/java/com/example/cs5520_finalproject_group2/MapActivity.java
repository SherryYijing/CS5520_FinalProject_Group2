package com.example.cs5520_finalproject_group2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MyLocationNewOverlay myLocationOverlay;
    private MapView map = null;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

//        Context ctx = getApplicationContext();
//        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_map);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        map = (MapView) findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Don't repeat map
        map.setHorizontalMapRepetitionEnabled(false);
        map.setVerticalMapRepetitionEnabled(false);

        // Limit map don't go forward screen size
        map.setScrollableAreaLimitLatitude(MapView.getTileSystem().getMaxLatitude(), MapView.getTileSystem().getMinLatitude(), 0);
        map.setScrollableAreaLimitLongitude(MapView.getTileSystem().getMinLongitude(), MapView.getTileSystem().getMaxLongitude(), 0);

//        double DEFAULT_ZOOM = 3;
//        map.getController().setZoom(DEFAULT_ZOOM);
//        map.setMinZoomLevel(DEFAULT_ZOOM);
//        GeoPoint centerPoint = new GeoPoint(42.3398, -71.0892);
//        map.getController().setCenter(centerPoint);
//        map.getController().setZoom(18);
        map.setMultiTouchControls(true);

        GeoPoint centerPoint = new GeoPoint(42.3398, -71.0892);
        IMapController mapController = map.getController();
        mapController.setZoom(17);
//        mapController.setCenter(centerPoint);


//        List<GeoPoint> geoPoints = new ArrayList<>();
//        // trip.json provided from professor.
//        String jsonfromTrip = "";
//        InputStream inputStream =
//                getResources().openRawResource(R.raw.trip);
//        try {
//            byte[] buffer = new byte[inputStream.available()];
//            while(inputStream.read(buffer) != -1);
//            jsonfromTrip = new String(buffer);
//            JSONObject rootJSON = null;
//            try {
//                rootJSON = new JSONObject(jsonfromTrip);
//                JSONArray arrayJSON = rootJSON.getJSONArray("locations");
//                for (int i = 0; i < arrayJSON.length(); i++) {
//                    JSONObject itemJSON = arrayJSON.getJSONObject(i);
//                    double latitude = itemJSON.getDouble("latitude");
//                    double longitude = itemJSON.getDouble("longitude");
//                    geoPoints.add(new GeoPoint(latitude, longitude));
//                }
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        Polyline line = new Polyline();   //see note below!
//        line.setPoints(geoPoints);
//        map.getOverlayManager().add(line);

//        Polygon polygon = new Polygon();
//        geoPoints.add(geoPoints.get(0));
//        polygon.getFillPaint().setColor(Color.parseColor("#1EFFE70E")); //set fill color
//        polygon.setPoints(geoPoints);
//        polygon.setTitle("Northeastern University");
//        map.getOverlayManager().add(polygon);

        // Navigation
//        this.myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),map);
//        this.myLocationOverlay.enableMyLocation();
        // Create start point.

        // Use getGeocode function to get latitude and longitude.
        getGeocode("Northeastern University");

        Marker startMarker = new Marker(map);
        GeoPoint startPoint = new GeoPoint(42.3385434,-71.0971816);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        map.invalidate();
//        startMarker.setTitle("Start point");

        // Make the center of the map to be the start point.
        mapController.setCenter(startPoint);

        // Create stop point.
        Marker endMarker = new Marker(map);
        GeoPoint endPoint = new GeoPoint(42.3395109,-71.0913559);
        endMarker.setPosition(endPoint);
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(endMarker);
        map.invalidate();
        endMarker.setTitle("End point");

        // Get road between start point and end point.
        RoadManager roadManager = new OSRMRoadManager(this, getPackageName());
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        waypoints.add(endPoint);
        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        map.getOverlays().add(roadOverlay);
        map.invalidate();

        // Mark each step.
        for (int i=0; i < road.mNodes.size(); i++){
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setTitle("Step "+ i);
            map.getOverlays().add(nodeMarker);
        }

        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );
    }

    /**
     * get latitude and longitude of input place.
     * @param input input place.
     * @return item of Address class.
     */
    public Address getGeocode(String input){
        Address address = null;
        try {
            Geocoder geocoder = new Geocoder(getBaseContext());
            // We only need one result.
            List<Address> result = geocoder.getFromLocationName(input, 1);
            if (result.size() > 0) {
                address = result.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }
    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}