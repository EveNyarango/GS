package com.example.green;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileViewholder extends RecyclerView.ViewHolder {

    CircleImageView ivProfile;
    TextView tvName, tvProf, tvViewProf, tvSendBtn2;
    CardView cvProfile;

    public ProfileViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setProfile(FragmentActivity fragmentActivity, String name, String uid, String prof, String url){

        cvProfile = itemView.findViewById(R.id.cvProfile_profile2);
        ivProfile = itemView.findViewById(R.id.ivProfile_profile2);
        tvName = itemView.findViewById(R.id.tvName_profile2);
        tvProf = itemView.findViewById(R.id.tvProf_profile2);
        tvViewProf = itemView.findViewById(R.id.tvViewProf_profile2);


        Picasso.get().load(url).into(ivProfile);
        tvProf.setText(prof);
        tvName.setText(name);

    }
    public  void setInProfileChat(Application fragmentActivity, String name, String uid,
                                  String prof, String url){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();

        CircleImageView ivProfile2 = itemView.findViewById(R.id.ivProfile_chatItem);
        TextView tvName2 = itemView.findViewById(R.id.tvName_chatItem);
        TextView tvProf2 = itemView.findViewById(R.id.tvProf_chatItem);
        TextView tvSendBtn2 = itemView.findViewById(R.id.tvSendMessage_chatItem);

        if (userid.equals(uid)){
            Picasso.get().load(url).into(ivProfile2);
            tvName2.setText(name);
            tvProf2.setText(prof);
            tvSendBtn2.setVisibility(View.INVISIBLE);
        }else{
            Picasso.get().load(url).into(ivProfile2);
            tvName2.setText(name);
            tvProf2.setText(prof);
        }

    }
}
