package authoritydmc.updateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import authoritydmc.UpdateApp;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  FirebaseApp.initializeApp(MainActivity.this);
        startActivity(new Intent(MainActivity.this, UpdateApp.class));
//        UpdateApp.checkupdate(MainActivity.this, BuildConfig.VERSION_NAME,true);
    }
}
