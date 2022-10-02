package com.example.learning.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learning.R;
import com.example.learning.Constant;
import com.example.learning.MainActivity;
import com.example.learning.Method;
import com.example.learning.videoInFolder;

public class RVAdapterFolder extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final Context mContext;
    public static int selectedFolder=0;

    public RVAdapterFolder(Context mContext){
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_list,parent,false);
        return new RVAdapterFolder.FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        String fileName = Constant.allFolderList.get(holder.getBindingAdapterPosition()).getName();

        ((FileLayoutHolder)holder).TextViewFolderName.setText(fileName);
        ((FileLayoutHolder)holder).textViewVideoCounter.setText(
                mContext.getString(R.string.just_specifier,Constant.videoCounter.get(holder.getBindingAdapterPosition())+" videos"));


        //listener
        holder.itemView.setOnClickListener(view -> {
            MainActivity.selectedFragmentValue=5;

            //loading video from this folder
            Method.loadVideoFromFolder(holder.getBindingAdapterPosition());
            //loading video from this folder

            selectedFolder = holder.getBindingAdapterPosition();
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            MainActivity.selectedFragmentValue = 5;
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.FragmentContainer, new videoInFolder())
                    .addToBackStack(null)
                    .commit();
        });
        //listener above
    }

    @Override
    public int getItemCount() {
        return Constant.allFolderList.size();
    }

    public static class FileLayoutHolder extends RecyclerView.ViewHolder{
        final TextView TextViewFolderName;
	    final TextView textViewVideoCounter;
        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);
            TextViewFolderName = itemView.findViewById(R.id.textViewFolderName);
            textViewVideoCounter = itemView.findViewById(R.id.textViewVideoCount);
        }
    }

}