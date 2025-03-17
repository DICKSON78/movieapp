package com.example.movies;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private List<Video> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }

    public Object getVideos() {
        return results;
    }

    public static class Video {
        private String videoUrl;
        private String site;
        private String thumbnailUrl;
        private String movieTitle; // Added movieTitle field

        @SerializedName("key")
        private String key;

        @SerializedName("name")
        private String name;

        @SerializedName("type")
        private String type;

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public String getVideoUrl() {
            if (site != null && key != null && site.equalsIgnoreCase("YouTube")) {
                return "https://www.youtube.com/watch?v=" + key; // Standard YouTube URL
            } else {
                return null;
            }
        }

        public String getThumbnailUrl() {
            if (site != null && key != null && site.equalsIgnoreCase("YouTube")) {
                return "https://img.youtube.com/vi/" + key + "/0.jpg"; // Standard YouTube thumbnail URL
            } else {
                return null;
            }
        }

        public String getSite() {
            if (site != null) {
                return site;
            } else {
                return null;
            }
        }

        public void setMovieTitle(String title) {
            this.movieTitle = title;
        }

        public String getMovieTitle() {
            return movieTitle;
        }
    }
}