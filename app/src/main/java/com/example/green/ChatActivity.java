package com.example.green;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.jetbrains.annotations.NotNull;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference profileRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView recyclerView;
    EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        etSearch = findViewById(R.id.etSearch_chat);
        recyclerView = findViewById(R.id.rvChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        profileRef = database.getReference("All Users");

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String query = etSearch.getText().toString().toUpperCase();
                Query search = profileRef.orderByChild("name").startAt(query).endAt(query + "\uf0ff");


                FirebaseRecyclerOptions<AllUserMember> options1 =
                        new FirebaseRecyclerOptions.Builder<AllUserMember>()
                                .setQuery(profileRef, AllUserMember.class)
                                .build();
                FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder> firebaseRecyclerAdapter1 =
                        new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                            @Override
                            protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {

                                final String postkey = getRef(position).getKey();

                                holder.setInProfileChat(getApplication(), model.getName(), model.getUid(), model.getProf(), model.getUrl());

                                String name = getItem(position).getName();
                                String url = getItem(position).getUrl();
                                String uid = getItem(position).getUid();

                                holder.tvSendBtn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                                        intent.putExtra("n", name);
                                        intent.putExtra("u", url);
                                        intent.putExtra("uid", uid);
                                        startActivity(intent);


                                    }
                                });

                            }

                            @NonNull
                            @Override
                            public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.chat_item, parent, false);
                                return new ProfileViewholder(view);

                            }
                        };
                firebaseRecyclerAdapter1.startListening();
                recyclerView.setAdapter(firebaseRecyclerAdapter1);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AllUserMember> options1 =
                new FirebaseRecyclerOptions.Builder<AllUserMember>()
                        .setQuery(profileRef,AllUserMember.class)
                        .build();

        FirebaseRecyclerAdapter<AllUserMember,ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<AllUserMember, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull AllUserMember model) {


                        final String postkey = getRef(position).getKey();

                        holder.setInProfileChat(getApplication(),model.getName(),model.getUid(),model.getProf(),model.getUrl());


                        String  name = getItem(position).getName();
                        String  url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();


                        holder.tvSendBtn2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ChatActivity.this,MessageActivity.class);
                                intent.putExtra("n",name);
                                intent.putExtra("u",url);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_item,parent,false);

                        return new ProfileViewholder(view);
                    }
                };

        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);
    }
}