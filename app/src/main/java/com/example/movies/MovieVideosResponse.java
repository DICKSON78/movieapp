package com.example.movies;
import java.util.List;

public class MovieVideosResponse {

    private List<Video> results;

    public List<Video> getResults() {
        return results;
    }

    public static class Video {
        private String key;
        private String name;
        private String type;

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}