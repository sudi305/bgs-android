package com.bgs.common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhufre on 6/20/2016.
 */
public class GpsUtils {
    public static final Location DUMMY_LOCATION = new Location(LocationManager.GPS_PROVIDER){{
        setLatitude(-6.212601);//-6.212601
        setLongitude(106.617825);//106.617825
    }};

    public static final long TWO_MINUTES = TimeUnit.MINUTES.toMillis(2);
    public static final long LOCATION_UPDATE_RATE = TimeUnit.SECONDS.toMillis(3);

    //GPS METHOD
    /**
     * @return the last know best location
     */
    @SuppressWarnings("MissingPermission")
    public static Location getLastBestLocation(LocationManager locationManager) {

        if ( locationManager == null ) return null;

        Location locationGPS = null;
        Location locationNet = null;
        //if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        //        || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        Log.e(Constants.TAG, "GET LOCATION");
        locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.e(Constants.TAG, "locationGPS => " + locationGPS);
        locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.e(Constants.TAG, "locationNet => " + locationNet);
        //}
        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) { NetLocationTime = locationNet.getTime(); }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }


    /** Determines whether one location reading is better than the current location fix
     * @param location  The new location that you want to evaluate
     * @param currentBestLocation  The current location fix, to which you want to compare the new one.
     */
    public static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        //check if lat-long equal
        if ( location.getLatitude() == currentBestLocation.getLatitude() && location.getLongitude() == currentBestLocation.getLongitude() )
            return false;

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location,
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        Log.d(Constants.TAG_GPS, String.format("location.getAccuracy()=%s, currentBestLocation.getAccuracy()=%s",location.getAccuracy(), currentBestLocation.getAccuracy()));
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            Log.d(Constants.TAG_GPS, "new location isMoreAccurate ( < 0 )");
            return true;
        } else if (isNewer && !isLessAccurate) {
            Log.d(Constants.TAG_GPS, "new location isNewer && !isLessAccurate ( > 0 )");
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            Log.d(Constants.TAG_GPS, "new location isNewer && !isSignificantlyLessAccurate ( > 200 )");
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


}
