package com.example.movies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<Cast> castList;
    private Context context;
    private OnItemClickListener listener; // Add listener

    public CastAdapter(Context context, List<Cast> castList) {
        this.context = context;
        this.castList = castList;
    }

    public interface OnItemClickListener { // Functional interface
        void onItemClick(Cast cast);
    }

    public void setOnItemClickListener(OnItemClickListener listener) { // Corrected method
        this.listener = listener;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_item, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        if (castList != null && position < castList.size()) {
            Cast cast = castList.get(position);

            if (cast.getProfilePath() != null && !cast.getProfilePath().isEmpty()) {
                String imageUrl = "https://image.tmdb.org/t/p/w500" + cast.getProfilePath();

                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_actor)
                        .error(R.drawable.person)
                        .into(holder.castImageView);
            } else {
                holder.castImageView.setImageResource(R.drawable.placeholder_actor);
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) { // Invoke listener
                    listener.onItemClick(cast);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return castList == null ? 0 : castList.size();
    }

    public void setCastList(List<Cast> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    public static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView castImageView;
        TextView castNameTextView;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            castImageView = itemView.findViewById(R.id.actorImage);
            castNameTextView= itemView.findViewById (R.id.actorName);
        }
    }
}