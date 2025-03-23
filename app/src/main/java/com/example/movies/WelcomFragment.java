package com.example.movies;

import static androidx.core.util.TypedValueCompat.dpToPx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomFragment extends Fragment {
    private RecyclerView continueWatchingRecyclerView, latestMoviesRecyclerView;
    private final String API_KEY = "fecae410722bc227d74f320928c3f0a4";
    private ContinueWatchingAdapter continueWatchingAdapter;
    private LatestMoviesAdapter latestMoviesAdapter;
    private GoogleSignInClient mGoogleSignInClient;
    private Button SignOut;
    private TextView name;
    private ImageSlider imageSlider;
    private ApiService apiService;
    private SimpleExoPlayer player;
    private TextView videoTitleTextView;
    private Context context;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcom, container, false);

        apiService = ApiClient.getApiService();

        SignOut = view.findViewById(R.id.logout);
        name = view.findViewById(R.id.lastname);
        imageSlider = view.findViewById(R.id.bannerSlider);
        continueWatchingRecyclerView = view.findViewById(R.id.video_recycler_view);
        latestMoviesRecyclerView = view.findViewById(R.id.movies_reyclerview);
        videoTitleTextView = view.findViewById (R.id.movie_title);

        continueWatchingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        continueWatchingAdapter = new ContinueWatchingAdapter(new ArrayList<>(), new ArrayList<>());
        continueWatchingRecyclerView.setAdapter(continueWatchingAdapter);

        latestMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        latestMoviesAdapter = new LatestMoviesAdapter(new ArrayList<>());
        latestMoviesRecyclerView.setAdapter(latestMoviesAdapter);

        int horizontalSpacing = (int) dpToPx(8, getContext());
        int verticalSpacing = dpToPx(10, getContext());
        continueWatchingRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(verticalSpacing, horizontalSpacing));
        latestMoviesRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(verticalSpacing, horizontalSpacing));

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            name.setText(account.getDisplayName());
        }

        SignOut.setOnClickListener(v -> signOut());

        fetchAndSetSliderImages(imageSlider);
        loadContinueWatching();
        addDefaultContinueWatching();
        fetchPopularMoviesForLatest();

        return view;
    }

    private void addDefaultContinueWatching() {
        if (getContinueWatchingList().isEmpty()) {
            setupVideoPlayback(89);
        }
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
        private final int verticalSpaceHeight;
        private final int horizontalSpaceWidth;

        public RecyclerViewItemDecoration(int verticalSpaceHeight, int horizontalSpaceWidth) {
            this.verticalSpaceHeight = verticalSpaceHeight;
            this.horizontalSpaceWidth = horizontalSpaceWidth;
        }

        @Override
        public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = horizontalSpaceWidth;
            outRect.right = horizontalSpaceWidth;
            outRect.bottom = verticalSpaceHeight;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = verticalSpaceHeight;
            } else {
                outRect.top = 0;
            }
        }
    }

    public void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            startActivity(new Intent(getContext(), MainActivity.class));
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void fetchAndSetSliderImages(ImageSlider imageSlider) {
        ApiService apiService = ApiClient.getApiService();
        Call<MovieDetails> call = apiService.getPopularMovies(API_KEY);

        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (imageSlider != null) imageSlider.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    List<SlideModel> slideModels = new ArrayList<>();

                    if (movies != null) {
                        for (Movie movie : movies) {
                            String posterPath = movie.getPosterPath();
                            if (posterPath != null && !posterPath.isEmpty()) {
                                String imageUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                                slideModels.add(new SlideModel(imageUrl, ScaleTypes.CENTER_CROP));
                            }
                        }
                    }
                    if (imageSlider != null) imageSlider.setImageList(slideModels);

                    if (imageSlider != null) {
                        imageSlider.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onItemSelected(int position) {
                                if (movies != null && position >= 0 && position < movies.size()) {
                                    Movie selectedMovie = movies.get(position);
                                    if (selectedMovie != null) {
                                        openMovieDetails(selectedMovie.getId());
                                    } else {
                                        Log.e("ImageSlider", "Selected movie is null at position: " + position);
                                        Toast.makeText(getContext(), "Error opening movie details.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.e("ImageSlider", "Invalid position: " + position + ", movies size: " + (movies != null ? movies.size() : "null"));
                                    Toast.makeText(getContext(), "Error opening movie details.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void doubleClick(int position) {
                                Log.d("ImageSlider", "Double click at position: " + position);
                            }
                        });
                    }
                } else {
                    if (imageSlider != null) imageSlider.setImageList(new ArrayList<>());
                    Log.e("ImageSlider", "Unsuccessful response. Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("ImageSlider", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("ImageSlider", "Error parsing error body", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                if (imageSlider != null) imageSlider.setVisibility(View.GONE);
                Log.e("Image Slider API Failure", "API call failed: " + t.getMessage());
                t.printStackTrace();

                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Image Slider Network error. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API_FAILURE", "Cannot show Toast, Fragment not added or context is null.");
                }
            }
        });
    }

    private void openMovieDetails(int movieId) {
        Intent intent = new Intent(getContext(), Film.class);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
    }

    private void loadContinueWatching() {
        List<ContinueWatchingItem> continueWatchingList = getContinueWatchingList();
        List<Movie> movieList = new ArrayList<>();
        if (!continueWatchingList.isEmpty()) {
            for (ContinueWatchingItem item : continueWatchingList) {
                fetchMovieById(item.getMovieId(), movieList);
            }
        }
    }

    private void fetchMovieById(int movieId, List<Movie> movies) {
        Log.d("FetchMovie", "Fetching movie with ID: " + movieId);

        ApiService apiService = ApiClient.getApiService();
        Call<MovieListResponse> call = apiService.getMovieById(movieId, API_KEY);

        call.enqueue(new Callback<MovieListResponse>() {
            @Override
            public void onResponse(Call<MovieListResponse> call, Response<MovieListResponse> response) {
                Log.d("FetchMovie", "onResponse called for movie ID: " + movieId);

                if (response.isSuccessful() && response.body() != null) {
                    MovieListResponse movieListResponse = response.body();
                    List<Movie> movieList = movieListResponse.getResults();

                    if (movieList != null && !movieList.isEmpty()) {
                        Movie movie = movieList.get(0);

                        movies.add(movie);
                        fetchVideos(movie.getId(), movie);

                        if (movies.size() == getContinueWatchingList().size()) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    continueWatchingAdapter.setContinueWatchingList(getContinueWatchingList());
                                    continueWatchingAdapter.setMovieList(movies);
                                });
                            } else {
                                Log.e("WelcomeFragment", "getActivity() returned null. Fragment not attached.");
                            }
                        }
                    } else {
                        Log.w("FetchMovie", "No movies found for ID: " + movieId);
                    }
                } else {
                    Log.e("FetchMovie", "Failed to fetch movie with ID: " + movieId + ", code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MovieListResponse> call, Throwable t) {
                Log.e("FetchMovie", "Error fetching movie with ID: " + movieId + ", error: " + t.getMessage());
            }
        });
    }

    private void fetchVideos(int movieId, Movie movie) {
        Log.d("FetchVideos", "fetchVideos called for movie ID: " + movieId);

        ApiService apiService = ApiClient.getApiService();
        Call<MovieResponse> call = apiService.getMovieVideos(movieId, API_KEY);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                Log.d("FetchVideos", "onResponse called for movie ID: " + movieId);

                if (response.isSuccessful() && response.body() != null) {
                    MovieResponse movieResponse = response.body();
                    List<MovieResponse.Video> videos = movieResponse.getResults();

                    if (videos != null && !videos.isEmpty()) {
                        MovieResponse.Video firstVideo = videos.get(0);
                        String videoKey = firstVideo.getKey();

                        movie.setVideoKey(videoKey);

                        Log.d("FetchVideos", "Video key found: " + videoKey);
                    } else {
                        Log.w("FetchVideos", "No videos found for movie ID: " + movieId);
                    }
                } else {
                    Log.e("FetchVideos", "Unsuccessful video fetch for movie ID: " + movieId + ", code: " + response.code());
                    if(response.errorBody() != null) {
                        try {
                            Log.e("FetchVideos", "Error Body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("FetchVideos", "Error parsing error body", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("FetchVideos", "onFailure called for movie ID: " + movieId + ", error: " + t.getMessage());
            }
        });
    }

    private void saveContinueWatching(int movieId, int currentPosition) {
        List<ContinueWatchingItem> continueWatchingList = getContinueWatchingList();
        ContinueWatchingItem newItem = new ContinueWatchingItem(movieId, currentPosition);

        for (int i = 0; i < continueWatchingList.size(); i++) {
            if (continueWatchingList.get(i).getMovieId() == movieId) {
                continueWatchingList.set(i, newItem);
                saveListToPrefs(continueWatchingList);
                return;
            }
        }

        continueWatchingList.add(newItem);
        saveListToPrefs(continueWatchingList);
    }

    private List<ContinueWatchingItem> getContinueWatchingList() {
        SharedPreferences prefs = requireContext().getSharedPreferences("continue_watching", Context.MODE_PRIVATE);
        String json = prefs.getString("list", null);
        if (json == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ContinueWatchingItem>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    private void saveListToPrefs(List<ContinueWatchingItem> list) {
        SharedPreferences prefs = requireContext().getSharedPreferences("continue_watching", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("list", json);
        editor.apply();
    }

    public static class ContinueWatchingItem {
        private int movieId;
        private int currentPosition;

        public ContinueWatchingItem(int movieId, int currentPosition) {
            this.movieId = movieId;
            this.currentPosition = currentPosition;
        }

        public int getMovieId() {
            return movieId;
        }

        public int getCurrentPosition() {
            return currentPosition;
        }
    }

    public class ContinueWatchingAdapter extends RecyclerView.Adapter<ContinueWatchingAdapter.ContinueWatchingViewHolder> {
        private List<ContinueWatchingItem> continueWatchingList;
        private List<Movie> movieList;

        public ContinueWatchingAdapter(List<ContinueWatchingItem> continueWatchingList, List<Movie> movieList) {
            this.continueWatchingList = continueWatchingList;
            this.movieList = movieList;
        }

        public void setMovieList(List<Movie> movieList) {
            this.movieList = movieList;
            notifyDataSetChanged();
        }

        public void setContinueWatchingList(List<ContinueWatchingItem> continueWatchingList) {
            this.continueWatchingList = continueWatchingList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ContinueWatchingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.continue_watching_item, parent, false);
            return new ContinueWatchingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContinueWatchingViewHolder holder, int position) {
            ContinueWatchingItem item = continueWatchingList.get(position);
            Movie movie = movieList.get(position);
            if (movie != null) {
                holder.videoTitle.setText(movie.getTitle());
                String imageUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
                Glide.with(getContext()).load(imageUrl).into(holder.videoImage);

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), Film.class);
                    intent.putExtra("movieId", item.getMovieId());
                    intent.putExtra("currentPosition", item.getCurrentPosition());
                    startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return continueWatchingList.size();
        }

        public class ContinueWatchingViewHolder extends RecyclerView.ViewHolder {
            TextView videoTitle;
            ImageView videoImage;
            public ContinueWatchingViewHolder(@NonNull View itemView) {
                super(itemView);
                videoTitle = itemView.findViewById(R.id.movie_title);
                videoImage = itemView.findViewById(R.id.video_thumbnail);
            }
        }
    }

    public void setupVideoPlayback(int movieId) {
        if (player != null) {
            apiService.getMovieVideos(movieId, API_KEY).enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getResults() != null && !response.body().getResults().isEmpty()) {
                        String videoKey = response.body().getResults().get(0).getKey();

                        if (videoKey != null && !videoKey.isEmpty()) {
                            String youtubeVideoUrl = "https://www.youtube.com/watch?v=" + videoKey;
                            Uri videoUri = Uri.parse(youtubeVideoUrl);
                            MediaItem mediaItem = MediaItem.fromUri(videoUri);
                            MediaSource mediaSource = new ProgressiveMediaSource.Factory(
                                    new DefaultHttpDataSource.Factory())
                                    .createMediaSource(mediaItem);

                            player.setMediaSource(mediaSource);
                            player.prepare();
                            player.play();

                            if (videoTitleTextView != null) {
                                videoTitleTextView.setText("Video Key: " + videoKey);
                            } else {
                                Log.e("VideoPlaybackManager", "videoTitleTextView is null");
                            }

                        } else {
                            Log.e("VideoPlaybackManager", "Video key is null or empty");
                            Toast.makeText(context, "Video not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("VideoPlaybackManager", "Failed to fetch video data: " + response.message());
                        Toast.makeText(context, "Video not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                    Log.e("VideoPlaybackManager", "Network error: " + t.getMessage());
                    Toast.makeText(context, "Network error.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("VideoPlaybackManager", "Player is null in setupVideoPlayback");
            Toast.makeText(context, "Player not found.", Toast.LENGTH_SHORT).show();
        }
    }

    // Fetch popular movies for the latest movies section
    private void fetchPopularMoviesForLatest() {
        apiService.getPopularMovies(API_KEY).enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null && !response.body().getResults().isEmpty()) {
                    List<Movie> popularMovies = response.body().getResults();
                    latestMoviesAdapter.setMovies(popularMovies);
                } else {
                    Log.e("LatestMovies", "Failed to fetch popular movies.");
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Log.e("LatestMovies", "Network error fetching popular movies: " + t.getMessage());
            }
        });
    }

    // Adapter for the latest movies RecyclerView
    public class LatestMoviesAdapter extends RecyclerView.Adapter<LatestMoviesAdapter.LatestMoviesViewHolder> {
        private List<Movie> movies;

        public LatestMoviesAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        public void setMovies(List<Movie> movies) {
            this.movies = movies;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public LatestMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_view, parent, false);
            return new LatestMoviesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LatestMoviesViewHolder holder, int position) {
            Movie movie = movies.get(position);
            if (movie != null) {
                holder.movieTitle.setText(movie.getTitle());
                String imageUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterPath();
                Glide.with(getContext()).load(imageUrl).into(holder.movieImage);

                holder.itemView.setOnClickListener(v -> openMovieDetails(movie.getId()));
            }
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

        public class LatestMoviesViewHolder extends RecyclerView.ViewHolder {
            TextView movieTitle;
            ImageView movieImage;

            public LatestMoviesViewHolder(@NonNull View itemView) {
                super(itemView);
                movieTitle = itemView.findViewById(R.id.movie_title);
                movieImage = itemView.findViewById(R.id.movie_image);
            }
        }
    }
}