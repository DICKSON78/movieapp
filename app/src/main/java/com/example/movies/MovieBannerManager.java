package com.example.movies;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieBannerManager {

    private final Context context;
    private final RecyclerView recyclerView;
    private final ApiService apiService;
    private final String apiKey;

    public MovieBannerManager(Context context, RecyclerView recyclerView, String apiKey) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.apiKey = apiKey;
        this.apiService = ApiClient.getApiService();
    }

    public void fetchAndDisplayPopularMoviesAndVideos() {
        Call<MovieDetails> movieCall = apiService.getPopularMovies(apiKey);
        Call<MovieResponse> videoCall = apiService.getMovieVideos(157336, apiKey);

        movieCall.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();

                    videoCall.enqueue(new Callback<MovieResponse>() {
                        @Override
                        public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<MovieResponse.Video> videos = response.body().getResults();

                                List<MovieItem> items = new ArrayList<>();
                                if (movies != null) {
                                    for (Movie movie : movies) {
                                        items.add(new MovieItem(movie));
                                    }
                                }

                                if (videos != null) {
                                    for (MovieResponse.Video video : videos) {
                                        fetchMovieTitle(video, items);
                                    }
                                }
                                if (!items.isEmpty()) {
                                    MovieBannerAdapter adapter = new MovieBannerAdapter(context, items, movieId -> openMovieDetails(movieId)); // Pass the listener
                                    recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setVisibility(android.view.View.VISIBLE);
                                } else {
                                    Log.w("MovieBannerManager", "No popular movies or videos found.");
                                    Toast.makeText(context, "No popular movies or videos found.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MovieResponse> call, Throwable t) {
                            Log.e("MovieBannerManager", "Network error: " + t.getMessage());
                            Toast.makeText(context, "Network error loading popular movies.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Log.e("MovieBannerManager", "Network error: " + t.getMessage());
                Toast.makeText(context, "Network error loading popular movies.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMovieTitle(MovieResponse.Video video, List<MovieItem> items) {
        int movieId = 157336; // Default, replace with actual ID if available

        apiService.getMovieById(movieId, apiKey).enqueue(new Callback<MovieListResponse>() {
            @Override
            public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null && !response.body().getResults().isEmpty()) {
                    String title = response.body().getResults().get(0).getTitle();
                    video.setMovieTitle(title);
                    items.add(new MovieItem(video));
                } else {
                    Log.e("MovieBannerManager", "Failed to fetch movie title for video");
                    items.add(new MovieItem(video));
                }
            }

            @Override
            public void onFailure(Call<MovieListResponse> call, Throwable t) {
                Log.e("MovieBannerManager", "Network error fetching movie title: " + t.getMessage());
                items.add(new MovieItem(video));
            }
        });
    }

    private void openMovieDetails(int movieId) {
        Intent intent = new Intent(context, Film.class);
        intent.putExtra("movieId", movieId);
        context.startActivity(intent);
    }
}