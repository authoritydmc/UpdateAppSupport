# UpdateAppSupport
This Library aims to help newbie developers to Push Update to their app without Using Google PlayStore...

## Basic Requirement



### 1. Connect your app to Firebase and Enable Realtime Database


    while enabling Realtime Database try in `test mode` i.e both read and write permission are true

### 2. Goto your project Level Build.gradle and add following

  ```
  dependencies {
  
         classpath 'com.google.gms:google-services:4.2.0'
          //this is required1
       }
  ```
  
  and
  
  ```
  
  repositories {
			...
			maven { url 'https://jitpack.io' }
		}
  
  ```
### 3. Goto Your App level Build.gradle and add the Following
 
 ```
 apply plugin: 'com.google.gms.google-services' //this is required 2
 
 
 
 ```
 **and**
 please use the latest Release version to ensure Compability 
 ```
 	dependencies {
	        implementation 'com.github.authoritydmc:UpdateAppSupport:0.2.2'
	}
 ```
 ## How to use
 
 ### step 1:
>  import com.vastgk.updateapp.UpdateApp;
 
### step 2:
##### 2.1 Call via  method 
 > `UpdateApp.checkupdate(Context context,String currentVersionname,boolean shouldShowDialog);`
 
 where **currentVersionname** can be Passed via Either using **BuildConfig.VersionName** or
 via calling `getCurrentVersion();` method getCurrentVersion() should be implemented first . 
 ```
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
```
**or**
 
##### 2.2 Start the Activity via Intent
 > `StartActvitiy(new Intent(context,com.vastgk.updateapp.UpdateApp.class));`
 
 
 
 
 
 ## Firebase Update node Field and their usuage
 
 1. **name**  `String`:- name of your app `
 2. **isforced** `boolean` :- Whether the Current Version is forced .so that app cannot run without updating.. 
 note it requires calling to checkupdate method inside Main or any parent activity
 3. **version**  `String`:-what will be version number of next update (All update logic depend on this number .)
 choose any higher number than current build version to allow update 
 4. **size** ` String`:- size of next update app file
 
 5. **app_download_url** ` String`:-url of the apk file
 
 6. **icon_url** ` String`:- url of icon of app 
 
