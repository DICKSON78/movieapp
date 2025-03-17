package com.example.movies;

public class MovieItem {

    public static final int TYPE_BANNER = 0;
    public static final int TYPE_VIDEO = 1;

    private int type;
    private Movie movie; // For banners
    private MovieResponse.Video video; // For videos

    public MovieItem(Movie movie) {
        this.type = TYPE_BANNER;
        this.movie = movie;
    }

    public MovieItem(MovieResponse.Video video) {
        this.type = TYPE_VIDEO;
        this.video = video;
    }

    public int getType() {
        return type;
    }

    public Movie getMovie() {
        return movie;
    }

    public MovieResponse.Video getVideo() {
        return video;
    }
}