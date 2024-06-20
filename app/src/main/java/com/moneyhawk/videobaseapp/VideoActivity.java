package com.moneyhawk.videobaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.moneyhawk.videobaseapp.api.ApiInterface;
import com.moneyhawk.videobaseapp.model.AppName;
import com.moneyhawk.videobaseapp.model.ListModel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VideoActivity extends AppCompatActivity {

    VideoView videoView;
    ApiInterface apiInterface;
    String fromgallery,filepathvideo,filePath;
    int a;
    ProgressDialog progressDialog;
    Button  btnupVideo,btnuploadedVideo;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final String UPLOAD_URL = "https://codingsansar.com/api/android2/android/video.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.videoView);
        btnupVideo = findViewById(R.id.btnupVideo);
        btnuploadedVideo = findViewById(R.id.btnuploadedVideo);

        Retrofit retrofit = com.moneyhawk.videobaseapp.api.ApiClient.getclient();
        apiInterface = retrofit.create(com.moneyhawk.videobaseapp.api.ApiInterface.class);


        progressDialog =new ProgressDialog(VideoActivity.this);
        progressDialog.setTitle("Uploading ");
        progressDialog.setMessage("File is uploading please wait");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fromgallery = bundle.getString("filepath");
            filepathvideo = bundle.getString("filepathvideo", "");
            a = bundle.getInt("testing");
            Log.d("testfromgallery11", "fromgal :" + fromgallery);
            Log.d("testprinturiis  in bundle", "fromgal :" + filepathvideo);
            filePath=filepathvideo;
            if (filepathvideo.isEmpty()) {
               // filepathvideo = fromdevice;
                Log.d("testprinturiis  in null", "fromgal :" + filepathvideo);
            }

        }
        gethorizontalscrollitem();

        MediaController  mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
       // videoView.setVideoURI((Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")));
       // videoView.setVideoURI((Uri.parse("https://codingsansar.com/api/android2/android/uploads/2839596-hd_1280_720_30fps.mp4")));
        videoView.setVideoURI(Uri.parse(filepathvideo));
        videoView.start();


        btnuploadedVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(VideoActivity.this,GetUploadedVideo.class);
                startActivity(intent);
            }
        });

        btnupVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission()) {
                    uploadVideo();
                } else {
                    requestPermission();
                }
            }
        });
    }

    public void gethorizontalscrollitem() {



        apiInterface.fetchmaincategories().enqueue(new Callback<ListModel>() {
            @Override
            public void onResponse(Call<ListModel> call, Response<ListModel> response) {

                try {
                    if (response != null) {

                        Log.e("apihit","api responce: "+ response.body().getMessage());


                        if (response.body().isStatus()==true) {
                          //  setadapter(response.body().getData());
//                            setadapterhardware(response.body().getData());
//                            GalleryList(new Gson().toJson(response.body()).toString()+"");



                        } else {
                            Toast.makeText(VideoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (Exception e) {
                    Log.e("exp", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<ListModel> call, Throwable t) {
                Log.e("failure", t.getLocalizedMessage());
            }
        });

//
    }



    private void uploadVideo() {
        progressDialog.show();
        Log.d("path3" ," path : " +filepathvideo );
        if (filepathvideo != null) {
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
                        // Remove this line: connection.setRequestProperty("video", filePath);

                        outputStream = new DataOutputStream(connection.getOutputStream());
                        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                        outputStream.writeBytes("Content-Disposition: form-data; name=\"video\";filename=\"" + videoFile.getName() + "\"" + lineEnd);
                        outputStream.writeBytes("Content-Type: video/mp4" + lineEnd); // You may change MIME type if needed
                        outputStream.writeBytes(lineEnd);

                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];

                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {
                            outputStream.write(buffer, 0, bytesRead);
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
                        Log.i("ServerResponse", "Code: " + serverResponseCode + ", Message: " + serverResponseMessage + ", " + serverResponseMessage1);

                        fileInputStream.close();
                        outputStream.flush();
                        outputStream.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(VideoActivity.this, "Upload complete", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(VideoActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("path4", "msf is : " + e.getMessage());
                            }
                        });
                    }
                }
            }).start();


        } else {
            Toast.makeText(VideoActivity.this, "Please select a video first", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(VideoActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(VideoActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
            ActivityCompat.requestPermissions(VideoActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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

                } else {
                    Toast.makeText(VideoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}