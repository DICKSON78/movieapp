package com.example.movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("movie/{movie_id}/videos")
    Call<MovieResponse> getMovieVideos( // Corrected return type to VideoResponse
                                        @Path("movie_id") int movieId,
                                        @Query("api_key") String apiKey
    );

    @GET("movie/popular")
    Call<MovieDetails> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Call<CastResponse> getMovieCredits( // Corrected return type!
                                        @Path("movie_id") int movieId,
                                        @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}")
    Call<MovieDetails> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}")
    Call<MovieListResponse> getMovieById(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey);


    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(@Query("api_key") String apiKey);

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(@Query("api_key") String apiKey);

    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("movie/{movie_id}/recommendations")
    Call<MovieResponse> getMovieRecommendations(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/similar")
    Call<MovieResponse> getSimilarMovies(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("discover/movie")
    Call<MovieResponse> getMoviesByGenre(
            @Query("api_key") String apiKey,
            @Query("with_genres") String genreIds
    );

    @GET("movie/{movie_id}/videos")
    Call<MovieVideosResponse> getMoviesVideos(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );
    @GET("person/{person_id}")
    Call<Cast> getActorDetails(@Path("person_id") int personId, @Query("api_key") String apiKey);


}