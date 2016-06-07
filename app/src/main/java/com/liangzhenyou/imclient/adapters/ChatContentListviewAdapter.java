package com.liangzhenyou.imclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.dao.CustomMessage;
import com.liangzhenyou.imclient.ui.activity.ImageActivity;
import com.liangzhenyou.imclient.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by youyou on 2016/4/10.
 */
public class ChatContentListviewAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<CustomMessage> arrayList = null;

    private MediaPlayer mediaPlayer;


    public ChatContentListviewAdapter(Context context, ArrayList<CustomMessage> messages) {
        mContext = context;
        arrayList = messages;
        mediaPlayer = new MediaPlayer();
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
        public ImageView iconLeft;
        public TextView textViewLeft;
        public ImageView imageViewLeft;
        public TextView audioViewLeft;

        public RelativeLayout reLayoutRight;
        public ImageView iconRight;
        public TextView textViewRight;
        public ImageView imageViewRight;
        public TextView audioViewRight;


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
            viewHolder.iconLeft = (ImageView) view.findViewById(R.id.chat_content_listview_item_icon_left);
            viewHolder.textViewLeft = (TextView) view.findViewById(R.id.chat_content_listview_item_text_left);
            viewHolder.imageViewLeft = (ImageView) view.findViewById(R.id.chat_content_image_left);
            viewHolder.audioViewLeft = (TextView) view.findViewById(R.id.chat_content_audio_left);


            viewHolder.reLayoutRight = (RelativeLayout) view.findViewById(R.id.chat_content_listview_item_right);
            viewHolder.iconRight = (ImageView) view.findViewById(R.id.chat_content_listview_item_icon_right);
            viewHolder.textViewRight = (TextView) view.findViewById(R.id.chat_content_listview_item_text_right);
            viewHolder.imageViewRight = (ImageView) view.findViewById(R.id.chat_content_image_right);
            viewHolder.textViewTime = (TextView) view.findViewById(R.id.chat_content_listview_item_time);
            viewHolder.audioViewRight = (TextView) view.findViewById(R.id.chat_content_audio_right);


            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
            initView(viewHolder);
        }

        initViewByMyMessage(arrayList.get(position), viewHolder);


        return view;
    }

    public void initView(ViewHolder viewHolder) {
        viewHolder.reLayoutRight.setVisibility(View.GONE);
        viewHolder.reLayoutLeft.setVisibility(View.GONE);
        //viewHolder.iconLeft.setVisibility(View.GONE);
        //viewHolder.iconRight.setVisibility(View.GONE);
        viewHolder.textViewLeft.setVisibility(View.GONE);
        viewHolder.textViewRight.setVisibility(View.GONE);
        viewHolder.imageViewLeft.setVisibility(View.GONE);
        viewHolder.imageViewRight.setVisibility(View.GONE);
        viewHolder.audioViewLeft.setVisibility(View.GONE);
        viewHolder.audioViewRight.setVisibility(View.GONE);

        viewHolder.textViewTime.setText("");


    }

    public void initViewByMyMessage(final CustomMessage customMessage, ViewHolder holder) {
        holder.textViewTime.setText(customMessage.getDateFormat().toString());

        holder.imageViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = customMessage.getBody();
                Intent intent = new Intent(mContext, ImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        holder.imageViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = customMessage.getBody();
                Intent intent = new Intent(mContext, ImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        holder.audioViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer.reset();
                    File file = new File(customMessage.getBody());
                    FileInputStream fis = new FileInputStream(file);
                    mediaPlayer.setDataSource(fis.getFD());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.audioViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer.reset();
                    File file = new File(customMessage.getBody());
                    FileInputStream fis = new FileInputStream(file);
                    mediaPlayer.setDataSource(fis.getFD());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        //消息从本地发到远程
        if (customMessage.getORIGIN() == CustomMessage.ORIGIN_LOCAL) {
            holder.reLayoutRight.setVisibility(View.VISIBLE);
            if (customMessage.getTYPE() == CustomMessage.TYPE_TEXT) {
                holder.textViewRight.setVisibility(View.VISIBLE);
                holder.textViewRight.setText(customMessage.getBody());
            } else if (customMessage.getTYPE() == CustomMessage.TYPE_AUDIO) {
                holder.audioViewRight.setVisibility(View.VISIBLE);
            } else if (customMessage.getTYPE() == CustomMessage.TYPE_IMAGE) {
                holder.imageViewRight.setVisibility(View.VISIBLE);
                holder.imageViewRight.setImageBitmap(FileUtils.getimage(customMessage.getBody()));
            }
        } else if (customMessage.getORIGIN() == CustomMessage.ORIGIN_REMOTE) {
            holder.reLayoutLeft.setVisibility(View.VISIBLE);

            if (customMessage.getTYPE() == CustomMessage.TYPE_TEXT) {
                holder.textViewLeft.setVisibility(View.VISIBLE);
                holder.textViewLeft.setText(customMessage.getBody());
            } else if (customMessage.getTYPE() == CustomMessage.TYPE_AUDIO) {
                holder.audioViewLeft.setVisibility(View.VISIBLE);
            } else if (customMessage.getTYPE() == CustomMessage.TYPE_IMAGE) {
                holder.imageViewLeft.setVisibility(View.VISIBLE);
                holder.imageViewLeft.setImageBitmap(FileUtils.getimage(customMessage.getBody()));
            }

        }

    }

    public void stopMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    }
}
