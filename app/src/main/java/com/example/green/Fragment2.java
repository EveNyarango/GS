package com.example.green;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment2 extends Fragment implements View.OnClickListener {
    FloatingActionButton floating;
    CircleImageView image;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        recyclerView = getActivity().findViewById(R.id.rvFrag2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        databaseReference = database.getReference("All Questions");


        image = getActivity().findViewById(R.id.ivProfile_f2);
        floating = getActivity().findViewById(R.id.floatingActionButton2);
        reference = db.collection("user").document(currentUserid);

        floating.setOnClickListener(this);
        image.setOnClickListener(this);

        FirebaseRecyclerOptions<QuestionMember> options =
                new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(databaseReference, QuestionMember.class)
                .build();

        FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question>(options) {
                    @Override
                    protected void onBindViewHolder(Viewholder_Question holder, int position, @NonNull QuestionMember model) {

                        holder.setitem(getActivity(),model.getName(), model.getUrl(), model.getUserid(), model.getQuestion(), model.getKey(), model.getPrivacy(), model.getTime());

                    }

                    @NonNull

                    @Override
                    public Viewholder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.question_item, parent, false);
                        return new Viewholder_Question(view);
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivProfile_f2:
                break;

            case R.id.floatingActionButton2:
                Intent intent = new Intent(getActivity(), AskActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            String url = task.getResult().getString("url");

                            Picasso.get().load(url).into(image);

                        }else{
                            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
