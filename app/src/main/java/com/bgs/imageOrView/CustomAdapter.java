package com.bgs.imageOrView;

import android.content.Context;
import android.content.res.Resources;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by SND on 27/01/2016.
 */
public class CustomAdapter extends BaseAdapter {
    private LayoutInflater lInflater;
    private List<ItemObjectCustomList> listStorage;
    //    NumberFormat formatter = new DecimalFormat("#0.000");
    int isPromo;
    String promo="-";
    RoundImage crop_image_circle, crop_image_circle_back;

    public CustomAdapter(Context context, List<ItemObjectCustomList> customizedListView) {
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
            convertView = lInflater.inflate(R.layout.listview_modified, parent, false);
            listViewHolder.textView_loc_id = (TextView)convertView.findViewById(R.id.textView_loc_id);
            listViewHolder.textView_loc_name = (TextView)convertView.findViewById(R.id.textView_loc_name);
            listViewHolder.textView_loc_address = (TextView)convertView.findViewById(R.id.textView_loc_address);
            listViewHolder.textView_loc_distance = (TextView)convertView.findViewById(R.id.textView_distance);
            listViewHolder.textView_promo = (TextView)convertView.findViewById(R.id.textView_promo);
            listViewHolder.imageView_loc_pic = (ImageView)convertView.findViewById(R.id.imageView_loc_pic);
            listViewHolder.imageView_backPic = (ImageView)convertView.findViewById(R.id.imageView_backPic);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }
        if (isPromo==1)promo="Promo";
        else promo="-";
        listViewHolder.textView_loc_id.setText(""+listStorage.get(position).getId_loc());
        listViewHolder.textView_loc_name.setText(listStorage.get(position).getLoc_name());
        listViewHolder.textView_loc_address.setText(listStorage.get(position).getLoc_address());
        listViewHolder.textView_promo.setText(promo);
        Utility setNumber = new Utility();
        listViewHolder.textView_loc_distance.setText("" + setNumber.changeFormatNumber(listStorage.get(position).getLoc_distance()) + " Km");

        final float scale = convertView.getResources().getDisplayMetrics().density;
        int wH_pic = (int)(60 * scale + 0.5f);
        int wH_pic_back = (int)(70 * scale + 0.5f);
        Bitmap bitmap = BitmapFactory.decodeResource(convertView.getResources(),R.drawable.small_logo);
        Bitmap bitmapBack = BitmapFactory.decodeResource(convertView.getResources(),R.drawable.logo_back);
        crop_image_circle = new RoundImage(bitmap,wH_pic);
        crop_image_circle_back = new RoundImage(bitmapBack,wH_pic_back);
        listViewHolder.imageView_backPic.setImageDrawable(crop_image_circle_back);
        listViewHolder.imageView_backPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        listViewHolder.imageView_loc_pic.setImageDrawable(crop_image_circle);
        listViewHolder.imageView_loc_pic.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //listViewHolder.imageView_loc_pic.setImageResource(R.drawable.small_logo);
        return convertView;
    }
    static class ViewHolder{
        TextView textView_loc_name, textView_loc_address, textView_loc_distance, textView_promo, textView_loc_id;
        ImageView imageView_loc_pic, imageView_backPic;
    }
}