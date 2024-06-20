package com.moneyhawk.videobaseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadVideo extends AppCompatActivity {

    Button  btnselectgallery,btnselectfolder,btnselectcamera,btnUpload;
    private static final int PICK_VIDEO_REQUEST = 1;
    private static final String UPLOAD_URL = "https://codingsansar.com/api/android2/android/video.php";

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_WRITE_REQUEST_CODE = 101;
    private static final int MY_READ_REQUEST_CODE = 102;
    private static final int STORAGE_PERMISSION_CODE = 100;
    final int SELECT_IMAGE = 1234;
    final int CAPTURE_VIDEO = 123344;

    private Uri videoUri;
    private String videopathis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);


        btnselectgallery= findViewById(R.id.btnselectgallery);
        btnselectfolder= findViewById(R.id.btnselectfolder);
        btnselectcamera= findViewById(R.id.btnselectcamera);
        btnUpload= findViewById(R.id.btnUpload);

//        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    MY_WRITE_REQUEST_CODE);
//        }



        btnselectfolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_READ_REQUEST_CODE);
                }

                //  Select Video from folder
                Intent intenta = new Intent();
                intenta.setType("video/*");
                intenta.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intenta, 1);

            }
        });

        btnselectcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_REQUEST_CODE);
                }

                Intent intent = new Intent(UploadVideo.this, MainActivity.class);
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intent, 2);

            }
        });


        btnselectgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    selectVideo();
                } else {
                    requestPermission();
                }
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("path3" ," path : " +videoUri );
                if (videoUri != null) {
                    uploadVideo(videoUri);
                } else {
                    Toast.makeText(UploadVideo.this, "Please select a video first", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void selectVideo() {

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 0);

    }



    private void uploadVideo(Uri videoUri) {



      //  String filePath = RealPathUtil.getRealPath(UploadVideo.this, videoUri).toString();
      //  String filePath = videoUri.toString();
        String filePath = "/storage/9C33-6BBD/DCIM/Camera/VID_20240620_081113.mp4";

          Log.d("path4: " ,filePath );
        if (filePath == null) {
            Toast.makeText(this, "File path is invalid", Toast.LENGTH_SHORT).show();
            return;
        }




    //   add

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File videoFile = new File(filePath);
                    HttpURLConnection connection = null;
                    DataOutputStream outputStream = null;

                    String boundary = "*****";
                    String twoHyphens = "--";
                    String lineEnd = "\r\n";

                    int bytesRead, bytesAvailable, bufferSize;
                    byte[] buffer;
                    int maxBufferSize = 1 * 1024 * 1024;

                    FileInputStream fileInputStream = new FileInputStream(videoFile);
                    URL url = new URL(UPLOAD_URL);

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    connection.setRequestProperty("video", filePath);

                    outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"video\";filename=\"" + filePath + "\"" + lineEnd);
                    outputStream.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        outputStream.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    int serverResponseCode = connection.getResponseCode();
                    String serverResponseMessage = connection.getResponseMessage();



                    InputStream inputStream;
                    if (serverResponseCode == HttpURLConnection.HTTP_OK) {
                        inputStream = connection.getInputStream();
                    } else {
                        inputStream = connection.getErrorStream();
                    }
                    StringBuilder response = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    String serverResponseMessage1 = response.toString();
                    Log.i("ServerResponse", "Code: " + serverResponseCode + ", Message: " + serverResponseMessage + ", "+ serverResponseMessage1);

                    fileInputStream.close();
                    outputStream.flush();
                    outputStream.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadVideo.this, "Upload complete", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadVideo.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("path4","msf is : " + e.getMessage() );
                        }
                    });
                }
            }
        }).start();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(UploadVideo.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(UploadVideo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, STORAGE_PERMISSION_CODE);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, STORAGE_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(UploadVideo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean readExternalStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean writeExternalStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (readExternalStorageAccepted && writeExternalStorageAccepted) {
                    selectVideo();
                } else {
                    Toast.makeText(UploadVideo.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri video = data.getData();
            String videopath = data.getData().getPath();
            videopathis=videopath;
            Log.d("testvideopath", "" + videopath);
            Log.d("testvideop", "" + video);
            String aa = RealPathUtil.getRealPath(UploadVideo.this, video).toString();
            Log.d("testvideoabsolute", "" + aa);
            videoUri =video;

                Intent intentw = new Intent(UploadVideo.this, VideoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("filepath", String.valueOf(video));
            bundle.putString("filepathvideo", aa);
            bundle.putInt("testing", 2);
            intentw.putExtras(bundle);
            startActivity(intentw);
          //  videoUri =Uri.parse(videopath);

            Log.d("path1","pathis :" + String.valueOf(video));
            Log.d("path2","path2is :" + aa  +  "video :" +videoUri );

        }
    }



}