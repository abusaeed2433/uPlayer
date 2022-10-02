package com.example.learning.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RVAdapterEachFolder extends RecyclerView.Adapter<RVAdapterEachFolder.FileLayoutHolder>{
    private final Context mContext;
    public SharedPreferences lockerFileLocation;
    public SharedPreferences.Editor lockerFileLocationEditor;

    public RVAdapterEachFolder(Context mContext){
        this.mContext=mContext;
    }

    int selectedPosition=-1,counter=0;
    public boolean isFromLongPress=false;
    public static final ArrayList<Boolean> isSelectedFolder = new ArrayList<>();
    public static ActionMode actionMode;

    @NonNull
    @Override
    public FileLayoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        lockerFileLocation = mContext.getSharedPreferences("lockerFileLocation",Context.MODE_PRIVATE);
        View view;
        if(MainActivity.layoutValue==1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_grid_view, parent, false);
        }

        Collections.sort(Constant.allVideoInFolder);
        switch (MainActivity.sortByValue){
            case 2:
                Collections.reverse(Constant.allVideoInFolder);
                break;
            case 3:
                long[] allDates = new long[Constant.allVideoInFolder.size()];
                File[] allFileList= new File[Constant.allVideoInFolder.size()];
                for(int i=0;i<Constant.allVideoInFolder.size();i++){
                    allDates[i]=Constant.allVideoInFolder.get(i).lastModified();
                    allFileList[i]=Constant.allVideoInFolder.get(i);
                }
                for(int i=0;i<Constant.allVideoInFolder.size();i++){
                    for(int j=i+1;j<Constant.allVideoInFolder.size();j++){
                        if(allDates[i]>allDates[j]){
                            long temp = allDates[i];
                            allDates[i]=allDates[j];
                            allDates[j]=temp;

                            File tempFile = allFileList[i];
                            allFileList[i]=allFileList[j];
                            allFileList[j]=tempFile;
                        }
                    }
                }
                Constant.allVideoInFolder.clear();
                Constant.allVideoInFolder.addAll(Arrays.asList(allFileList));
                break;

            default:
                Collections.sort(Constant.allVideoInFolder);
                break;
        }
        return new RVAdapterEachFolder.FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileLayoutHolder holder, int position) {
        String fileName = Constant.allVideoInFolder.get(holder.getBindingAdapterPosition()).getName();

        ((FileLayoutHolder) holder).TextViewTitle.setText(fileName);
        Uri uri = FileProvider.getUriForFile(mContext,
                BuildConfig.APPLICATION_ID + ".provider",
                Constant.allVideoInFolder.get(holder.getBindingAdapterPosition()));

        try {
            Glide
                    .with(mContext)
                    .load(uri)
                    .error(R.drawable.ic_broker_thumbnail)
                    .into(((FileLayoutHolder) holder).ImageViewThumbnail);
        } catch (Exception e) {
            ((RVAdapterEachFolder.FileLayoutHolder) holder).ImageViewThumbnail.setImageResource(R.drawable.ic_video);
        }
        if (isSelectedFolder.get(holder.getBindingAdapterPosition())) {
            holder.itemView.setBackgroundResource(R.drawable.button_effect);
        }
        else {
            holder.itemView.setBackgroundResource(R.drawable.shadow_background_up);
        }

        // single click listener
        holder.itemView.setOnClickListener(v -> {
            if (isFromLongPress) {
                if (isSelectedFolder.get(holder.getBindingAdapterPosition())) {
                    holder.itemView.setBackgroundResource(R.drawable.shadow_background_up);
                    isSelectedFolder.set(holder.getBindingAdapterPosition(), false);
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
                    isSelectedFolder.set(holder.getBindingAdapterPosition(), true);
                }
            }
            else {
                selectedPosition = holder.getBindingAdapterPosition();
                Intent intent = new Intent(mContext,exoPlayerTesting.class);

                exoPlayerTesting.specialBool = true;

                intent.putExtra("position", holder.getBindingAdapterPosition());
                intent.putExtra("adapterFinder", 4);
                mContext.startActivity(intent);
            }
        });
        //single click listener above

        //long press listener
        holder.itemView.setOnLongClickListener(view -> {
            if (isSelectedFolder.get(holder.getBindingAdapterPosition())) {
                isSelectedFolder.set(holder.getBindingAdapterPosition(), false);
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
                isSelectedFolder.set(holder.getBindingAdapterPosition(), true);
                if (actionMode == null) {
                    actionMode = ((AppCompatActivity) mContext).startActionMode(actionModeCallback);
                }
                holder.itemView.setBackgroundResource(R.drawable.button_effect);
            }
            return true;
        });
        //long press listener above
        //extra above

        holder.imageViewMore.setOnClickListener(view -> showBottomDialog(holder.getBindingAdapterPosition()));
        //button more listener above
    }


    @Override
    public int getItemCount() {
        return Constant.allVideoInFolder.size();
    }

    public static class FileLayoutHolder extends RecyclerView.ViewHolder{

        final ImageView ImageViewThumbnail,imageViewMore;
        final TextView TextViewTitle;
        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);
            ImageViewThumbnail=itemView.findViewById(R.id.ImageViewThumbnail);
            TextViewTitle=itemView.findViewById(R.id.TextViewTitle);
            imageViewMore =itemView.findViewById(R.id.ImageButtonMore);
        }
    }

    private void showBottomDialog(int position){
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
        buttonDelete.setOnClickListener(view1 -> {
            final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setTitle("Delete file");
            alertDialog.setMessage("Do you want to delete those file?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog1, int which) {
                            if (Constant.allVideoInFolder.get(position).delete()) {
                                try {
                                    Constant.allMediaList.remove(Constant.allVideoInFolder.get(position));
                                    Constant.allMediaList2.remove(Constant.allVideoInFolder.get(position));
                                }
                                catch (Exception ignored){

                                }
                                Constant.allVideoInFolder.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, "deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "failed to delete", Toast.LENGTH_SHORT).show();
                            }
                            alertDialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", (dialogInterface, i) -> alertDialog.dismiss());
            alertDialog.show();
            dialog.dismiss();
        });

        if(buttonRename != null)
        buttonRename.setOnClickListener(view12 -> {
            dialog.dismiss();
            Constant.renameFile = Constant.allVideoInFolder.get(position);
            Constant.renameFilePos = position;
            Constant.renameFileCheck=2;

            Intent intent = new Intent(mContext, videoRename.class);
            mContext.startActivity(intent);
        });

        if(buttonDetails != null)
        buttonDetails.setOnClickListener(view13 -> {
            dialog.dismiss();
            Method.videoDetails(Constant.allVideoInFolder.get(position));
            Intent intent = new Intent(mContext, videoDetails.class);
            mContext.startActivity(intent);
        });

        if(buttonShare != null)
        buttonShare.setOnClickListener(view14 -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("video/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Constant.allVideoInFolder.get(position));
            mContext.startActivity(Intent.createChooser(sharingIntent, "Share Video"));
            dialog.dismiss();
        });
        dialog.show();
    }

    public void makeDefault(){
        isSelectedFolder.clear();
        for (int i = 0; i < Constant.allVideoInFolder.size(); i++) {
            isSelectedFolder.add(false);
        }
        counter = 0;
        isFromLongPress=false;
        notifyDataSetChanged();
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
            if(id == R.id.delete) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Delete file");
                alertDialog.setMessage("Do you want to delete those file?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        (dialog, which) -> {
                            int countDelete = 0, total = 0;
                            for (int i = 0; i < isSelectedFolder.size(); i++) {
                                if (isSelectedFolder.get(i)) {
                                    total++;
                                    if (Constant.allVideoInFolder.get(i - countDelete).delete()) {
                                        Constant.allMediaList.remove(Constant.allVideoInFolder.get(i - countDelete));
                                        Constant.allMediaList2.remove(Constant.allVideoInFolder.get(i - countDelete));
                                        countDelete++;
                                        Constant.allVideoInFolder.remove(i - countDelete + 1);
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
                            Log.d("testinggg", String.valueOf(Constant.allVideoInFolder.size()));
                            if (Constant.allVideoInFolder.size() == 0) {
                                Constant.allFolderList.remove(RVAdapterFolder.selectedFolder);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", (dialogInterface, i) -> {
                    makeDefault();
                    alertDialog.dismiss();
                    mode.finish();
                });
                alertDialog.show();

            }
            else if(id == R.id.share) {
                ArrayList<Uri> files = new ArrayList<>();
                for (int i = 0; i < isSelectedFolder.size(); i++) {
                    if (isSelectedFolder.get(i)) {
                        Uri uri = FileProvider.getUriForFile(mContext,
                                BuildConfig.APPLICATION_ID + ".provider",
                                Constant.allVideoInFolder.get(i));
                        files.add(uri);
                    }
                }
                Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, files);
                mContext.startActivity(Intent.createChooser(sharingIntent, "Share Video"));
                //another again again
                makeDefault();
                mode.finish();
            }
            else if(id == R.id.lock) {
                int countDelete = 0, failedCount = 0;
                lockerFileLocationEditor = lockerFileLocation.edit();
                for (int i = 0; i < isSelectedFolder.size(); i++) {
                    if (isSelectedFolder.get(i)) {
                        try {
                            String dest = Constant.appLocation + Constant.allVideoInFolder.get(i - countDelete).getName();
                            org.apache.commons.io.FileUtils.moveFile(Constant.allVideoInFolder.get(i - countDelete), new File(dest));
                            lockerFileLocationEditor.putString(dest, String.valueOf(Constant.allVideoInFolder.get(i - countDelete)));
                            Constant.allVideoInFolder.remove(i - countDelete);
                            countDelete++;
                        } catch (Exception e) {
                            failedCount++;
                        }
                    }
                }
                lockerFileLocationEditor.apply();
                if (failedCount != 0) {
                    Toast.makeText(mContext, "something went wrong\nfailed to lock " + failedCount + " files", Toast.LENGTH_SHORT).show();
                }
                if (Constant.allVideoInFolder.size() == 0) {
                    Constant.allFolderList.remove(RVAdapterFolder.selectedFolder);
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

}
