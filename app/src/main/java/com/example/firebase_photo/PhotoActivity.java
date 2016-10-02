package com.example.firebase_photo;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PhotoActivity extends AppCompatActivity implements OnSuccessListener<UploadTask.TaskSnapshot> {
    private static final String TAG = "PhotoActivity";
    private ImageView imageView;
    private Button uploadBtn;
    private Button pickBtn;
    private int RC_ADD_PHOTO=1;
    private int REQUEST_PICK_PHOTO=2;
    private String selectedImagePath;
    private EditText titleEd;
    private EditText descriptionEd;

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
        titleEd = (EditText) findViewById(R.id.titleEd);
        descriptionEd = (EditText) findViewById(R.id.descriptionEd);
        uploadBtn = (Button) findViewById(R.id.button2);
        pickBtn = (Button) findViewById(R.id.button3);


    }

    public void uploadPhoto(View v){
        String titleString = titleEd.getText().toString();
        String descriptionString = descriptionEd.getText().toString();

        FirebaseStorage firebaseStorage =FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://fir-photo.appspot.com");
        StorageReference  imagedRef =storageReference.child(new Date().getTime()+"");
        InputStream inputStream =null;
        try {
            inputStream = new FileInputStream(new File(selectedImagePath));
            UploadTask uploadTask = imagedRef.putStream(inputStream);
            uploadTask.addOnSuccessListener(this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void pickPhoto(View v){
        Intent pickIntent=new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(pickIntent, REQUEST_PICK_PHOTO);
    }



    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult");
        //是去挑照片且有挑一個照片(資料在intent裡)
        if(requestCode ==REQUEST_PICK_PHOTO && resultCode == RESULT_OK){
            Log.d(TAG,"pick success.");
            Uri originUri = data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),originUri);

                final int takeFlags= data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION |Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //noinspection WrongConstant
                getContentResolver().takePersistableUriPermission(originUri,takeFlags);
                String id = originUri.getLastPathSegment().split(":")[1];
                final String[] imagesColumns ={MediaStore.Images.Media.DATA};
                final String orderBy=null;
                Uri uri = getUri();
                selectedImagePath=null;
                Cursor cursor = managedQuery(uri,imagesColumns,MediaStore.Images.Media._ID+"="+id,null,orderBy);
                if(cursor.moveToFirst()){
                    Log.d(TAG,"cursor.moveToFirst()");
                    selectedImagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
                Log.d(TAG, "selected path ="+selectedImagePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private Uri getUri(){
        String state = Environment.getExternalStorageState();
        if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)){
            return  MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        }else{
            return  MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        }
    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        //上傳成功會有下載網址
        Log.d(TAG,"Url="+taskSnapshot.getDownloadUrl());

        String titleString = titleEd.getText().toString();
        String descriptionString = descriptionEd.getText().toString();
        String uid=
        getSharedPreferences("pref_name",MODE_PRIVATE).getString("pref_userid","default userid");
        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference();
        Map<String,Object> photo = new HashMap<>();
        photo.put("title", titleString);
        photo.put("description", descriptionString);
        photo.put("url", taskSnapshot.getDownloadUrl());
        databaseReference.child(uid).child("photos").push().setValue(photo);

    }
}
