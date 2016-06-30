package com.bgs.common;

import android.location.Location;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.map.Graphic;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhufre on 6/30/2016.
 */
public class MapUtils {
    /**
     * update map graphic
     * @param layer
     * @param graphics
     */
    public static void updateMap(final GraphicsLayer layer, final Graphic[] graphics) {
        if ( layer == null ) return;
        //Utility.runOnUIThread(new Runnable() {
        //    @Override
        //    public void run() {
                //check if graphic exist
                int[] oldGraphicIDs = layer.getGraphicIDs();
                List<Integer> oldGraphicIDList = new ArrayList<Integer>(0);
                //sent empty array in case of empty layer
                if ( oldGraphicIDs != null ) {
                    oldGraphicIDList = new ArrayList(Arrays.asList(ArrayUtils.toObject(oldGraphicIDs)));
                }
                Log.d(Constants.TAG, "oldGraphicIDs.size()=" +oldGraphicIDList.size());
                //tampung dalam list

                Graphic oldGraphic = null;
                Graphic newGraphic = null;
                boolean exist;

                //store new and old ids
                int[] newAndOldIDs = new int[graphics.length];

                for(int i = 0; i < graphics.length; i++) {
                    newGraphic = graphics[i];
                    //remove existing
                    for(int x = oldGraphicIDList.size()-1; x >= 0; x--) {
                        int id  = oldGraphicIDList.get(x);
                        //Log.d(Constants.TAG, String.format("x=%s => id=%s", x, id));
                        oldGraphic = layer.getGraphic(id);
                        if ( oldGraphic != null ) {
                            if ((int) oldGraphic.getAttributeValue("id_loc") == (int) newGraphic.getAttributeValue("id_loc")) {
                                layer.updateGraphic(id, newGraphic);
                                //layer.removeGraphic(id);
                                newAndOldIDs[i] = id;
                                //remve from list it will decrease old id looping
                                oldGraphicIDList.remove(x);
                                break;
                            }
                        }
                    }
                    //add for unexisting
                    newAndOldIDs[i] = layer.addGraphic(newGraphic);
                }

                //remove old and not update graphics within radius
                /*
                if ( oldGraphicIDs != null ) {
                    for (int oldID : oldGraphicIDs) {
                        //check for existing
                        exist = false;
                        for (int newID : newAndOldIDs) {
                            if (oldID == newID) {
                                exist = true;
                                break;
                            }
                        }
                        //not exist in new radius remove
                        if (!exist) layer.removeGraphic(oldID);
                    }
                }*/

        //    }
        //});

    }

    /**
     * Zoom to location using a specific size of extent.
     *
     * @param loc the location to center the MapView at
     */
    public static void zoomToLocation(MapView mapView, SpatialReference reference ,Location loc, double zoomBy) {
        Point mapPoint = MapUtils.getAsPoint(reference, loc);
        Unit mapUnit = reference.getUnit();
        double zoomFactor = Unit.convertUnits(zoomBy, Unit.create(LinearUnit.Code.MILE_US), mapUnit);
        Envelope zoomExtent = new Envelope(mapPoint, zoomFactor, zoomFactor);
        mapView.setExtent(zoomExtent);
    }

    public Point onSingleTaps(MapView mapView, float x, float y) {
        Point pnt = (Point) GeometryEngine.project(mapView.toMapPoint(x, y), mapView.getSpatialReference(), SpatialReference.create(4326));
        return pnt;
    }

    public static Point getAsPoint(SpatialReference reference, Location loc) {
        Point wgsPoint = new Point(loc.getLongitude(), loc.getLatitude());
        return (Point) GeometryEngine.project(wgsPoint, SpatialReference.create(4326),
                reference);
    }

}
