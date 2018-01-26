package com.example.suanysilva.streamingmediaplayer.api;

import com.example.suanysilva.streamingmediaplayer.api.models.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Suany Silva on 25/01/2018.
 */

public class Client {
    public interface Tracks{
        @GET("tracks")
        Call<List<Track>> getTracks();
    }


}
