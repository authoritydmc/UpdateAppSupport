# UpdateAppSupport

This is very simple project i did.. basically for new to android programming people who do not have play account but want to somehow  deploy necessary update to their app ,,

**This library uses Firebase database...**

It will automatically give you a update, function like download the apk file and then  open it automatically to install.. or like whether the update is forced or not....
If there is an forced update..your app won't run unless user update it.. 

This library automatically manages downloading of apk file,managing write permission and also file providers to locate and open apk to install via content:// uri instead of file:// 

This library helps you to easily deploy update just by updating links in Firebase node ... 
For more information read the readme.md 


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
	        implementation 'com.github.authoritydmc:UpdateAppSupport:1.0.0'
	}
 ```
 ## How to use

### step 1:
 
#### 1.1 Call via  method
 
##### import the class
>  import authoritydmc.UpdateApp;
 
##### call the method now

 > `UpdateApp.checkupdate(Context context,String currentVersionname,boolean shouldShowDialog);`
 
 where **currentVersionname** can be Passed via Either using **BuildConfig.VersionName** or
 via calling `getCurrentVersion();` method **[getCurrentVersion()](#code-for-getcurrentversion)** should be implemented first . 

**or**
 
#### 1.2 Start the Activity via Intent
 > `StartActvitiy(new Intent(context,UpdateApp.class));`
 
 
 
 
 
 ## Firebase Update node Field and their usuage
 
 1. **name**  `String`:- name of your app `
 2. **isforced** `boolean` :- Whether the Current Version is forced .so that app cannot run without updating.. 
 note it requires calling to checkupdate method inside Main or any parent activity
 3. **version**  `String`:-what will be version number of next update (All update logic depend on this number .)
 choose any higher number than current build version to allow update 
 4. **size** ` String`:- size of next update app file
 
 5. **app_download_url** ` String`:-url of the apk file
 
 6. **icon_url** ` String`:- url of icon of app 
 
 ![sample update node ](https://github.com/authoritydmc/UpdateAppSupport/blob/master/Assets/Screenshot_2020-01-02-22-46-28-91.png)
 
 
 

 
 
 ### EXTRAS
 ##### code for getCurrentVersion()
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

 
 ### Screenshots
 
 ##### updatecheck method when dialogue box should show is enabled
 ![checkup date](https://github.com/authoritydmc/UpdateAppSupport/blob/master/Assets/Screenshot_2020-01-02-22-40-31-51_3d5f0dd0cff8ba8714d843155c1b5f56.png)
 #### Update Activity page
 
 ![update activity](https://github.com/authoritydmc/UpdateAppSupport/blob/master/Assets/Screenshot_2020-01-02-22-40-39-28_3d5f0dd0cff8ba8714d843155c1b5f56.png)
