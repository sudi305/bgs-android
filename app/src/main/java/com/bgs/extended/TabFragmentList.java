package com.bgs.extended;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bgs.dheket.R;
import com.bgs.imageOrView.CustomAdapter;
import com.bgs.model.ItemObjectCustomList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SND on 27/01/2016.
 */
public class TabFragmentList extends Fragment {
    private ListView customListView;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_fragment_list, container, false);
        customListView = (ListView)rootView.findViewById(R.id.listView);
        List<ItemObjectCustomList> listViewItems = new ArrayList<ItemObjectCustomList>();
        listViewItems.add(new ItemObjectCustomList("Nigeria", R.drawable.logo));
        listViewItems.add(new ItemObjectCustomList("Ghana", R.drawable.logo));
        listViewItems.add(new ItemObjectCustomList("Senegal", R.drawable.logo));
        listViewItems.add(new ItemObjectCustomList("Togo", R.drawable.logo));

        customListView.setAdapter(new CustomAdapter(rootView.getContext(), listViewItems));

        customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // make Toast when click
                Toast.makeText(rootView.getContext(), "Position " + position, Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }
}
