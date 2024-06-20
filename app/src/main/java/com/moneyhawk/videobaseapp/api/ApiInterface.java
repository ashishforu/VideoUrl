package com.moneyhawk.videobaseapp.api;

import com.moneyhawk.videobaseapp.model.ListModel;
import com.moneyhawk.videobaseapp.model.Root;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("api/v1/auth/login")
    Call<ListModel> loginverification(@Field("email") String email, @Field("password") String password);


    @GET("api/android2/android/list.php")
    Call<ListModel>  fetchmaincategories();

    @GET("api/android2/android/list_videos.php")
    Call<Root>  getuploadedvideos();
}
