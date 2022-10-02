package com.example.learning.Adapter;

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
import com.example.learning.hidden;
import com.example.learning.videoDetails;
import com.example.learning.videoRename;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RVAdapterHidden extends RecyclerView.Adapter<RVAdapterHidden.FileLayoutHolder>{

    public static boolean isFromLongPressHidden = false;
    private final Context mContext;
    public static int counter=0;
    public static final ArrayList<Boolean> isSelectedHidden = new ArrayList<>();
    public SharedPreferences lockerFileLocation;
    public SharedPreferences.Editor lockerFileLocationEditor;
    public RVAdapterHidden(Context mContext){
        this.mContext=mContext;
    }

    public static ActionMode actionMode;
    int selectedPosition=-1;
    public static boolean isFromPlayerHidden=false;

    @NonNull
    @Override
    public FileLayoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        lockerFileLocation = mContext.getSharedPreferences("lockerFileLocation",Context.MODE_PRIVATE);
        View view;
        if(MainActivity.layoutValue==1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list, parent, false);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_grid_view, parent, false);
        }
        Collections.sort(Constant.allHiddenMediaList);
        switch (MainActivity.sortByValue){
            case 2:
                Collections.reverse(Constant.allHiddenMediaList);
                break;
            case 3:
                long[] allDates = new long[Constant.allHiddenMediaList.size()];
                File[] allFileList= new File[Constant.allHiddenMediaList.size()];
                for(int i=0;i<Constant.allHiddenMediaList.size();i++){
                    allDates[i]=Constant.allHiddenMediaList.get(i).lastModified();
                    allFileList[i]=Constant.allHiddenMediaList.get(i);
                }
                for(int i=0;i<Constant.allHiddenMediaList.size();i++){
                    for(int j=i+1;j<Constant.allHiddenMediaList.size();j++){
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
                Constant.allHiddenMediaList.clear();
                Constant.allHiddenMediaList.addAll(Arrays.asList(allFileList));
                break;
            default:
                Collections.sort(Constant.allHiddenMediaList);
                break;
        }
        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileLayoutHolder holder, int position) {

        hidden.isFromRefresh=true;
        String fileName = Constant.allHiddenMediaList.get(holder.getBindingAdapterPosition()).getName();
        ((FileLayoutHolder) holder).TextViewTitle.setText(fileName);
        Uri uri = FileProvider.getUriForFile(mContext,
                BuildConfig.APPLICATION_ID +".provider",
                Constant.allHiddenMediaList.get(holder.getBindingAdapterPosition()));
        Glide
                .with(mContext)
                .load(uri)
                .error(R.drawable.ic_video)
                .into(((FileLayoutHolder) holder).ImageViewThumbnail);

        if (isSelectedHidden.get(holder.getBindingAdapterPosition())) {
            holder.itemView.setBackgroundResource(R.drawable.button_effect);
        }
        else {
            holder.itemView.setBackgroundResource(R.drawable.shadow_background_up);
        }

        // single click listener
        holder.itemView.setOnClickListener(v -> {
            if (isFromLongPressHidden) {
                if (isSelectedHidden.get(holder.getBindingAdapterPosition())) {
                    holder.itemView.setBackgroundResource(R.drawable.shadow_background_up);
                    isSelectedHidden.set(holder.getBindingAdapterPosition(), false);
                    counter--;
                    if (counter <= 0) {
                        actionMode.finish();
                        isFromLongPressHidden = false;
                        isSelectedHidden.clear();
                        for (int i = 0; i < Constant.allHiddenMediaList.size(); i++) {
                            isSelectedHidden.add(false);
                        }
                        counter = 0;
                    }
                } else {
                    counter++;
                    if(actionMode==null){
                        actionMode = ((AppCompatActivity)mContext).startActionMode(actionModeCallback);
                    }

                    holder.itemView.setBackgroundResource(R.drawable.button_effect);
                    isSelectedHidden.set(holder.getBindingAdapterPosition(), true);
                }
            }
            else {
                selectedPosition = holder.getBindingAdapterPosition();

                //for opening file from file manager
                exoPlayerTesting.specialBool = true;
                //for opening file from file manager above

                Intent intent = new Intent(mContext, exoPlayerTesting.class);
                intent.putExtra("position", holder.getBindingAdapterPosition());
                intent.putExtra("adapterFinder",2);
                mContext.startActivity(intent);
            }
        });
        //single click listener above

        //long press listener
        holder.itemView.setOnLongClickListener(view -> {
            if(isSelectedHidden.get(holder.getBindingAdapterPosition())){
                isSelectedHidden.set(holder.getBindingAdapterPosition(),false);
                holder.itemView.setBackgroundResource(R.drawable.shadow_background_up);
                counter--;
                if(counter<=0){
                    isFromLongPressHidden=false;
                    try {
                        actionMode.finish();
                    }
                    catch (Exception ignored){ }
                }
            }
            else {
                counter++;
                isFromLongPressHidden = true;
                isSelectedHidden.set(holder.getBindingAdapterPosition(), true);
                if(actionMode==null){
                    actionMode= ((AppCompatActivity)mContext).startActionMode(actionModeCallback);
                }
                holder.itemView.setBackgroundResource(R.drawable.button_effect);
            }
            return true;
        });
        //long press listener above

        //button more listener
        holder.imageViewMore.setOnClickListener(view -> showBottomDialog(holder.getBindingAdapterPosition()));
        //button more listener above
    }

    @Override
    public int getItemCount() {
        return Constant.allHiddenMediaList.size();
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
        BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(R.layout.dialog_options);

        Window window = dialog.getWindow();
        if(window != null) {
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
                    (dialog1, which) -> {
                        if (Constant.allHiddenMediaList.get(position).delete()) {
                            Constant.allHiddenMediaList.remove(position);
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
        buttonRename.setOnClickListener(view12 -> {
            dialog.dismiss();
            Constant.renameFile = Constant.allHiddenMediaList.get(position);
            Constant.renameFilePos = position;
            Constant.renameFileCheck = 3;

            Intent intent = new Intent(mContext, videoRename.class);
            mContext.startActivity(intent);
        });

        if(buttonDetails != null)
        buttonDetails.setOnClickListener(view13 -> {
            dialog.dismiss();
            Method.videoDetails(Constant.allHiddenMediaList.get(position));
            Intent intent = new Intent(mContext, videoDetails.class);
            mContext.startActivity(intent);
        });

        if(buttonShare != null)
        buttonShare.setOnClickListener(view14 -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("video/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM,Constant.allHiddenMediaList.get(position));
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
            if(id == R.id.delete) {
                final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Delete file");
                alertDialog.setMessage("Do you want to delete those file?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        (dialog, which) -> {
                            int countDelete = 0, total = 0;
                            for (int i = 0; i < isSelectedHidden.size(); i++) {
                                if (isSelectedHidden.get(i)) {
                                    total++;
                                    if (Constant.allHiddenMediaList.get(i - countDelete).delete()) {
                                        countDelete++;
                                        Constant.allHiddenMediaList.remove(i - countDelete + 1);
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
            else if(id == R.id.share) {
                ArrayList<Uri> files = new ArrayList<>();
                for (int i = 0; i < isSelectedHidden.size(); i++) {
                    Uri uri = FileProvider.getUriForFile(mContext,
                            BuildConfig.APPLICATION_ID + ".provider",
                            Constant.allHiddenMediaList.get(i));
                    if (isSelectedHidden.get(i)) {
                        files.add(uri);
                    }
                }
                Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                sharingIntent.setType("video/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, files);
                mContext.startActivity(Intent.createChooser(sharingIntent, "Share Video"));
                makeDefault();
                mode.finish();
            }
            else if(id == R.id.lock) {
                int countDelete = 0, failedCount = 0;
                lockerFileLocationEditor = lockerFileLocation.edit();
                for (int i = 0; i < isSelectedHidden.size(); i++) {
                    if (isSelectedHidden.get(i)) {
                        try {
                            String dest = Constant.appLocation + Constant.allHiddenMediaList.get(i - countDelete).getName();
                            org.apache.commons.io.FileUtils.moveFile(Constant.allHiddenMediaList.get(i - countDelete), new File(dest));
                            lockerFileLocationEditor.putString(dest, String.valueOf(Constant.allHiddenMediaList.get(i - countDelete)));
                            Constant.allHiddenMediaList.remove(i - countDelete);
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

    public void makeDefault(){
        isSelectedHidden.clear();
        for (int i = 0; i < Constant.allHiddenMediaList.size(); i++) {
            isSelectedHidden.add(false);
        }
        counter = 0;
        isFromLongPressHidden=false;
        notifyDataSetChanged();
    }

}
