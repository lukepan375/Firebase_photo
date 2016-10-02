package com.example.firebase_photo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button uploadBtn;
    private Button pickBtn;
    private int RC_ADD_PHOTO=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        imageView = (ImageView) findViewById(R.id.imageView);
        uploadBtn = (Button) findViewById(R.id.button2);
        pickBtn = (Button) findViewById(R.id.button3);


    }

    public void uploadPhoto(View v){

    }

    public void pickPhoto(View v){

    }

}
