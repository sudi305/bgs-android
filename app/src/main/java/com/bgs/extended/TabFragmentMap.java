package com.bgs.extended;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bgs.dheket.R;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

/**
 * Created by SND on 27/01/2016.
 */
public class TabFragmentMap extends Fragment {
    MapView mMapView;
    GraphicsLayer graphicsLayer = new GraphicsLayer();
    Point point;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_fragment_map,
                container, false);


        //Retrieve the map and initial extent from XML layout
        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.centerAndZoom(-9664114.480484284,3962469.970217699,10);

        mMapView.addLayer(new ArcGISTiledMapServiceLayer("http://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer"));
        Point p = new Point(-9664114.480484284,3962469.970217699); //Birmingham
        mMapView.setResolution(mMapView.getMinResolution());

        GraphicsLayer graphicsLayer = new GraphicsLayer(mMapView.getSpatialReference(), new Envelope(-180, -90, 180, 90));
        mMapView.addLayer(graphicsLayer);

        //add marker
        Drawable d = getResources().getDrawable(R.drawable.logo_back);
        PictureMarkerSymbol sym = new PictureMarkerSymbol(d);
        sym.setOffsetX(10);
        sym.setOffsetY(10);
        Graphic g = new Graphic(p, sym);
        graphicsLayer.addGraphic(g);

        return rootView;
    }

    private int convertToDp(double input) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (input * scale + 0.5f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.unpause();
    }
}

        /*Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_map, container, false);
    }
}*/