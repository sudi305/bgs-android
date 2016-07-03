package com.bgs.common;

import android.content.ContentValues;
import android.location.Location;
import android.util.Log;
import android.util.Pair;

import com.bgs.domain.chat.repository.IMessageRepository;
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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhufre on 6/30/2016.
 */
public class MapUtils {
    /**
     * update map graphic
     * @param layer
     * @param graphics
     */
    @Deprecated
    public static void updateMap(final GraphicsLayer layer, final Graphic[] graphics) {
        if ( layer == null ) return;
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
    }

    /**
     * update map
     * @param layer
     * @param graphicMap
     */
    public static void updateMap(final GraphicsLayer layer, final Map<String, ArrayList> graphicMap) {
        if ( layer == null ) return;
        //add
        final ArrayList<Graphic> newList = graphicMap.get("new");
        for(Graphic g : newList ) {
            layer.addGraphic(g);
        }

        //update
        final ArrayList<Pair<Integer, Graphic>> updateList = graphicMap.get("update");
        for(Pair<Integer, Graphic> pair : updateList ) {
            layer.updateGraphic(pair.first, pair.second);
        }
        //remove
        final ArrayList<Integer> removeList = graphicMap.get("remove");
        for(Integer i : removeList) {
            layer.removeGraphic(i);
        }

    }

    /**
     *
     * @param layer
     * @param graphics
     * @return hashmap contain 3 type of list with type key: new, update & remove
     */
    public static Map<String, ArrayList> analyzeGraphics(final GraphicsLayer layer, final Graphic[] graphics) {
        Map<String, ArrayList> graphicMap = new HashMap<String, ArrayList>(){{
            put("new", new ArrayList<Graphic>());
            put("update", new ArrayList<Pair<Integer, Graphic>>());
            put("remove", new ArrayList<Integer>());
        }};

        if ( layer == null ) return graphicMap;
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
        final ArrayList<Graphic> newList = new ArrayList<Graphic>();
        final ArrayList<Pair<Integer, Graphic>> updateList = new ArrayList<Pair<Integer, Graphic>>();
        final ArrayList<Integer> removeList = new ArrayList<Integer>();
        boolean exist;
        Map<Integer, Graphic> updateMap;
        for(int i = 0; i < graphics.length; i++) {
            newGraphic = graphics[i];
            exist = false;
            //remove existing
            for(int x = oldGraphicIDList.size()-1; x >= 0; x--) {
                int id  = oldGraphicIDList.get(x);
                oldGraphic = layer.getGraphic(id);
                if ( oldGraphic != null ) {
                    if (isGraphicEqual(oldGraphic, newGraphic)) {
                        updateList.add(new Pair<Integer, Graphic>(id, newGraphic));
                        //remve from list it will decrease old_id looping
                        oldGraphicIDList.remove(x);
                        exist = true;
                        break;
                    }
                }
            }
            //add for unexisting
            if ( !exist )  newList.add(newGraphic);

        }

        //remove old and not update graphics within radius
        if ( oldGraphicIDs != null ) {
            for (int oldID : oldGraphicIDs) {
                oldGraphic = layer.getGraphic(oldID);
                //check for existing
                exist = false;
                if ( oldGraphic != null ) {
                    for(Graphic newG : newList ) {
                        if (isGraphicEqual(oldGraphic, newGraphic)) { exist = true; break; }
                    }
                    for(Pair<Integer, Graphic> pairG : updateList ) {
                        if (pairG.first == oldID) { exist = true; break; }
                    }
                    //not exist in new radius remove
                    if (!exist) removeList.add(oldID);
                }

            }
        }

        graphicMap.put("new", newList);
        graphicMap.put("update", updateList);
        graphicMap.put("remove", removeList);
        return graphicMap;
    }

    /**
     *
     * @param graphic1
     * @param graphic1
     * @return
     */
    private static boolean isGraphicEqual(Graphic graphic1, Graphic graphic2) {
        if ((int) graphic1.getAttributeValue("id_loc") == (int) graphic2.getAttributeValue("id_loc")) {
            return true;
        }
        return false;
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
