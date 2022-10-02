package com.example.learning.Adapter;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learning.BuildConfig;
import com.example.learning.Constant;
import com.example.learning.MainActivity;
import com.example.learning.Method;
import com.example.learning.R;
import com.example.learning.exoPlayerTesting;
import com.example.learning.videoDetails;
import com.example.learning.videoRename;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class RVAdapterLocker extends RecyclerView.Adapter<RVAdapterLocker.FileLayoutHolder>{

    private final Context mContext;
    public ActionMode actionMode;
    public int counter=0;
    public static final ArrayList<Boolean> isSelectedLocker = new ArrayList<>();
    public boolean isFromLongPressLocker = false;
    int selectedPosition = 0;
    public SharedPreferences lockerFileLocation;

    public RVAdapterLocker(Context mContext){
        this.mContext=mContext;
    }
    @NotNull
    public FileLayoutHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType){
        lockerFileLocation = mContext.getSharedPreferences("lockerFileLocation",Context.MODE_PRIVATE);
        View view;
        if (MainActivity.layoutValue == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_grid_view, parent, false);
        }
        Collections.sort(Constant.allVideoInLocker);
        switch (MainActivity.sortByValue) {
            case 2:
                Collections.reverse(Constant.allVideoInLocker);
                break;
            case 3:
                long[] allDates = new long[Constant.allVideoInLocker.size()];
                File[] allFileList = new File[Constant.allVideoInLocker.size()];
                for (int i = 0; i < Constant.allVideoInLocker.size(); i++) {
                    allDates[i] = Constant.allVideoInLocker.get(i).lastModified();
                    allFileList[i] = Constant.allVideoInLocker.get(i);
                }
                for (int i = 0; i < Constant.allVideoInLocker.size(); i++) {
                    for (int j = i + 1; j < Constant.allVideoInLocker.size(); j++) {
                        if (allDates[i] > allDates[j]) {
                            long temp = allDates[i];
                            allDates[i] = allDates[j];
                            allDates[j] = temp;

                            File tempFile = allFileList[i];
                            allFileList[i] = allFileList[j];
                            allFileList[j] = tempFile;
                        }
                    }
                }
                Constant.allVideoInLocker.clear();
                Constant.allVideoInLocker.addAll(Arrays.asList(allFileList));
                break;
            default:
                Collections.sort(Constant.allVideoInLocker);
                break;
        }
        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileLayoutHolder holder, int position) {
        String  fileName = Constant.allVideoInLocker.get(holder.getBindingAdapterPosition()).getName();
        ((FileLayoutHolder)holder).TextViewTitle.setText(fileName);

        Uri uri = FileProvider.getUriForFile(mContext,
                BuildConfig.APPLICATION_ID +".provider",
                Constant.allVideoInLocker.get(holder.getBindingAdapterPosition()));

        try {
            Glide
                    .with(mContext)
                    .load(uri)
                    .error(R.drawable.ic_broker_thumbnail)
                    .into(((RVAdapterLocker.FileLayoutHolder) holder).ImageViewThumbnail);
        }
        catch (Exception e){
            holder.ImageViewThumbnail.setImageResource(R.drawable.ic_video);
        }

        if (isSelectedLocker.get(holder.getBindingAdapterPosition())) {
            holder.itemView.setBackgroundResource(R.drawable.button_effect);
        }
        else {
            holder.itemView.setBackgroundResource(R.drawable.shadow_background_up);
        }

        holder.itemView.setOnClickListener(v -> {
            if (isFromLongPressLocker) {
                if (isSelectedLocker.get(holder.getBindingAdapterPosition())) {
                    holder.itemView.setBackgroundResource(R.drawable.shadow_background_up);
                    isSelectedLocker.set(holder.getBindingAdapterPosition(), false);
                    counter--;
                    if (counter <= 0) {
                        try {
                            actionMode.finish();
                        } catch (Exception ignored) {

                        }
                        makeDefault();
                    }
                } else {
                    if (actionMode == null) {
                        actionMode = ((AppCompatActivity) mContext).startActionMode(actionModeCallback);
                    }
                    counter++;
                    holder.itemView.setBackgroundResource(R.drawable.button_effect);
                    isSelectedLocker.set(holder.getBindingAdapterPosition(), true);
                }
            } else {
                selectedPosition = holder.getBindingAdapterPosition();

                //for opening file from file manager
                exoPlayerTesting.specialBool = true;
                //for opening file from file manager above

                Intent intent = new Intent(mContext, exoPlayerTesting.class);//testing
                intent.putExtra("position", holder.getBindingAdapterPosition());
                intent.putExtra("adapterFinder", 7);
                mContext.startActivity(intent);
            }
        });
        //single click listener above

        //long press listener
        holder.itemView.setOnLongClickListener(view -> {
            if (isSelectedLocker.get(holder.getBindingAdapterPosition())) {
                isSelectedLocker.set(holder.getBindingAdapterPosition(), false);
                holder.itemView.setBackgroundResource(R.drawable.shadow_background_up);
                counter--;
                if (counter <= 0) {
                    try {
                        actionMode.finish();
                    } catch (Exception ignored) {

                    }
                    isFromLongPressLocker = false;
                }
            } else {
                counter++;
                isFromLongPressLocker = true;
                isSelectedLocker.set(holder.getBindingAdapterPosition(), true);
                if (actionMode == null) {
                    actionMode = ((AppCompatActivity) mContext).startActionMode(actionModeCallback);
                }
                holder.itemView.setBackgroundResource(R.drawable.button_effect);
            }
            return true;
        });
        //long press listener above

        holder.imageViewMore.setOnClickListener(view -> showBottomDialog(holder.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return Constant.allVideoInLocker.size();
    }


    protected static class FileLayoutHolder extends RecyclerView.ViewHolder{

        final ImageView ImageViewThumbnail,imageViewMore;
        final TextView TextViewTitle;
        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);
            ImageViewThumbnail=itemView.findViewById(R.id.ImageViewThumbnail);
            TextViewTitle=itemView.findViewById(R.id.TextViewTitle);
            imageViewMore =itemView.findViewById(R.id.ImageButtonMore);
        }
    }

    private void showBottomDialog(final int position){
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(R.layout.dialog_options);

        Window window = dialog.getWindow();
        if(window != null){
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        Button buttonRename = dialog.findViewById(R.id.buttonRename);
        Button buttonDetails = dialog.findViewById(R.id.buttonDetails);
        Button buttonShare = dialog.findViewById(R.id.buttonShare);
        Button buttonDelete = dialog.findViewById(R.id.buttonDelete);

        if(buttonDelete != null)
        buttonDelete.setOnClickListener(view -> {
            final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setTitle("Delete file");
            alertDialog.setMessage("Do you want to delete those file?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    (dialog1, which) -> {
                        if (Constant.allVideoInLocker.get(position).delete()) {
                            Constant.allVideoInLocker.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(mContext, "deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "failed to delete", Toast.LENGTH_SHORT).show();
                        }
                        alertDialog.dismiss();
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", (dialogInterface, i) -> alertDialog.dismiss());
            alertDialog.show();
            dialog.dismiss();
        });

        if(buttonRename != null)
        buttonRename.setOnClickListener(view -> {
            dialog.dismiss();
            Constant.renameFile = Constant.allVideoInLocker.get(position);
            Constant.renameFilePos = position;
            Constant.renameFileCheck=4;
            Intent intent = new Intent(mContext, videoRename.class);
            mContext.startActivity(intent);
        });

        if(buttonDetails != null)
        buttonDetails.setOnClickListener(view -> {
            dialog.dismiss();
            Method.videoDetails(Constant.allVideoInLocker.get(position));
            Intent intent = new Intent(mContext, videoDetails.class);
            mContext.startActivity(intent);
        });

        if(buttonShare != null)
        buttonShare.setOnClickListener(view -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("video/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM,Constant.allVideoInLocker.get(position));
            mContext.startActivity(Intent.createChooser(sharingIntent, "Share Video"));
            dialog.dismiss();
        });
        dialog.show();
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_menu_2, menu);
            mode.setTitle("Choose your option");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if(id == R.id.delete_2) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Delete file");
                alertDialog.setMessage("Do you want to delete those file?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        (dialog, which) -> {
                            int countDelete = 0, total = 0;
                            for (int i = 0; i < isSelectedLocker.size(); i++) {
                                if (isSelectedLocker.get(i)) {
                                    total++;
                                    if (Constant.allVideoInLocker.get(i - countDelete).delete()) {
                                        countDelete++;
                                        Constant.allVideoInLocker.remove(i - countDelete + 1);
                                    }
                                }
                            }
                            Toast.makeText(mContext,
                                    "deleted " + countDelete +
                                            " file\n" + "Failed to delete " + (total - countDelete) + " file",
                                    Toast.LENGTH_SHORT).show();
                            makeDefault();
                            alertDialog.dismiss();
                            mode.finish();
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", (dialogInterface, i) -> {
                    makeDefault();
                    alertDialog.dismiss();
                    mode.finish();
                });
                alertDialog.show();
            }
            else if(id == R.id.share_2) {
                ArrayList<Uri> files = new ArrayList<>();
                for (int i = 0; i < isSelectedLocker.size(); i++) {
                    if (isSelectedLocker.get(i)) {
                        Uri uri = FileProvider.getUriForFile(mContext,
                                BuildConfig.APPLICATION_ID + ".provider",
                                Constant.allVideoInLocker.get(i));
                        files.add(uri);
                    }
                }
                Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE); //for multiple
                sharingIntent.setType("video/*");
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);//for multiple
                mContext.startActivity(Intent.createChooser(sharingIntent, "Share Video"));
                makeDefault();
                mode.finish();
            }
            else if(id == R.id.lock_2) {
                int countDelete = 0;
                for (int i = 0; i < isSelectedLocker.size(); i++) {
                    if (isSelectedLocker.get(i)) {
                        try {
                            File dest = new File(Objects.requireNonNull(lockerFileLocation.getString(
                                    String.valueOf(Constant.allVideoInLocker.get(i - countDelete)),
                                    Constant.defaultLocation)));
                            org.apache.commons.io.FileUtils.moveFile(Constant.allVideoInLocker.get(i - countDelete), dest);
                            Constant.allVideoInLocker.remove(i - countDelete);
                            countDelete++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                makeDefault();
                mode.finish();
            }
            else return false;

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            makeDefault();
        }
    };

    @SuppressLint("NotifyDataSetChanged")
    public void makeDefault() {
        isSelectedLocker.clear();
        for (int i = 0; i < Constant.allVideoInLocker.size(); i++) {
            isSelectedLocker.add(false);
        }
        counter = 0;
        isFromLongPressLocker = false;
        notifyDataSetChanged();
    }
}
