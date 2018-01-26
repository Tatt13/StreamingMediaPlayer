package com.example.suanysilva.streamingmediaplayer.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Suany Silva on 25/01/2018.
 */

public class ServiceGenerator {
    private static final String BASE_URL = "https://kianda-simple-tracks-api.herokuapp.com";
    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static final Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> service){
        return retrofit.create(service);
    }
}
