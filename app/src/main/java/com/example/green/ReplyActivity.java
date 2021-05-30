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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyActivity extends AppCompatActivity {

    String uid,question,post_key,key;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference, reference2;

    TextView tvNameReply, tvQuestionReply,tvReplyReply,tvReplyUser;
    RecyclerView recyclerViewR;
    CircleImageView ivProfile1, ivProfile2;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference votesref, Allquestions;
    Boolean voteChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        tvNameReply = findViewById(R.id.tvName_Reply);
        tvQuestionReply = findViewById(R.id.tvQuestion_Reply);
        tvReplyUser = findViewById(R.id.tvReplyUser);
        ivProfile1 = findViewById(R.id.ivProfile_Reply);
        ivProfile2 = findViewById(R.id.ivProfile_ReplyUser);
        tvReplyReply = findViewById(R.id.tvReplyUser);

        recyclerViewR = findViewById(R.id.rvReply);
        recyclerViewR.setLayoutManager(new LinearLayoutManager(ReplyActivity.this));


        Bundle extra = getIntent().getExtras();
        if(extra != null){

            uid = extra.getString("uid");
            post_key = extra.getString("postkey");
            question = extra.getString("q");
//            key = extra.getString("key");

        }else{
            Toast.makeText(this, "Opps", Toast.LENGTH_SHORT).show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        Allquestions = database.getReference("All Questions").child(post_key).child("Answer");
        votesref = database.getReference("votes");

        reference = db.collection("user").document(uid);
        reference2 = db.collection("user").document(currentuid);


        tvReplyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReplyActivity.this,AnswerActivity.class);
                intent.putExtra("u", uid);
//                intent.putExtra("q",question);
                intent.putExtra("p", post_key);
//                                intent.putExtra("key", privacy);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            String url = task.getResult().getString("url");
                            String name = task.getResult().getString("name");
                            Picasso.get().load(url).into(ivProfile2);
                            Picasso.get().load(url).into(ivProfile1);
                            tvQuestionReply.setText(question);
                            tvNameReply.setText(name);


                        }else{
                            Toast.makeText(ReplyActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        reference2.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            String url = task.getResult().getString("url");

                            Picasso.get().load(url).into(ivProfile2);


                        }else{
                            Toast.makeText(ReplyActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        FirebaseRecyclerOptions<AnswerMember> options =
                new FirebaseRecyclerOptions.Builder<AnswerMember>()
                        .setQuery(Allquestions, AnswerMember.class)
                        .build();

        FirebaseRecyclerAdapter<AnswerMember, AnsViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AnswerMember, AnsViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(AnsViewholder holder, int position, @NonNull AnswerMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();

                        holder.setAnswer(getApplication(), model.getName(), model.getAnswer(), model.getUid(),
                                model.getTime(), model.getUrl());
                        holder.upvoteChecker(postkey);
                        holder.tvLikesAns.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                voteChecker = true;
                                votesref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (voteChecker.equals(true)){
                                            if(snapshot.child(postkey).hasChild(currentUserid)){
                                                votesref.child(postkey).child(currentUserid).removeValue();

                                                voteChecker = false;
                                            }else {
                                                votesref.child(postkey).child(currentUserid).setValue(true);

                                                voteChecker = false;
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        });

                    }

                    @NonNull

                    @Override
                    public  AnsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.activity_ans, parent, false);
                        return new AnsViewholder(view);
                    }
                };


        recyclerViewR.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
}