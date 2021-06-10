package com.example.green;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


public class ImageFragment extends RecyclerView.ViewHolder {
    ImageView ivPostImage;
    public ImageFragment(@NonNull View itemView) {
        super(itemView);
    }
    public  void SetImage(FragmentActivity activity, String name, String url, String postUri, String time,
                          String uid, String type, String desc){

        ivPostImage = itemView.findViewById(R.id.ivPost_image);

            Picasso.get().load(postUri).into(ivPostImage);
    }
}
