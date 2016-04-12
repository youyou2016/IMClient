package com.liangzhenyou.imclient.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.dao.MyMessage;

import java.util.ArrayList;

/**
 * Created by youyou on 2016/4/10.
 */
public class ChatContentListviewAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<MyMessage> arrayList = null;


    public ChatContentListviewAdapter(Context context, ArrayList<MyMessage> messages) {
        mContext = context;
        arrayList = messages;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        public RelativeLayout reLayoutLeft;
        public ImageView imageViewLeft;
        public TextView textViewLeft;

        public RelativeLayout reLayoutRight;
        public ImageView imageViewRight;
        public TextView textViewRight;

        public TextView textViewTime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;
        if (convertView == null) {

            view = View.inflate(mContext, R.layout.chat_content_listview_item, null);
            viewHolder = new ViewHolder();
            viewHolder.reLayoutLeft = (RelativeLayout) view.findViewById(R.id.chat_content_listview_item_left);
            viewHolder.imageViewLeft = (ImageView) view.findViewById(R.id.chat_content_listview_item_icon_left);
            viewHolder.textViewLeft = (TextView) view.findViewById(R.id.chat_content_listview_item_text_left);
            viewHolder.reLayoutRight = (RelativeLayout) view.findViewById(R.id.chat_content_listview_item_right);
            viewHolder.imageViewRight = (ImageView) view.findViewById(R.id.chat_content_listview_item_icon_right);
            viewHolder.textViewRight = (TextView) view.findViewById(R.id.chat_content_listview_item_text_right);
            viewHolder.textViewTime = (TextView) view.findViewById(R.id.chat_content_listview_item_time);

            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
            initView(viewHolder);
        }

        initViewByMyMessage(arrayList.get(position), viewHolder);


        return view;
    }

    public void initView(ViewHolder viewHolder) {
        viewHolder.reLayoutRight.setVisibility(View.GONE);
        viewHolder.reLayoutLeft.setVisibility(View.GONE);
        viewHolder.textViewTime.setText("");
    }

    public void initViewByMyMessage(MyMessage myMessage, ViewHolder holder) {
        holder.textViewTime.setText(myMessage.getDateFormat().toString());

        //消息从本地发到远程
        if (myMessage.getORIGIN() == MyMessage.ORIGIN_LOCAL) {
            holder.reLayoutRight.setVisibility(View.VISIBLE);
            holder.textViewRight.setText(myMessage.getBody());
        } else {
            holder.reLayoutLeft.setVisibility(View.VISIBLE);
            holder.textViewLeft.setText(myMessage.getBody());
        }

    }
}
