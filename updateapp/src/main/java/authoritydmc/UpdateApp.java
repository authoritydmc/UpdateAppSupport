package authoritydmc;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vastgk.updateapp.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class UpdateApp extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 7;
    private static Update_MODEL updateDetails = null;
    private TextView nametxtView, sizetxtView, versiontxtView, downloadurltxtView, dwnldInfotxtv;
    private Button btnDownload;
    private ImageView iconImgView;
   private boolean isChecking = false;
  private   boolean isDownloadSuccess = false;
   private static String fileProviderName = "";
private  static final String TAG="UPDATESUPPORT";
    private ProgressBar progressBar;
   private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);
FirebaseApp.initializeApp(UpdateApp.this);
        init();
        toggleUIvisibility(false);
        fileProviderName=getPackageName()+".fileprovider";
        Toast.makeText(this, fileProviderName, Toast.LENGTH_SHORT).show();
        checkforupdate();

    }


    private void init() {
        nametxtView = findViewById(R.id.update_app_name);
        sizetxtView = findViewById(R.id.update_app_size);
        btnDownload = findViewById(R.id.update_downloadBtn);
        iconImgView = findViewById(R.id.update_app_ico);
        downloadurltxtView = findViewById(R.id.update_app_url);
        versiontxtView = findViewById(R.id.update_app_version);
        dwnldInfotxtv = findViewById(R.id.update_app_downloadinfo);
        progressBar = findViewById(R.id.progressBar);
        btnDownload.setOnClickListener(v -> {
            if (isChecking) {
                Toast.makeText(this, "Checking for update", Toast.LENGTH_SHORT).show();
                checkforupdate();

            } else {
                Toast.makeText(this, "Downloading Update", Toast.LENGTH_SHORT).show();


                if (checkforWritePermission()) {
                    DownloadApp();
                } else {
                    Toast.makeText(this, "Please Allow Write permission to Download the File", Toast.LENGTH_SHORT).show();


                }

            }

        });

    }

    private void DownloadApp() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + updateDetails.getName() + updateDetails.getVersion() + ".apk");
        if (file.exists()) {
            Toast.makeText(UpdateApp.this, "App already Downloaded In Download Folder \nInstalling...", Toast.LENGTH_LONG).show();

            installDownloadedAPP(file);

            dwnldInfotxtv.setVisibility(View.VISIBLE);
            dwnldInfotxtv.setText("ReDownload");
            dwnldInfotxtv.setOnClickListener(v -> {
                new
                        DownloadFileFromURL().execute(updateDetails.getApp_download_url());
            });

        } else {
            new
                    DownloadFileFromURL().execute(updateDetails.getApp_download_url());

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                DownloadApp();

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.;
                nametxtView.setText("Please Allow Write Permission to Download the app");
            }
            return;
        }
    }

    private boolean checkforWritePermission() {
        if (ContextCompat.checkSelfPermission(UpdateApp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(UpdateApp.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            return false;
        } else
            return true;

    }


    private void updateUI(Update_MODEL updateDetails, String currentVersion) {
        Update_MODEL obj = updateDetails;

        if (obj != null) {
            Log.d("RAAJ", "updateUI: onbj"+obj.getVersion());
            if (Float.valueOf(obj.getVersion()) > Float.valueOf(currentVersion)) {
                Toast.makeText(this, "Update Available", Toast.LENGTH_SHORT).show();
                toggleUIvisibility(true);
                //load image
                Picasso.get().load(obj.getIcon_url()).placeholder(R.drawable.ic_scan_24dp).into(iconImgView);
                sizetxtView.setText(obj.getSize());
                nametxtView.setText(obj.getName());
                versiontxtView.setText(obj.getVersion());
                downloadurltxtView.setText("Open Link in Browser");
                downloadurltxtView.setOnClickListener(v -> {
                    ClipboardManager clipboard = (ClipboardManager)
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("website", obj.getApp_download_url());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(obj.getApp_download_url()));
                    startActivity(browserIntent);

                });
            } else {
                Toast.makeText(this, "Already the Latest Version", Toast.LENGTH_SHORT).show();
                versiontxtView.setText("Version :" +currentVersion);
                toggleUIvisibility(false);
            }

        } else {
            Toast.makeText(this, "error checking update", Toast.LENGTH_SHORT).show();
            versiontxtView.setText("Error Getting Update Details ");
            toggleUIvisibility(false);

        }

    }

    private void toggleUIvisibility(boolean show) {
        int visibility_value = show ? View.VISIBLE : View.GONE;
        nametxtView.setVisibility(visibility_value);
        iconImgView.setVisibility(visibility_value);
        downloadurltxtView.setVisibility(visibility_value);
        sizetxtView.setVisibility(visibility_value);
        if (show == false) {
            isChecking = true;
            btnDownload.setText("Check For Update");
        } else {
            isChecking = false;
            btnDownload.setText("Download");
        }
    }

    private void checkforupdate() {
DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("update")) {
                    updateDetails = dataSnapshot.child("update").getValue(Update_MODEL.class);
                    Log.d("RAAJ", "onDataChange: +"+getCurrentVersion());
                    updateUI(updateDetails, getCurrentVersion());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private String getCurrentVersion() {
        //get the current version number and name

        String versionName = "";
        int versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;

    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            dwnldInfotxtv.setVisibility(View.VISIBLE);
            dwnldInfotxtv.setOnClickListener(null);
            dwnldInfotxtv.setText("Downloading Begaining");
            progressBar.setMax(100);
            progressBar.setProgress(0);
            isDownloadSuccess = false;
            filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + updateDetails.getName() + updateDetails.getVersion() + ".apk";
btnDownload.setEnabled(false);
btnDownload.getBackground().setAlpha(0);

        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdir();
                }
                if (success) {
                    // Do something on success
                } else {
                    // Do something else on failure
                }
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                //extension must change (mp3,mp4,zip,apk etc.)
                OutputStream output = new FileOutputStream(filename);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called

                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }


        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            int d = Integer.parseInt(progress[0]);
            progressBar.setProgress(d);
            dwnldInfotxtv.setText("Downloaded " + d + " %");
            if (d == 100)
                isDownloadSuccess = true;
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            progressBar.setVisibility(View.GONE);
            btnDownload.setEnabled(true);
            btnDownload.getBackground().setAlpha(255);
            dwnldInfotxtv.setVisibility(View.GONE);
            if (isDownloadSuccess) {
                File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + updateDetails.getName() + updateDetails.getVersion() + ".apk");

                Toast.makeText(UpdateApp.this, "Downloaded in Download folder ", Toast.LENGTH_SHORT).show();
                installDownloadedAPP(file);
            }

        }

    }

    private void installDownloadedAPP(File file) {


        btnDownload.setText("Install");


        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(UpdateApp.this, fileProviderName, file);
            Log.d(TAG, "installDownloadedAPP: "+apkUri.getAuthority());
            Log.d(TAG, "installDownloadedAPP: p"+fileProviderName);
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            Uri apkUri = Uri.fromFile(file);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }


    public static void checkupdate(Context context, String currentVersion, boolean shouldShowDialogueBox) {
        FirebaseApp.initializeApp(context);
        fileProviderName=context.getPackageName()+".fileprovider";

        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("update")) {
                    rootref.child("update").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean isforced = false;


                            if (dataSnapshot.hasChild("isforced")) {
                                isforced = dataSnapshot.child("isforced").getValue(Boolean.class);
                            }
                            String finalVersion = null;
                            if (dataSnapshot.hasChild("version"))
                                finalVersion = dataSnapshot.child("version").getValue(String.class);
                            if (finalVersion != null && Float.valueOf(currentVersion) < Float.valueOf(finalVersion)) {
                                if (isforced) {
                                    context.startActivity(new Intent(context, UpdateApp.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    ((Activity) context).finish();
                                } else {
                                    if (!shouldShowDialogueBox)
                                        Toast.makeText(context, "Update Available", Toast.LENGTH_SHORT).show();
                                    else {
                                        //Show Dialogue BOx of Update available
                                        Dialog updatedial = new Dialog(context);
                                        updatedial.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        updatedial.setContentView(R.layout.dialogue_update_available);
Button updatebtn=updatedial.findViewById(R.id.dialogue_update_updatebtn);
                                        TextView textView = updatedial.findViewById(R.id.dialogue_update_avail_detailtxtview);
                                        String str = "Available Version: " + finalVersion +
                                                "\nCurrent Version: " + currentVersion;
                                        textView.setText(str);
                                        updatebtn.setOnClickListener(v->{
                                            if (updatedial.isShowing())updatedial.dismiss();
                                            context.startActivity(new Intent(context,UpdateApp.class));
                                        });
                                        updatedial.setCancelable(true);
                                        updatedial.show();




                                    }

                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } else {
                    //initialize the update module...
                    rootref.child("update").setValue(new Update_MODEL("app name here", "app size here ", "link of apk file", "version name ", "icon url")).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            rootref.child("update").child("isforced").setValue(false);
                            Log.d(TAG, "Please Goto Firebase Database to Update the Details of Update Node ");
                            Toast.makeText(context, "Please Goto Firebase Database to Update the Details of Update Node", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Can't Initialize update module due to lack of permission");
                            Toast.makeText(context, "Can't Initialize update module due to lack of permission", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
