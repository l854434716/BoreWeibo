package com.ultrapower.boreweibo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ultrapower.boreweibo.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/25.
 */
public class WriteStatusGridImgsAdapter extends BaseAdapter{

    private  Context context;

    private  ArrayList<Uri> uris;

    private  GridView gridView;


    public WriteStatusGridImgsAdapter(Context context, ArrayList<Uri> imgUris, GridView gridView) {

        this.context=context;

        this.uris= imgUris;

        this.gridView= gridView;

    }

    @Override
    public int getCount() {
        if (uris==null)
            return 0;
        return uris.size()>0 ? uris.size()+1:0;
    }

    @Override
    public Uri getItem(int position) {
        return uris.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final  Holder viewHolder;

        if(convertView==null){

            viewHolder= new Holder();
            convertView= View.inflate(context, R.layout.item_grid_image,null);

            viewHolder.iv_Image= (ImageView) convertView.findViewById(R.id.iv_image);
            viewHolder.iv_delete_image= (ImageView) convertView.findViewById(R.id.iv_delete_image);


            convertView.setTag(viewHolder);
        }else {

            viewHolder= (Holder) convertView.getTag();
        }

        int  horizontalSpacing=gridView.getHorizontalSpacing();

        int  imageWidth= (gridView.getWidth()- 2*horizontalSpacing-gridView.getPaddingLeft()
                -gridView.getPaddingRight())/3;

        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(imageWidth,imageWidth);

        viewHolder.iv_Image.setLayoutParams(params);

        if (position <getCount()-1){
            final Uri uri= getItem(position);

            viewHolder.iv_Image.setImageURI(uri);

            viewHolder.iv_delete_image.setVisibility(View.GONE);

            viewHolder.iv_delete_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uris.remove(position);
                    notifyDataSetChanged();
                }
            });

        }else {

            viewHolder.iv_Image.setImageResource(R.drawable.compose_pic_add_more);
        }


        return convertView;
    }



    public  static  class Holder{

        public ImageView  iv_Image;

        public ImageView  iv_delete_image;
    }
}
