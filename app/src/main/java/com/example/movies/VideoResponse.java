package com.example.movies;
import java.util.List;

public class VideoResponse {
    private List<VideoResult> results;

    public List<VideoResult> getResults() {
        return results;
    }

    public static class VideoResult {
        private String key;

        public String getKey() {
            return key;
        }
    }
}