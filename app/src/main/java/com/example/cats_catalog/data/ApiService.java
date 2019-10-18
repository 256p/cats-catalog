package com.example.cats_catalog.data;

import com.example.cats_catalog.data.models.Cat;
import com.example.cats_catalog.data.models.Vote;
import com.example.cats_catalog.data.models.VoteResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("images/search")
    Observable<List<Cat>> getCats(@Query("limit") int limit);

    @POST("votes")
    Observable<VoteResponse> postVote(@Body Vote vote);

}
