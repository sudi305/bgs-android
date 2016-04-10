package com.bgs.imageOrView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bgs.dheket.R;
import com.bgs.dheket.SearchAllCategoryActivity;
import com.bgs.dheket.SearchViewActivity;
import com.bgs.dheket.SelectCategoryActivity;
import com.bgs.dheket.SettingCategoryBubbleActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.HashMap;

public class ListViewAdapterSettingBubble extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    ArrayList<String>categoryUser = new ArrayList<>();
    String email;

    Picasso picasso;

    HashMap<String, String> result = new HashMap<String, String>();

    public ListViewAdapterSettingBubble(Context context,ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, final View convertView, ViewGroup parent) {
        ImageView imageView_icon;
        TextView textView_id_category, textView_category_name, textView_hashtag,
                textView_radius_or_edit, textView_id_profile_tag;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.listview_modified_item_for_setting, parent, false);

        result = data.get(position);

        imageView_icon = (ImageView)itemView.findViewById(R.id.imageView_list_setting_icon);
        textView_id_category = (TextView)itemView.findViewById(R.id.textView_list_setting_id_category);
        textView_category_name = (TextView)itemView.findViewById(R.id.textView_list_setting_category);
        textView_hashtag = (TextView)itemView.findViewById(R.id.textView_list_setting_hashtag);
        textView_id_profile_tag = (TextView)itemView.findViewById(R.id.textView_list_setting_id_profile_tag);
        textView_radius_or_edit = (TextView)itemView.findViewById(R.id.textView_list_setting_edit_or_radius);

        // Capture position and set results to the TextViews
        if (result.get("icon")!= null){
            picasso.with(context).load(result.get("icon")).transform(new CircleTransform()).into(imageView_icon);
        }
        Log.e("Position","arraylist ke-"+position+ " = "+result.size());
        if (position!=data.size()-1){
            textView_id_category.setText(result.get("id_category"));
            textView_category_name.setText(result.get("category_name"));
            textView_id_profile_tag.setText(result.get("id_profile_tag"));
            if (!result.get("detail_tag").isEmpty() && !result.get("detail_tag").toString().equalsIgnoreCase("null"))textView_hashtag.setText(result.get("detail_tag"));
            else textView_hashtag.setText("-");
            textView_radius_or_edit.setText("Edit");
        } else {
            textView_id_category.setText("");
            textView_category_name.setText("Radius");
            textView_id_profile_tag.setText("");
            textView_hashtag.setText("YOUR SEARCHING RANGE");
            textView_radius_or_edit.setText(result.get("radius")+" KM");
            email = result.get("email");
        }

        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                result = data.get(position);
                for (int i = 0; i < result.size() ; i++) {
                    categoryUser.add(i, data.get(i).get("category_name"));
                    Log.e("datax " + categoryUser.size(), data.get(i).get("category_name").toString());
                }
                if (position!=data.size()-1){
                    Intent intent = new Intent(context, SelectCategoryActivity.class);
                    Bundle paket = new Bundle();
                    paket.putString("email",email);
                    paket.putString("id_category",result.get("id_category"));
                    paket.putString("category_name",result.get("category_name"));
                    paket.putString("id_profile_tag",result.get("id_profile_tag"));
                    paket.putString("detail_tag",result.get("detail_tag"));
                    paket.putStringArrayList("data_category",categoryUser);
                    intent.putExtras(paket);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                } else {
                    SettingCategoryBubbleActivity.viewGroup.setVisibility(View.VISIBLE);
                }
                //context.finish();
            }
        });
        return itemView;
    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
