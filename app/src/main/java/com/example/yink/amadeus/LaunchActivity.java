package com.example.yink.amadeus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class LaunchActivity extends AppCompatActivity {
    ImageView connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        connect = (ImageView) findViewById(R.id.imageView_connect);
        connect.setImageResource(R.drawable.connect_unselect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect.setImageResource(R.drawable.connect_select);
                Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        connect.setImageResource(R.drawable.connect_unselect);
        super.onResume();
    }
}
