package com.musasyihab.themovieproject.model.response;

import com.musasyihab.themovieproject.model.VideoModel;

import java.util.List;

/**
 * Created by musasyihab on 8/4/17.
 */

public class GetMovieVideosResponse {
    private int id;
    private List<VideoModel> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<VideoModel> getResults() {
        return results;
    }

    public void setResults(List<VideoModel> results) {
        this.results = results;
    }
}
