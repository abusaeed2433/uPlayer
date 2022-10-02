package com.example.learning;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.learning.R;

public class eachFile extends AppCompatActivity {

//    private TextView TextViewOtherActivity;
    protected ImageView ImageViewTesting;
    // --Commented out by Inspection (02-Jan-21 8:51 PM):public volatile String[] arr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_file);
//        TextViewOtherActivity=findViewById(R.id.TextViewOtherActivity);
        ImageViewTesting=findViewById(R.id.ImageViewTesting);
        Bitmap bmImg = BitmapFactory.decodeFile("/0/Android/data/com.android.chrome/files/Download/download.jpeg");
        ImageViewTesting.setImageBitmap(bmImg);
    }
//    public static ArrayList<String> GetImagePath(Activity activity) {
//        Uri uri;
//        ArrayList<String> listOfAllImages = new ArrayList<String>();
//        Cursor cursor;
//        int column_index_data, column_index_folder_name;
//        String PathOfImage = null;
//        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        String[] projection = { MediaStore.MediaColumns.DATA,
//                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
//
//        cursor = activity.getContentResolver().query(uri, projection, null,
//                null, null);
//
//        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//        column_index_folder_name = cursor
//                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//        while (cursor.moveToNext()) {
//            PathOfImage = cursor.getString(column_index_data);
//
//            listOfAllImages.add(PathOfImage);
//        }
//        return listOfAllImages;
//    }
}
