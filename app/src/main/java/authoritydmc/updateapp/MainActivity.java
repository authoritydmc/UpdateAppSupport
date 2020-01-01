package authoritydmc.updateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import com.vastgk.updateapp.UpdateApp;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      UpdateApp.checkupdate(MainActivity.this, BuildConfig.VERSION_NAME,true);
    }
}
