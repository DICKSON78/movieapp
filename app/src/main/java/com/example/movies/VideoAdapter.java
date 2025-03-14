package com.example.movies;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<MovieResponse.Video> videoList;
    private Context context;

    public VideoAdapter(Context context, List<MovieResponse.Video> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.continue_watching_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        MovieResponse.Video video = videoList.get(position);
        String videoUrl = video.getVideoUrl();
        String thumbnailUrl = video.getThumbnailUrl();

        Log.d("ADAPTER_URL", "Position: " + position + ", Video URL: " + videoUrl + ", Thumbnail URL: " + thumbnailUrl);

        holder.setupVideoPlayback(videoUrl, thumbnailUrl);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        PlayerView playerView;
        ExoPlayer player;
        ImageView thumbnailImageView;
        Context context;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.player_view);
            thumbnailImageView = itemView.findViewById(R.id.video_thumbnail);
            context = itemView.getContext();

            player = new ExoPlayer.Builder(context).build();
            playerView.setPlayer(player);

            thumbnailImageView.setOnClickListener(v -> {
                thumbnailImageView.setVisibility(View.GONE);
                playerView.setVisibility(View.VISIBLE);
                setupExoPlayer();
            });
            playerView.setVisibility(View.GONE);

        }

        public void setupVideoPlayback(String videoUrl, String thumbnailUrl) {
            Glide.with(context)
                    .load(thumbnailUrl)
                    .into(thumbnailImageView);

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

        private void setupExoPlayer(){
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
    public void onViewDetachedFromWindow(@NonNull VideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.releasePlayer();
    }
}