package com.example.green;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Related extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        RecyclerView recyclerViewR;
//        FirebaseDatabase database;
        DatabaseReference reference;
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related);

        recyclerViewR = findViewById(R.id.rvRelated);
        recyclerViewR.setHasFixedSize(true);
        recyclerViewR.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        reference = database.getReference("favouriteList").child(currentUserid);

        FirebaseRecyclerOptions<QuestionMember> options =
                new FirebaseRecyclerOptions.Builder<QuestionMember>()
                        .setQuery(reference, QuestionMember.class)
                        .build();

        FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question>(options) {
                    @Override
                    protected void onBindViewHolder(Viewholder_Question holder, int position, @NonNull QuestionMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserid = user.getUid();

                        final String postKey = getRef(position).getKey();

                        holder.setitemRelated(getApplication(),model.getName(), model.getUrl(), model.getUserid(), model.getQuestion(), model.getKey(), model.getPrivacy(), model.getTime());

                        String que = getItem(position).getQuestion();
//                        String name = getItem(position).getName();
//                        String url = getItem(position).getUrl();
//                        String time = getItem(position).getTime();
//                        String privacy = getItem(position).getPrivacy();
                        String userid = getItem(position).getUserid();
//                        holder.favouriteChecker(postKey);

                        holder.tvReplyR.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Related.this,ReplyActivity.class);
                                intent.putExtra("uid", userid);
                                intent.putExtra("q",que);
                                intent.putExtra("postkey", postKey);
                                startActivity(intent);

                            }
                        });


                    }

                    @NonNull

                    @Override
                    public Viewholder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.related_item, parent, false);
                        return new Viewholder_Question(view);
                    }
                };
        recyclerViewR.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }
}