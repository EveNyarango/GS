package com.example.green;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class PhotosTab extends Fragment {

    FirebaseDatabase database;
    DatabaseReference reference;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photos_tabs, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        database = FirebaseDatabase.getInstance();

        recyclerView = getActivity().findViewById(R.id.rvImage_tabs);
        reference = database.getReference("All images").child(uid);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<PostMember> options =
                new FirebaseRecyclerOptions.Builder<PostMember>()
                        .setQuery(reference, PostMember.class)
                        .build();

        FirebaseRecyclerAdapter<PostMember, ImageFragment> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PostMember, ImageFragment>(options) {
                    @Override
                    protected void onBindViewHolder(ImageFragment holder, int position, @NonNull PostMember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserid = user.getUid();

                        final String postKey = getRef(position).getKey();

                        holder.SetImage(getActivity(), model.getName(), model.getUrl(), model.getPostUri(), model.getTime(),
                                model.getUid(), model.getType(), model.getDesc());

                    }

                    @NonNull

                    @Override
                    public ImageFragment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_images, parent, false);
                        return new ImageFragment(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        GridLayoutManager gln = new GridLayoutManager(getActivity(), 3,GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gln);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
