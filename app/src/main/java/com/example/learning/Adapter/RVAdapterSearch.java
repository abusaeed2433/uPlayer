package com.example.learning.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learning.BuildConfig;
import com.example.learning.Constant;
import com.example.learning.R;
import com.example.learning.exoPlayerTesting;

public class RVAdapterSearch extends RecyclerView.Adapter<RVAdapterSearch.FileLayoutHolder>{
    private final Context mContext;

    public RVAdapterSearch(Context mContext){
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public FileLayoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list, parent, false);
        return new RVAdapterSearch.FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileLayoutHolder holder, int position) {
        String  fileName = Constant.filteredMediaList.get(holder.getBindingAdapterPosition()).getName();
        holder.TextViewTitle.setText(fileName);

        Uri uri = FileProvider.getUriForFile(mContext,
                BuildConfig.APPLICATION_ID +".provider",
                Constant.filteredMediaList.get(holder.getBindingAdapterPosition()));

        Glide
                .with(mContext)
                .load(uri)
                .into(((FileLayoutHolder)holder).ImageViewThumbnail);

        holder.itemView.setOnClickListener(v -> {
            exoPlayerTesting.specialBool = true;

            Intent intent = new Intent(mContext, exoPlayerTesting.class);
            intent.putExtra("position", holder.getBindingAdapterPosition());
            intent.putExtra("adapterFinder",3);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Constant.filteredMediaList.size();
    }


    protected static class FileLayoutHolder extends RecyclerView.ViewHolder{

        final ImageView ImageViewThumbnail,imageViewMore;
        final TextView TextViewTitle;
        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);
            ImageViewThumbnail=itemView.findViewById(R.id.ImageViewThumbnail);
            TextViewTitle=itemView.findViewById(R.id.TextViewTitle);
            imageViewMore =itemView.findViewById(R.id.ImageButtonMore);
            imageViewMore.setVisibility(View.INVISIBLE);
        }
    }
}
