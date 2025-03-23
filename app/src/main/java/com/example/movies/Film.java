package com.example.movies;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Film extends AppCompatActivity {
    ProgressDialog progressDialog;

    private ImageView filmBanner;
    private AppCompatButton backButton, rateButton, watchNowButton, watchTrailerButton, likeButton, bookmarkButton, shareButton, downloadButton;
    private TextView movieTitleTextView, movieDescriptionTextView, movieDurationTextView, audioTextView, subtitleTextView, movieGenresText, movieReleaseDateText;
    private RecyclerView castRecyclerView;
    private CastAdapter castAdapter;

    private final String API_KEY = "fecae410722bc227d74f320928c3f0a4";
    private int movieId;
    private String moviePosterPath;

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_film);

        // Initialize progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);

        // Initialize UI elements
        filmBanner = findViewById(R.id.film_banner);
        backButton = findViewById(R.id.back);
        rateButton = findViewById(R.id.rate);
        watchNowButton = findViewById(R.id.watch_now);
        watchTrailerButton = findViewById(R.id.watch_trailer);
        likeButton = findViewById(R.id.like);
        bookmarkButton = findViewById(R.id.bookmark);
        shareButton = findViewById(R.id.share);
        downloadButton = findViewById(R.id.download);
        movieTitleTextView = findViewById(R.id.movie_title);
        movieDescriptionTextView = findViewById(R.id.movie_description);
        movieDurationTextView = findViewById(R.id.movie_duration_text);
        movieGenresText = findViewById(R.id.movie_genres_text);
        audioTextView = findViewById(R.id.audiomark);
        subtitleTextView = findViewById(R.id.subtitle);
        castRecyclerView = findViewById(R.id.cast_reycleview);
        castRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Retrieve movie ID and poster path from Intent
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            movieId = intent.getExtras().getInt("movieId");
            moviePosterPath = intent.getExtras().getString("moviePosterPath");

            // Fetch movie details and cast information
            fetchMovieDetails(movieId);

            // Load movie poster image if available
            if (moviePosterPath != null) {
                String imageUrl = "https://image.tmdb.org/t/p/w500" + moviePosterPath;
                Glide.with(this).load(imageUrl).into(filmBanner);
            }
        }

        // Set click listeners for buttons
        backButton.setOnClickListener(v -> onBackPressed());
        watchNowButton.setOnClickListener(v -> fetchMovieVideosFromTMDB(movieId));
        watchTrailerButton.setOnClickListener(v -> Toast.makeText(this, R.string.watch_trailer_clicked, Toast.LENGTH_SHORT).show());
        likeButton.setOnClickListener(v -> Toast.makeText(this, R.string.liked, Toast.LENGTH_SHORT).show());
        bookmarkButton.setOnClickListener(v -> Toast.makeText(this, R.string.bookmarked, Toast.LENGTH_SHORT).show());

        // Implement share button functionality
        shareButton.setOnClickListener(v -> {
            String movieTitle = movieTitleTextView.getText().toString();
            String movieDescription = movieDescriptionTextView.getText().toString();
            String shareMessage = getString(R.string.share_message, movieTitle, movieDescription);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            try {
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, R.string.no_sharing_app, Toast.LENGTH_SHORT).show();
            }
        });
        downloadButton.setOnClickListener(v -> Toast.makeText(this, R.string.downloaded, Toast.LENGTH_SHORT).show());
    }

    // Fetches movie details from the API
    private void fetchMovieDetails(int movieId) {
        progressDialog.show();
        ApiService apiService = ApiClient.getApiService();
        Call<MovieDetails> call = apiService.getMovieDetails(movieId, API_KEY);
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetails movieDetails = response.body();
                    updateUI(movieDetails);
                    fetchCastAndCrew(movieId); // Call fetchCastAndCrew after fetching movie details
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                progressDialog.dismiss();
                handleApiFailure(t);
            }
        });
    }

    // Fetches cast and crew information from the API
    private void fetchCastAndCrew(int movieId) {
        ApiService apiService = ApiClient.getApiService();
        Call<CreditResponse> call = apiService.getMovieCredits(movieId, API_KEY);
        call.enqueue(new Callback<CreditResponse>() {
            @Override
            public void onResponse(Call<CreditResponse> call, Response<CreditResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreditResponse CreditResponse = response.body();
                    List<Cast> cast = CreditResponse.getCast();

                    castAdapter = new CastAdapter(cast);
                    castRecyclerView.setAdapter(castAdapter);

                    setupCastClickListener();
                } else {
                    Log.e("API_RESPONSE", "Unsuccessful response: " + response.code());
                    Toast.makeText(Film.this, "API Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreditResponse> call, Throwable t) {
                Log.e("API_FAILURE", "API call failed: " + t.getMessage());
                Toast.makeText(Film.this, "API Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Sets up click listener for cast items in RecyclerView
    private void setupCastClickListener() {
        if (castRecyclerView != null && castAdapter != null) {
            castAdapter.setOnItemClickListener(new CastAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Cast cast) {
                    if (cast != null) {
                        openActorDetails(cast);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error opening actor details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onImageClick(Cast cast) {
                    if (cast != null) {
                        openActorDetails(cast);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error opening actor details", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Opens ActorActivity to display actor details
    private void openActorDetails(Cast cast) {
        if (cast != null) {
            Intent intent = new Intent(this, ActorActivity.class);
            intent.putExtra("castId", cast.getId());
            intent.putExtra("castProfilePath", cast.getProfilePath());
            intent.putExtra("castBiography",cast.getBiography());
            startActivity(intent);
        } else {
            Log.e("Film", "openActorDetails: Cast object is null");
            Toast.makeText(this, "Error opening actor details", Toast.LENGTH_SHORT).show();
        }
    }

    // RecyclerView Adapter for displaying cast information
    private class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

        private List<Cast> castList;
        private OnItemClickListener listener;
        private Context context;

        // Constructor to initialize the adapter with cast data
        public CastAdapter(List<Cast> castList) {
            this.context = context;
            this.castList = castList;
        }

        // Interface for handling item clicks
        public interface OnItemClickListener {
            void onItemClick(Cast cast);

            void onImageClick(Cast cast);
        }

        // Sets the item click listener
        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        // Creates a new ViewHolder for each item
        @NonNull
        @Override
        public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_item, parent, false);
            return new CastViewHolder(itemView);
        }

        // Binds data to the ViewHolder for each item
        @Override
        public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
            if (castList != null && position < castList.size()) {
                Cast castMember = castList.get(position);

                holder.castName.setText(castMember.getName());

                String imageUrl = "https://image.tmdb.org/t/p/w200" + castMember.getProfilePath();

                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.person)
                        .error(R.drawable.person)
                        .into(holder.castImage);

                // ✅ Save the correct context here
                Context safeContext = holder.itemView.getContext();

                holder.castImage.setOnClickListener(v -> {
                    if (safeContext != null) {
                        Intent intent = new Intent(safeContext, ActorActivity.class);
                        intent.putExtra("actor_name", castMember.getName());
                        intent.putExtra("actor_bio", castMember.getBiography());
                        safeContext.startActivity(intent);
                    } else {
                        Log.e("CastAdapter", "Context is null when trying to start ActorDetailActivity");
                    }
                });

                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(castMember);
                    }
                });
            }
        }


        // Returns the number of items in the list
        @Override
        public int getItemCount() {
            return castList != null ? castList.size() : 0;
        }

        // ViewHolder class to hold views for each item
        public class CastViewHolder extends RecyclerView.ViewHolder {
            ImageView castImage;
            TextView castName;

            public CastViewHolder(View itemView) {
                super(itemView);
                castImage = itemView.findViewById(R.id.actorImage);
                castName = itemView.findViewById(R.id.actorName);
            }
        }
    }


    // Updates UI with movie details
    private void updateUI(MovieDetails movieDetails) {
        if (movieDetails != null) {
            updateTextView(movieTitleTextView, movieDetails.getTitle(), getString(R.string.unknown), "movieTitleTextView");
            updateGenresText(movieDetails.getGenres());
            updateTextView(movieReleaseDateText, movieDetails.getYear(), getString(R.string.release_date_unavailable), "movieReleaseDateText");
            updateTextView(movieDescriptionTextView, movieDetails.getOverview(), getString(R.string.description_unavailable), "movieDescriptionTextView");
            updateDurationText(movieDetails.getRuntime());
            updateRateButton(movieDetails.getVoteAverage(), movieDetails.getYear(), movieDetails.getImdbId());
            updateLikeButton(movieDetails.getVoteCount());
            updateFilmBanner(movieDetails.getPosterPath());
            updateAudioTextView(movieDetails.getSpokenLanguages());
            updateTextView(subtitleTextView, movieDetails.getSubtitle(), getString(R.string.subtitles_default), "subtitleTextView");
        } else {
            Log.e("Film", "movieDetails is null!");
        }
    }

    // Updates TextView with value or default if value is null
    private void updateTextView(TextView textView, String value, String defaultValue, String logTag) {
        if (textView != null) {
            if (value != null) {
                textView.setText(value);
                Log.d("Film", logTag + " updated: " + value);
            } else {
                textView.setText(defaultValue);
                Log.d("Film", logTag + " updated: " + defaultValue + " (API returned null)");
            }
        } else {
            Log.e("Film", logTag + " is null!");
        }
    }

    // Updates genres TextView with formatted genres
    private void updateGenresText(Object genresObject) {
        if (movieGenresText != null) {
            String genresString = (String) genresObject;
            if (genresString != null && !genresString.isEmpty()) {
                movieGenresText.setText(genresString);
                Log.d("Film", "movieGenresText updated: " + genresString);
            } else {
                movieGenresText.setText(getString(R.string.genres_not_available));
                Log.d("Film", "movieGenresText updated: Genres not available");
            }
        } else {
            Log.e("Film", "movieGenresText is null!");
        }
    }

    // Updates duration TextView with formatted runtime
    private void updateDurationText(int runtime) {
        if (movieDurationTextView != null) {
            if (runtime > 0) {
                int hours = runtime / 60;
                int minutes = runtime % 60;
                movieDurationTextView.setText(getString(R.string.duration_format, hours, minutes));
                Log.d("Film", "movieDurationTextView updated: " + hours + "h " + minutes + "m");
            } else {
                movieDurationTextView.setText(getString(R.string.unknown));
                Log.d("Film", "movieDurationTextView updated: Unknown");
            }
        } else {
            Log.e("Film", "movieDurationTextView is null!");
        }
    }

    // Updates rate button with formatted vote average and year
    private void updateRateButton(double voteAverage, String year, String imdbId) {
        if (rateButton != null) {
            if (imdbId != null && !imdbId.equals("91")) {
                rateButton.setText(getString(R.string.rate_format, voteAverage, year));
                Log.d("Film", "rateButton updated: " + imdbId);
            } else {
                rateButton.setText(getString(R.string.imdb_id_unavailable));
                Log.d("Film", "rateButton updated: IMDb ID Unavailable");
                if (imdbId != null && imdbId.equals("91")) {
                    Log.w("Film", "IMDB ID was 91, replaced with Unavailable");
                }
            }
        } else {
            Log.e("Film", "rateButton is null!");
        }
    }

    // Updates like button with vote count
    private void updateLikeButton(int voteCount) {
        if (likeButton != null) {
            if (voteCount >= 0 && voteCount != 91) {
                likeButton.setText(String.valueOf(voteCount));
                Log.d("Film", "likeButton updated: " + voteCount);
            } else {
                likeButton.setText(getString(R.string.vote_count_unavailable));
                Log.d("Film", "likeButton updated: Vote Count Unavailable");
                if (voteCount == 91) {
                    Log.w("Film", "Vote Count was 91, replaced with Unavailable");
                }
            }
        } else {
            Log.e("Film", "likeButton is null!");
        }
    }

    // Updates film banner with movie poster image
    private void updateFilmBanner(String posterPath) {
        if (filmBanner != null) {
            if (posterPath != null) {
                String imageUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                Glide.with(this).load(imageUrl).into(filmBanner);
                Log.d("Film", "filmBanner updated with image: " + imageUrl);
            } else {
                Log.d("Film", "filmBanner posterPath was null");
            }
        } else {
            Log.e("Film", "filmBanner is null!");
        }
    }

    // Updates audio TextView with spoken languages
    private void updateAudioTextView(List<MovieDetails.SpokenLanguage> spokenLanguages) {
        if (audioTextView != null) {
            StringBuilder audioBuilder = new StringBuilder(getString(R.string.audio_track_label));
            if (spokenLanguages != null && !spokenLanguages.isEmpty()) {
                for (int i = 0; i < spokenLanguages.size(); i++) {
                    audioBuilder.append(spokenLanguages.get(i).getName());
                    if (i < spokenLanguages.size() - 1) {
                        audioBuilder.append(", ");
                    }
                }
            } else {
                audioBuilder.append(getString(R.string.unknown));
            }
            audioTextView.setText(audioBuilder.toString());
            Log.d("Film", "audioTextView updated: " + audioBuilder.toString());
        } else {
            Log.e("Film", "audioTextView is null!");
        }
    }

    // Fetches movie videos from TMDB
    private void fetchMovieVideosFromTMDB(int movieId) {
        ApiService apiService = ApiClient.getApiService();
        Call<MovieVideosResponse> call = apiService.getMoviesVideos(movieId, API_KEY);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MovieVideosResponse> call, Response<MovieVideosResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieVideosResponse movieVideosResponse = response.body();
                    List<MovieVideosResponse.Video> videos = movieVideosResponse.getResults();
                    openVideo(videos);
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(Call<MovieVideosResponse> call, Throwable t) {
                handleApiFailure(t);
            }
        });
    }

    // Opens a video from the list of videos
    private void openVideo(List<MovieVideosResponse.Video> videos) {
        if (videos != null && !videos.isEmpty()) {
            String videoKey = null;
            // Find a trailer or teaser video
            for (MovieVideosResponse.Video video : videos) {
                if (video.getType().equalsIgnoreCase("Trailer") || video.getType().equalsIgnoreCase("Teaser")) {
                    videoKey = video.getKey();
                    break;
                }
            }
            // If no trailer or teaser found, use the first video
            if (videoKey == null && !videos.isEmpty()) {
                videoKey = videos.get(0).getKey();
            }

            if (videoKey != null) {
                // Construct YouTube URL and open in browser
                String youtubeUrl = "https://www.youtube.com/watch?v=" + videoKey;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(this, R.string.no_app_to_play_video, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Film.this, R.string.no_video_available, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Film.this, R.string.no_video_available, Toast.LENGTH_SHORT).show();
        }
    }

    // Handles API errors
    private void handleApiError(Response<?> response) {
        Log.e("API_RESPONSE", "Unsuccessful response: " + response.code());
        if (response.errorBody() != null) {
            try {
                Log.e("API_RESPONSE", "Error body: " + response.errorBody().string());
            } catch (IOException e) {
                Log.e("API_RESPONSE", "Error reading error body", e);
            }
        }
        Toast.makeText(Film.this, R.string.failed_to_load_data, Toast.LENGTH_SHORT).show();
    }

    // Handles API failures
    private void handleApiFailure(Throwable t) {
        Log.e("API_FAILURE", "API call failed: " + t.getMessage());
        t.printStackTrace();
        Toast.makeText(Film.this, R.string.network_error, Toast.LENGTH_SHORT).show();
    }
}