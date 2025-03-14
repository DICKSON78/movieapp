package com.example.movies;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieListResponse {

    @SerializedName("results")
    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "MovieListResponse{" +
                "results=" + results +
                '}';
    }
}