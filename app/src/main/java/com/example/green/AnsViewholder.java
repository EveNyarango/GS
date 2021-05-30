package com.example.green;

import android.app.Application;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnsViewholder extends RecyclerView.ViewHolder {

    CircleImageView ivProfileAns;
    TextView tvNameAns, tvTimeAns, tvAnswerAns, tvLikesAns, tvVoteNo;
    int votesCount;
    DatabaseReference reference;
    FirebaseDatabase database;


    public AnsViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setAnswer(Application application, String name, String answer, String uid,String time, String url){
        ivProfileAns = itemView.findViewById(R.id.ivProfile_ans);
        tvNameAns = itemView.findViewById(R.id.tvName_ans);
        tvAnswerAns = itemView.findViewById(R.id.tvAnswer_ans);
        tvTimeAns = itemView.findViewById(R.id.tvTime_ans);


        tvTimeAns.setText(name);
        tvTimeAns.setText(time);
        tvAnswerAns.setText(answer);
        Picasso.get().load(url).into(ivProfileAns);

    }

    public void upvoteChecker (String postkey) {

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("votes");

        tvLikesAns = itemView.findViewById(R.id.tvLike_ans);
        tvVoteNo = itemView.findViewById(R.id.tvVotes);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(postkey).hasChild(currentuid)){
                    tvLikesAns.setText("Liked");
                    votesCount = (int) snapshot.child(postkey).getChildrenCount();
                    tvVoteNo.setText(Integer.toString(votesCount) + "Likes");

                }else{
                    tvLikesAns.setText("Like");
                    votesCount = (int) snapshot.child(postkey).getChildrenCount();
                    tvVoteNo.setText(Integer.toString(votesCount) + "Likes");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
