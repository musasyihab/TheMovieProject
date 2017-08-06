package com.musasyihab.themovieproject.network;

import com.musasyihab.themovieproject.model.MovieModel;
import com.musasyihab.themovieproject.model.response.GetMovieListResponse;
import com.musasyihab.themovieproject.model.response.GetMovieReviewsResponse;
import com.musasyihab.themovieproject.model.response.GetMovieVideosResponse;
import com.musasyihab.themovieproject.util.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by musasyihab on 8/2/17.
 */

public interface Restapi {

    @GET("movie/popular?api_key="+ Constants.API_KEY)
    Call<GetMovieListResponse> getPopularMovieList();

    @GET("movie/top_rated?api_key="+ Constants.API_KEY)
    Call<GetMovieListResponse> getTopRatedMovieList();

    @GET("movie/{id}?api_key="+ Constants.API_KEY)
    Call<MovieModel> getMovieDetail(@Path("id") int id);

    @GET("movie/{id}/videos?api_key="+ Constants.API_KEY)
    Call<GetMovieVideosResponse> getMovieVideos(@Path("id") int id);

    @GET("movie/{id}/reviews?api_key="+ Constants.API_KEY)
    Call<GetMovieReviewsResponse> getMovieReviews(@Path("id") int id);
}
