package com.example.daewon.urscampusmap;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface SpotService
{
    @Multipart
    @POST("spots/")
    Call<Spot> createSpot(
            @Header("Authorization") String authorization,
//            @Part("image\"; filename=\"myfile.jpg\" ") RequestBody file,
            @PartMap Map<String, RequestBody> params

            );

}
