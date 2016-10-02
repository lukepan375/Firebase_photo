package com.example.firebase_photo;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private static final String TAG = "MainActivity";
    boolean logon=false;
    private int RESULT_SIGN_IN=1;
    FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private int RC_ADD_PHOTO=2;
    private int REQUEST_STORAGE=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//FCM  (request the TOKEN)
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"token for FCM is "+token);
//     -----------------------------



        //Toolbar(在activity_main.xml可看到) 就是以前的ActionBar
        //,content_main.xml則是管理actionbar以下的內容版面配置
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permission != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE);
                }

                addPhoto();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(this);
        if(!logon){

            //the first params is a intent.
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setProviders(AuthUI.EMAIL_PROVIDER,
                            AuthUI.FACEBOOK_PROVIDER
                    ,AuthUI.GOOGLE_PROVIDER)
                    .setLogo(R.drawable.ic_visibility_off_black_24dp).build(), RESULT_SIGN_IN);
            //this Activity started by ^^^^^^^^ is presented by FireBase-UI



            //匿名登入用以下method
//            firebaseAuth.signInAnonymously();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            addPhoto();
        }
    }

    private void addPhoto() {
        startActivityForResult(new Intent(MainActivity.this,PhotoActivity.class),RC_ADD_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //直接設計auth listener即可
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        user = firebaseAuth.getCurrentUser();
        if(user !=null){
            Log.d(TAG,"userid="+user.getUid());
            Log.d(TAG,"userEmail="+user.getEmail());

            logon=true;

            getSharedPreferences("pref_name",MODE_PRIVATE).edit().putString("pref_userid",user.getUid()).apply();
            //Save some data ,Update user data every logged in.

            //connect to the root  of database
            FirebaseDatabase firebaseDatabase =FirebaseDatabase.getInstance();

            //create or get "users" folder
            DatabaseReference dbReference_users=firebaseDatabase.getReference("users");

            //child: add file in present folder and move to that folder.
            dbReference_users.child(user.getUid()).child("email").setValue(user.getEmail());
            dbReference_users.child(user.getUid()).child("name").setValue(user.getDisplayName());



        }
    }

    public void logout(View v){

    }
}
