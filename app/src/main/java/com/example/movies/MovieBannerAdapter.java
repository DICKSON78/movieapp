package com.example.movies;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Log;

import java.util.List;

public class MovieBannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<MovieItem> itemList;
    private final OnMovieClickListener movieClickListener;

    public MovieBannerAdapter(Context context, List<MovieItem> itemList, OnMovieClickListener movieClickListener) {
        this.context = context;
        this.itemList = itemList;
        this.movieClickListener = movieClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MovieItem.TYPE_BANNER) {
            View view = LayoutInflater.from(context).inflate(R.layout.movies_view, parent, false);
            return new MovieBannerViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.continue_watching_item, parent, false);
            return new VideoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MovieItem item = itemList.get(position);
        if (item.getType() == MovieItem.TYPE_BANNER) {
            MovieBannerViewHolder bannerHolder = (MovieBannerViewHolder) holder;
            Movie movie = item.getMovie();
            String posterPath = movie.getPosterPath();
            if (posterPath != null && !posterPath.isEmpty()) {
                String imageUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                Glide.with(context).load(imageUrl).into(bannerHolder.bannerImageView);
            }
        } else {
            VideoViewHolder videoHolder = (VideoViewHolder) holder;
            MovieResponse.Video video = item.getVideo();
            String videoUrl = video.getVideoUrl();
            String thumbnailUrl = video.getThumbnailUrl();
            String movieTitle = video.getMovieTitle();
            videoHolder.setupVideoPlayback(videoUrl, thumbnailUrl, movieTitle);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    public static class MovieBannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImageView;

        public MovieBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImageView = itemView.findViewById(R.id.movie_image);

            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    RecyclerView recyclerView = (RecyclerView) itemView.getParent(); // Get RecyclerView
                    MovieBannerAdapter adapter = (MovieBannerAdapter) recyclerView.getAdapter(); // Get adapter
                    MovieItem item = adapter.itemList.get(getAdapterPosition()); // Get item

                    if (item.getType() == MovieItem.TYPE_BANNER) {
                        adapter.movieClickListener.onMovieClick(item.getMovie().getId());
                    }
                }
            });
        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        PlayerView playerView;
        ExoPlayer player;
        ImageView thumbnailImageView;
        Context context;
        TextView titleTextView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.player_view);
            thumbnailImageView = itemView.findViewById(R.id.video_thumbnail);
            context = itemView.getContext();
            titleTextView = itemView.findViewById(R.id.movie_title);

            player = new ExoPlayer.Builder(context).build();
            playerView.setPlayer(player);

            thumbnailImageView.setOnClickListener(v -> {
                thumbnailImageView.setVisibility(View.GONE);
                playerView.setVisibility(View.VISIBLE);
                setupExoPlayer();
            });
            playerView.setVisibility(View.GONE);
        }

        public void setupVideoPlayback(String videoUrl, String thumbnailUrl, String movieTitle) {
            Glide.with(context)
                    .load(thumbnailUrl)
                    .into(thumbnailImageView);

            if (movieTitle != null && !movieTitle.isEmpty()) {
                titleTextView.setText(movieTitle);
            } else {
                titleTextView.setText("Unknown Movie"); //Changed from "Unknown" to "Unknown Movie"
            }

            if (videoUrl != null && !videoUrl.isEmpty()) {
                try {
                    MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
                    player.setMediaItem(mediaItem);
                } catch (Exception e) {
                    Log.e("VideoAdapter", "Error creating MediaItem: " + e.getMessage(), e);
                    Toast.makeText(context, "Error loading video.", Toast.LENGTH_SHORT).show();
                    playerView.setVisibility(View.GONE);
                    thumbnailImageView.setVisibility(View.VISIBLE);
                }
            } else {
                Log.e("VideoAdapter", "Video URL is null or empty.");
                Toast.makeText(context, "Video URL is missing.", Toast.LENGTH_SHORT).show();
                playerView.setVisibility(View.GONE);
                thumbnailImageView.setVisibility(View.VISIBLE);
            }
        }

        private void setupExoPlayer() {
            player.prepare();
            player.play();

            player.addListener(new com.google.android.exoplayer2.Player.Listener() {
                @Override
                public void onPlayerError(com.google.android.exoplayer2.PlaybackException error) {
                    Log.e("VideoAdapter", "ExoPlayer error: " + error.getMessage(), error);
                    Toast.makeText(context, "Error playing video: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    playerView.setVisibility(View.GONE);
                    thumbnailImageView.setVisibility(View.VISIBLE);
                }
            });
        }

        public void releasePlayer() {
            if (player != null) {
                player.release();
                player = null;
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof VideoViewHolder) {
            ((VideoViewHolder) holder).releasePlayer();
        }
    }

    public interface OnMovieClickListener {
        void onMovieClick(int movieId);
    }
}