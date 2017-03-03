package com.example.yink.amadeus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class LaunchActivity extends AppCompatActivity {
    ImageView connect, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);
        connect = (ImageView) findViewById(R.id.imageView_connect);
        cancel = (ImageView) findViewById(R.id.imageView_cancel);

        connect.setImageResource(R.drawable.connect_unselect);
        cancel.setImageResource(R.drawable.cancel_unselect);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connect.setImageResource(R.drawable.connect_select);

                Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel.setImageResource(R.drawable.cancel_select);

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        connect.setImageResource(R.drawable.connect_unselect);
        cancel.setImageResource(R.drawable.cancel_unselect);
        super.onResume();
    }
}
