package com.ultrapower.boreweibo.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.User;
import com.ultrapower.boreweibo.R;
import com.ultrapower.boreweibo.utils.DateUtils;
import com.ultrapower.boreweibo.utils.StringUtils;
import com.ultrapower.boreweibo.utils.ToastUtils;

import java.util.List;

public class StatusCommentAdapter extends BaseAdapter {

    private Context context;
    private List<Comment> comments;
    private ImageLoader imageLoader;

    public StatusCommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
        this.imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderList holder;
        if (convertView == null) {
            holder = new ViewHolderList();
            convertView = View.inflate(context, R.layout.item_comment, null);
            holder.ll_comments = (LinearLayout) convertView
                    .findViewById(R.id.ll_comments);
            holder.iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            holder.tv_subhead = (TextView) convertView
                    .findViewById(R.id.tv_subhead);
            holder.tv_caption = (TextView) convertView
                    .findViewById(R.id.tv_caption);
            holder.tv_comment = (TextView) convertView
                    .findViewById(R.id.tv_comment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderList) convertView.getTag();
        }

        Comment comment = getItem(position);
        User user = comment.user;

        imageLoader.displayImage(user.profile_image_url, holder.iv_avatar);
        holder.tv_subhead.setText(user.name);
        holder.tv_caption.setText(DateUtils.getShortTime(comment.created_at));
        SpannableString weiboContent = StringUtils.getWeiboContent(
                context, holder.tv_comment, comment.text);
        holder.tv_comment.setText(weiboContent);

        holder.ll_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(context, "回复评论", Toast.LENGTH_SHORT);
            }
        });

        return convertView;
    }

    public static class ViewHolderList {
        public LinearLayout ll_comments;
        public ImageView iv_avatar;
        public TextView tv_subhead;
        public TextView tv_caption;
        public TextView tv_comment;
    }

}
