package com.moneyhawk.videobaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import com.moneyhawk.videobaseapp.api.ApiInterface;
import com.moneyhawk.videobaseapp.model.ListModel;
import com.moneyhawk.videobaseapp.model.Root;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetUploadedVideo extends AppCompatActivity {
    VideoView videoView2;
    ProgressDialog progressDialog;
    ApiInterface apiInterface;
    String uploadedurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_uploaded_video);

        videoView2 = findViewById(R.id.videoView2);

        Retrofit retrofit = com.moneyhawk.videobaseapp.api.ApiClient.getclient();
        apiInterface = retrofit.create(com.moneyhawk.videobaseapp.api.ApiInterface.class);


        progressDialog =new ProgressDialog(GetUploadedVideo.this);
        progressDialog.setTitle("Uploading ");
        progressDialog.setMessage("File is uploading please wait");

        getuploadedvideo();


    }



    public void getuploadedvideo() {



        apiInterface.getuploadedvideos().enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {

                try {
                    if (response != null) {

                        Log.e("apihit","api responce: "+ response.body());


                        if (response.body().getVideos().size()>0) {
                            //  setadapter(response.body().getData());
//                            setadapterhardware(response.body().getData());
//                            GalleryList(new Gson().toJson(response.body()).toString()+"");
                           Log.d("uploadurl","url :" + response.body().getVideos().get(0).toString());

                            uploadedurl=response.body().getVideos().get(0).toString();
                            videoView2.setVideoURI(Uri.parse(uploadedurl));
                            videoView2.start();
                        } else {
                            Toast.makeText(GetUploadedVideo.this, response.body().getVideos().get(0), Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (Exception e) {
                    Log.e("exp", e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Log.e("failure", t.getLocalizedMessage());
            }
        });

//
    }
}