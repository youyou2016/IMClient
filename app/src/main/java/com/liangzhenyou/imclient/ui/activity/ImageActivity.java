package com.liangzhenyou.imclient.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.liangzhenyou.imclient.R;
import com.liangzhenyou.imclient.utils.FileUtils;

public class ImageActivity extends Activity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (ImageView) findViewById(R.id.image_view);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String path = bundle.getString("path");

        imageView.setImageBitmap(FileUtils.getimage(path));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageActivity.this.finish();
            }
        });


    }
}
