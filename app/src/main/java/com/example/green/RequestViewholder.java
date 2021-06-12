package com.example.green;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestViewholder extends RecyclerView.ViewHolder {

    CircleImageView ivProfile;
    TextView tvName, tvAccept, tvDecline;


    public RequestViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setRequest(FragmentActivity activity, String name, String url, String profession, String bio,
                           String privacy, String email, String followers, String website, String userid) {
        ivProfile = itemView.findViewById(R.id.ivProfile_RequestItem);
        tvName = itemView.findViewById(R.id.tvName_RItem);
        tvAccept = itemView.findViewById(R.id.tvAccept_RequestItem);
        tvDecline = itemView.findViewById(R.id.tvDecline_RequestItem);

        Picasso.get().load(url).into(ivProfile);
        tvName.setText(name);

    }

}
