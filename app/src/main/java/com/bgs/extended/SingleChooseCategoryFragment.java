package com.bgs.extended;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bgs.dheket.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by zhy on 15/9/10.
 */
public class SingleChooseCategoryFragment extends Fragment
{
    private String[] mVals;

    private TagFlowLayout mFlowLayout, mFlowLayoutSearch;
    private EditText editText;

    private List<String> filteredList = new ArrayList<>();
    private String[] newDataAfterRemove;
    LayoutInflater mInflater;
    private TagAdapter<String> mAdapter ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.flow_fragment_single_choose, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        mInflater = LayoutInflater.from(getActivity());
        mFlowLayout = (TagFlowLayout) view.findViewById(R.id.id_flowlayout);
        mFlowLayoutSearch = (TagFlowLayout) view.findViewById(R.id.id_flowlayout_);
        editText = (EditText) view.findViewById(R.id.editText);
        newDataAfterRemove = mVals;
        Collections.addAll(filteredList, mVals);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("char",""+s);
                filterItems(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //mFlowLayout.setMaxSelectCount(3);
        mFlowLayout.setAdapter(mAdapter = new TagAdapter<String>(mVals)
        {

            @Override
            public View getView(FlowLayout parent, int position, String s)
            {
                TextView tv = (TextView) mInflater.inflate(R.layout.flow_tv,
                        mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Toast.makeText(getActivity(), mVals[position], Toast.LENGTH_SHORT).show();
                //view.setVisibility(View.GONE);
                return true;
            }
        });


        mFlowLayout.setOnSelectListener(new TagFlowLayout.OnSelectListener()
        {
            @Override
            public void onSelected(Set<Integer> selectPosSet)
            {
                //getActivity().setTitle("choose:" + selectPosSet.toString());
            }
        });
    }

    public void filterItems(CharSequence text) {
        int countNotFound = 0;
        filteredList.clear();
        Log.e("masuk", "" + text);
        if (TextUtils.isEmpty(text)) {
            Collections.addAll(filteredList, newDataAfterRemove);
            changeViewSearch();
            Log.e("kosong","iya");
        } else {
            for (String s : newDataAfterRemove) {
                if (s.toLowerCase().contains(text.toString().toLowerCase())) {
                    filteredList.add(s);
                    //Log.e("cari dan ketemu", ""+filteredList.add(s));
                    changeViewSearch();
                }
                else {
                    countNotFound++;
                }
                //Log.e("cari", ""+filteredList.add(s));
            }
            if (countNotFound==newDataAfterRemove.length){
                filteredList.clear();
                changeViewSearch();
            }
        }
        //notifyDataSetChanged();
    }

    public void changeViewSearch(){
        final int[] searchItem = {0};
        final String[] cari = {""};
        mFlowLayoutSearch.setAdapter(new TagAdapter<String>(filteredList)
        {

            @Override
            public View getView(FlowLayout parent, int position, String s)
            {
                TextView tv = (TextView) mInflater.inflate(R.layout.flow_tv,
                        mFlowLayoutSearch, false);
                tv.setText(s);
                return tv;
            }
        });

        mFlowLayoutSearch.setOnTagClickListener(new TagFlowLayout.OnTagClickListener()
        {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent)
            {
                Toast.makeText(getActivity(), filteredList.get(position), Toast.LENGTH_SHORT).show();
                //view.setVisibility(View.GONE);
                Log.e("yang terpilih",""+searchItem[0]+"|"+cari[0]);
                for (int i = 0; i < mVals.length; i++) {
                    if (mVals[i].equalsIgnoreCase(filteredList.get(position))){
                        mAdapter.setSelectedList(i);
                    }
                }
                return true;
            }
        });


        mFlowLayoutSearch.setOnSelectListener(new TagFlowLayout.OnSelectListener()
        {
            @Override
            public void onSelected(Set<Integer> selectPosSet)
            {
                //searchItem = Integer.parseInt(selectPosSet.toString().replace("[","").replace("]",""));
                getActivity().setTitle("choose:" + selectPosSet.toString());
            }
        });
    }
}
