package com.example.green;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
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

public class Viewholder_Question extends RecyclerView.ViewHolder {

    CircleImageView imageQ;
    TextView time_result, name_result, question_result;
    ImageButton favourite;
    DatabaseReference favouriteref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    public Viewholder_Question(View itemView) {
        super(itemView);
    }

    public void setitem(FragmentActivity activity, String name, String url, String userid, String question, String key, String privacy, String time){
        imageQ = itemView.findViewById(R.id.ivProfile_item);
        time_result = itemView.findViewById(R.id.tvTime_item);
        name_result = itemView.findViewById(R.id.tvName_item);
        question_result = itemView.findViewById(R.id.tvQuestion_item);

        Picasso.get().load(url).into(imageQ);
        time_result.setText(time);
        name_result.setText(name);
        question_result.setText(question);

    }


    public void favouriteChecker(String postKey) {
        favourite = itemView.findViewById(R.id.favourite_item);

        favouriteref = database.getReference("favourites");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        favouriteref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(uid)){
                    favourite.setImageResource(R.drawable.ic_baseline_turned_in);
                }else{
                    favourite.setImageResource(R.drawable.ic_baseline_turned_in_not);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}
