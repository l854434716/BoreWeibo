package com.ultrapower.boreweibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.openapi.models.UserItem;
import com.ultrapower.boreweibo.R;
import com.ultrapower.boreweibo.utils.ToastUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/10/9.
 */
public class UserItemAdapter extends BaseAdapter {

    private Context context;
    private List<UserItem> datas;

    public UserItemAdapter(Activity context, List<UserItem> userItems) {

        this.context = context;
        this.datas = userItems;

    }

    @Override
    public int getCount() {
        if (datas!=null)
         return datas.size();

        return  0;
    }

    @Override
    public UserItem getItem(int position) {
        return datas.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView==null){
            viewHolder= new ViewHolder();
            convertView= View.inflate(context, R.layout.item_user,null);

            viewHolder.iv_left=(ImageView) convertView.findViewById(R.id.iv_left);
            viewHolder.ll_content=convertView.findViewById(R.id.ll_content);
            viewHolder.tv_caption=(TextView) convertView.findViewById(R.id.tv_caption);
            viewHolder.v_divider=convertView.findViewById(R.id.v_divider);
            viewHolder.tv_subhead= (TextView) convertView.findViewById(R.id.tv_subhead);

            convertView.setTag(viewHolder);
        }else{

            viewHolder= (ViewHolder) convertView.getTag();
        }

        // set data
        UserItem item = getItem(position);
        viewHolder.iv_left.setImageResource(item.getLeftImg());
        viewHolder.tv_subhead.setText(item.getSubhead());
        viewHolder.tv_caption.setText(item.getCaption());

        viewHolder.v_divider.setVisibility(item.isShowTopDivider() ?
                View.VISIBLE : View.GONE);
        viewHolder.ll_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(context, "item click position = " + position, Toast.LENGTH_SHORT);
            }
        });

        return convertView;
    }


    public static class ViewHolder{
        public View v_divider;
        public View ll_content;
        public ImageView iv_left;
        public TextView tv_subhead;
        public TextView tv_caption;
    }
}
