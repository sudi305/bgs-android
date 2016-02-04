package com.bgs.imageOrView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bgs.common.Utility;
import com.bgs.dheket.R;
import com.bgs.model.ItemObjectCustomList;

import java.util.List;

/**
 * Created by SND on 27/01/2016.
 */
public class CustomAdapterChat extends BaseAdapter {
    private LayoutInflater lInflater;
    private List<ItemObjectCustomList> listStorage;
    //    NumberFormat formatter = new DecimalFormat("#0.000");
    int isPromo;
    String promo="-";
    RoundImage crop_image_circle, crop_image_circle_back;

    public CustomAdapterChat(Context context, List<ItemObjectCustomList> customizedListView) {
        lInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }
    @Override
    public int getCount() {
        return listStorage.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder listViewHolder;
        isPromo = listStorage.get(position).getLoc_promo();
        if(convertView == null){
            listViewHolder = new ViewHolder();
            if (position%2==0) convertView = lInflater.inflate(R.layout.chat_user_visitor, parent, false);
            else convertView = lInflater.inflate(R.layout.chat_user_merchant, parent, false);

            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }
        if (isPromo==1)promo="Promo";
        else promo="-";

        //listViewHolder.imageView_loc_pic.setImageResource(R.drawable.small_logo);
        return convertView;
    }
    static class ViewHolder{
        TextView textView_loc_name, textView_loc_address, textView_loc_distance, textView_promo, textView_loc_id;
        ImageView imageView_loc_pic, imageView_backPic;
    }
}