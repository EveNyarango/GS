package com.example.green;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Fragment4 extends Fragment implements View.OnClickListener{
    Button btnCreate;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference, likeref;
    Boolean likeChecker;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment4, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnCreate = getActivity().findViewById(R.id.btnCreatePost_f4);

        reference = database.getReference("All posts");
        likeref = database.getReference("post likes");
        recyclerView = getActivity().findViewById(R.id.rvPost_f4);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnCreate.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreatePost_f4:
                Intent intent = new Intent(getActivity(), PostActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<PostMember> options =
                new FirebaseRecyclerOptions.Builder<PostMember>()
                        .setQuery(reference, PostMember.class)
                        .build();

        FirebaseRecyclerAdapter<PostMember, PostViewholder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PostMember, PostViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(PostViewholder holder, int position, @NonNull PostMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserid = user.getUid();

                        final String postKey = getRef(position).getKey();

                        holder.SetPost(getActivity(), model.getName(), model.getUrl(), model.getPostUri(), model.getTime(),
                                model.getUid(), model.getType(), model.getDesc());



//                        final String que = getItem(position).getQuestion();
//                        final String name = getItem(position).getName();
//                        final String url = getItem(position).getUrl();
//                        final String time = getItem(position).getTime();
//                        final String privacy = getItem(position).getPrivacy();
//                        final String userid = getItem(position).getUserid();



                        holder.likesChecker(postKey);
                        holder.btnLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                likeChecker = true;

                                likeref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (likeChecker.equals(true)){
                                            if (snapshot.child(postKey).hasChild(currentUserid)){
                                                likeref.child(postKey).child(currentUserid).removeValue();
//                                                delete(time);

                                                likeChecker = false;

                                            }else{
                                                likeref.child(postKey).child(currentUserid).setValue(true);


                                                likeref.child(postKey).setValue(true);
                                                likeChecker = false;
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
                    public PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_layout, parent, false);
                        return new PostViewholder(view);
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
}
