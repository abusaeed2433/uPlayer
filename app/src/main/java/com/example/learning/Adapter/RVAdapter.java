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
import android.widget.LinearLayout;
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
import com.example.learning.hidden;
import com.example.learning.videoDetails;
import com.example.learning.videoRename;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.FileLayoutHolder> {

    public static boolean isFromLongPress = false;
    private final Context mContext;
    public static ActionMode actionMode;
    public static int counter = 0;
    public static final ArrayList<Boolean> isSelected = new ArrayList<>();
    public SharedPreferences lockerFileLocation;
    public SharedPreferences.Editor lockerFileLocationEditor;
    int selectedPosition = 0;

    public RVAdapter(Context mContext)
    {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public FileLayoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        lockerFileLocation = mContext.getSharedPreferences("lockerFileLocation",Context.MODE_PRIVATE);
        if (MainActivity.layoutValue == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_grid_view, parent, false);
        }
        Collections.sort(Constant.allMediaList);
        switch (MainActivity.sortByValue) {
            case 2:
                Collections.reverse(Constant.allMediaList);
                break;
            case 3:
                long[] allDates = new long[Constant.allMediaList.size()];
                File[] allFileList = new File[Constant.allMediaList.size()];
                for (int i = 0; i < Constant.allMediaList.size(); i++) {
                    allDates[i] = Constant.allMediaList.get(i).lastModified();
                    allFileList[i] = Constant.allMediaList.get(i);
                }
                for (int i = 0; i < Constant.allMediaList.size(); i++) {
                    for (int j = i + 1; j < Constant.allMediaList.size(); j++) {
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
                Constant.allMediaList.clear();
                Constant.allMediaList.addAll(Arrays.asList(allFileList));
                break;
            default:
                Collections.sort(Constant.allMediaList);
                break;
        }
        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileLayoutHolder holder, int position) {
        String fileName = Constant.allMediaList.get(holder.getBindingAdapterPosition()).getName();
        holder.TextViewTitle.setText(fileName);

        Uri uri = FileProvider.getUriForFile(mContext,
                BuildConfig.APPLICATION_ID + ".provider",
                Constant.allMediaList.get(holder.getBindingAdapterPosition()));
        try {
            Glide
                    .with(mContext)
                    .load(uri)
                    .error(R.drawable.ic_broker_thumbnail)
                    .into(holder.ImageViewThumbnail);
        }
        catch (Exception e){
            holder.ImageViewThumbnail.setImageResource(R.drawable.ic_video);
        }

        if (isSelected.get(holder.getBindingAdapterPosition())) {
            holder.itemView.setBackgroundResource(R.drawable.button_effect);
        }
        else {
            holder.itemView.setBackgroundResource(R.drawable.shadow_background_up);
        }

        hidden.stopProgressBarLoading = true;

        // single click listener
        holder.itemView.setOnClickListener(v -> {
            if (isFromLongPress) {
                if (isSelected.get(holder.getBindingAdapterPosition())) {
                    holder.itemView.setBackgroundResource(R.drawable.shadow_background_up);
                    isSelected.set(holder.getBindingAdapterPosition(), false);
                    counter--;
                    if (counter <= 0) {
                        try {
                            actionMode.finish();
                        } catch (Exception ignored) {

                        }
                        makeDefault();
                    }
                }
                else {
                    if (actionMode == null) {
                        actionMode = ((AppCompatActivity) mContext).startActionMode(actionModeCallback);
                    }
                    counter++;
                    holder.itemView.setBackgroundResource(R.drawable.button_effect);
                    isSelected.set(holder.getBindingAdapterPosition(), true);
                }
            }
            else {
                selectedPosition = holder.getBindingAdapterPosition();

                exoPlayerTesting.specialBool = true;

                Intent intent = new Intent(mContext, exoPlayerTesting.class);

                intent.putExtra("position", holder.getBindingAdapterPosition());
                intent.putExtra("adapterFinder", 1);
                mContext.startActivity(intent);
            }
        });
        //single click listener above

        //long press listener
        holder.itemView.setOnLongClickListener(view -> {
            if (isSelected.get(holder.getBindingAdapterPosition())) {
                isSelected.set(holder.getBindingAdapterPosition(), false);
                holder.itemView.setBackgroundResource(R.drawable.shadow_background_up);
                counter--;
                if (counter <= 0) {
                    try {
                        actionMode.finish();
                    } catch (Exception ignored) {

                    }
                    isFromLongPress = false;
                }
            }
            else {
                counter++;
                isFromLongPress = true;
                isSelected.set(holder.getBindingAdapterPosition(), true);
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
        return Constant.allMediaList.size();
    }

    public static class FileLayoutHolder extends RecyclerView.ViewHolder {

        final ImageView ImageViewThumbnail,imageViewMore;
        final TextView TextViewTitle;
        final LinearLayout linearLayoutFileList;

        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);
            ImageViewThumbnail = itemView.findViewById(R.id.ImageViewThumbnail);
            TextViewTitle = itemView.findViewById(R.id.TextViewTitle);
            imageViewMore = itemView.findViewById(R.id.ImageButtonMore);
            linearLayoutFileList = itemView.findViewById(R.id.linearLayoutFileList);
        }
    }

    private void showBottomDialog(int position){
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        //dialog.requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        dialog.setContentView(R.layout.dialog_options);
        Window window = dialog.getWindow();
        if(window != null){
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        Button buttonRename = dialog.findViewById(R.id.buttonRename);
        Button buttonDetails = dialog.findViewById(R.id.buttonDetails);
        Button buttonShare = dialog.findViewById(R.id.buttonShare);
        Button buttonDelete = dialog.findViewById(R.id.buttonDelete);

        if(buttonDelete!=null)
        buttonDelete.setOnClickListener(view14 -> {
            final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setTitle("Delete file");
            alertDialog.setMessage("Do you want to delete those file?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog1, which) -> {
                if (Constant.allMediaList.get(position).delete()) {
                    try {
                        Constant.allMediaList2.remove(Constant.allMediaList.get(position));
                    }
                    catch (Exception ignored){

                    }
                    Constant.allMediaList.remove(position);
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

        if(buttonRename!=null)
        buttonRename.setOnClickListener(view13 -> {
            Constant.renameFile = Constant.allMediaList.get(position);
            Constant.renameFilePos = position;
            Constant.renameFileCheck=1;

            dialog.dismiss();
            Intent intent = new Intent(mContext,videoRename.class);
            mContext.startActivity(intent);
        });

        if(buttonDetails != null)
        buttonDetails.setOnClickListener(view12 -> {
            dialog.dismiss();
            Method.videoDetails(Constant.allMediaList.get(position));
            Intent intent = new Intent(mContext,videoDetails.class);
            mContext.startActivity(intent); //todo app crashing here :) fine
        });

        if(buttonShare != null)
        buttonShare.setOnClickListener(view1 -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("video/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM,Constant.allMediaList.get(position));
            mContext.startActivity(Intent.createChooser(sharingIntent, "Share Video"));
            dialog.dismiss();
        });
        dialog.show();
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
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
            if (id == R.id.delete) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Delete file");
                alertDialog.setMessage("Do you want to delete those file?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        (dialog, which) -> {
                            int countDelete = 0, total = 0;
                            for (int i = 0; i < isSelected.size(); i++) {
                                if (isSelected.get(i)) {
                                    total++;
                                    if (Constant.allMediaList.get(i - countDelete).delete()) {
                                        Constant.allMediaList2.remove(Constant.allMediaList.get(i - countDelete));
                                        countDelete++;
                                        Constant.allMediaList.remove(i - countDelete + 1);
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
            else if (id == R.id.share) {
                ArrayList<Uri> files = new ArrayList<>();
                for (int i = 0; i < isSelected.size(); i++) {
                    if (isSelected.get(i)) {
                        Uri uri = FileProvider.getUriForFile(mContext,
                                BuildConfig.APPLICATION_ID + ".provider",
                                Constant.allMediaList.get(i));
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
            else if (id == R.id.lock) {
                int countDelete = 0, failedCount = 0;
                lockerFileLocationEditor = lockerFileLocation.edit();
                for (int i = 0; i < isSelected.size(); i++) {
                    if (isSelected.get(i)) {
                        String dest = Constant.appLocation + Constant.allMediaList.get(i - countDelete).getName();
                        try {
                            org.apache.commons.io.FileUtils.moveFile(Constant.allMediaList.get(i - countDelete), new File(dest));
                            lockerFileLocationEditor.putString(dest, String.valueOf(Constant.allMediaList.get(i - countDelete)));
                            Constant.allMediaList.remove(i - countDelete);
                            countDelete++;
                        } catch (IOException e) {
                            failedCount++;
                            e.printStackTrace();
                        }
                    }
                }
                lockerFileLocationEditor.apply();
                if (failedCount != 0) {
                    Toast.makeText(mContext, "something went wrong\nfailed to lock " + failedCount + " files", Toast.LENGTH_SHORT).show();
                }
                makeDefault();
                mode.finish();
            }
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
        isSelected.clear();
        for (int i = 0; i < Constant.allMediaList.size(); i++) {
            isSelected.add(false);
        }
        counter = 0;
        isFromLongPress = false;
        notifyDataSetChanged();
    }

    public void saveArray(ArrayList<String> allFiles){ //not necessary
        SharedPreferences lockerFileLocation = mContext.getSharedPreferences("lockerFileLocation",Context.MODE_PRIVATE);
        SharedPreferences.Editor lockerFileLocationEditor = lockerFileLocation.edit();
        lockerFileLocationEditor.putInt("lockerFileSize",allFiles.size());
        for(int i=0;i<allFiles.size();i++){
            lockerFileLocationEditor.putString(allFiles.get(i),allFiles.get(i));
        }
        lockerFileLocationEditor.apply();
    }

}
