package com.example.green;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowUser extends AppCompatActivity {

    TextView tvName, tvProf, tvBio, tvEmail, tvWebsite, tvRequest, tvNoFollowers, tvNoPost;
    CircleImageView ivProfile;
    TextView btnFollow, tvFollowers, tvPost;
    LinearLayout llFollowers, llPost;
    Button btnSendMessage;
    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference1, databaseReference2, postnoref, requestref, db1, db2;
    String url, name,age, email,privacy,p, website, bio, userid;
    RequestMember requestMember;
    String name_result;
    String uidreq, urlreq, professionreq, namereq;

    int postNo;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference, documentReference1;

    int followercount, postiv,postvv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        tvName = findViewById(R.id.tvName_showUser);
        tvProf = findViewById(R.id.tvProf_showUser);
        tvBio = findViewById(R.id.tvBio_showUser);
        tvEmail = findViewById(R.id.tvEmail_showUser);
        tvWebsite = findViewById(R.id.tvWebsite_showUser);
        ivProfile = findViewById(R.id.ivProfile_showUser);
        btnFollow = findViewById(R.id.tvFollow_showUser);
        tvFollowers = findViewById(R.id.tvFollowers_showUser);
        tvPost = findViewById(R.id.tvPost_showUser);
        tvNoFollowers = findViewById(R.id.tvNoFollowers_showUser);
        tvNoPost = findViewById(R.id.tvNoPost_showUser);
        tvRequest = findViewById(R.id.tvRequest_showUser);
        btnSendMessage = findViewById(R.id.btnSendmessage_showuser);


        llFollowers = findViewById(R.id.llFollowers);
        llPost = findViewById(R.id.llPost);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            url = extras.getString("u");
            name = extras.getString("n");
            userid = extras.getString("uid");
        }else{

        }
        databaseReference = database.getReference("Requests").child(userid);
        databaseReference1 = database.getReference("followers").child(userid);
        documentReference = db.collection("user").document(userid);
        postnoref = database.getReference("User Posts").child(userid);
        databaseReference2 = database.getReference("followers");
        documentReference1 = db.collection("user").document(currentUserId);
        db1 = database.getReference("All images").child(userid);
        db2 = database.getReference("All videos").child(userid);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowUser.this,MessageActivity.class);
                intent.putExtra("n",name);
                intent.putExtra("u",url);
                intent.putExtra("uid",userid);
                startActivity(intent);
            }
        });

        postnoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postNo = (int) snapshot.getChildrenCount();
                tvPost.setText(Integer.toString(postNo));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = btnFollow.getText().toString();
                if(status.equals("Follow")){
                    follow();
                }else if (status.equals("Requested")){
                    delRequest();
                }else if(status.equals("Following")){
                    unfollow();

                }
            }
        });
        tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowUser.this,FollowersActivity.class);
                intent.putExtra("u", userid);
                startActivity(intent);
            }
        });
    }


    private void delRequest() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        databaseReference.child(currentUserId).removeValue();
        btnFollow.setText("Follow");
    }

    @Override
    protected void onStart() {
        super.onStart();

        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postiv = (int)snapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postvv = (int)snapshot.getChildrenCount();
                String total = Integer.toString(postiv+postvv);
                tvNoPost.setText(total);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String name_result = task.getResult().getString("name");
                            String age_result = task.getResult().getString("prof");
                            String bio_result = task.getResult().getString("bio");
                            String email_result = task.getResult().getString("email");
                            String web_result = task.getResult().getString("web");
                            String Url = task.getResult().getString("url");
                            p = task.getResult().getString("privacy");

                            if(p.equals("Public")){
                                tvProf.setText(age_result);
                                tvName.setText(name_result);
                                tvBio.setText(bio_result);
                                tvEmail.setText(email_result);
                                tvWebsite.setText(web_result);
                                Picasso.get().load(Url).into(ivProfile);
                                tvRequest.setVisibility(View.GONE);
                            }else{
                                String u = btnFollow.getText().toString();
                                if(u.equals("Following")){
                                    tvProf.setText(age_result);
                                    tvName.setText(name_result);
                                    tvBio.setText(bio_result);
                                    tvEmail.setText(email_result);
                                    tvWebsite.setText(web_result);
                                    Picasso.get().load(Url).into(ivProfile);
                                    tvRequest.setVisibility(View.GONE);
                                }else{
                                    tvProf.setText("********");
                                    tvName.setText(name_result);
                                    tvBio.setText("*******");
                                    tvEmail.setText("*******");
                                    tvWebsite.setText("*******");
                                    Picasso.get().load(Url).into(ivProfile);
                                    tvRequest.setVisibility(View.GONE);
                                }
                            }

                        }else{
                            Toast.makeText(ShowUser.this, "No profile exist", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                    }
                });



//        following

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    followercount = (int) snapshot.getChildrenCount();
                    tvFollowers.setText(Integer.toString(followercount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(currentUserId)){
                    btnSendMessage.setText("Requested");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userid).hasChild(currentUserId)){
                    btnSendMessage.setText("Following");
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

     void follow() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        if (p.equals("Public")) {
            btnSendMessage.setText("Following");
            requestMember.setUserid(currentUserId);
            requestMember.setProfession(professionreq);
            requestMember.setUrl(urlreq);
            requestMember.setName(namereq);
            databaseReference1.child(currentUserId).setValue(requestMember);
        }else{
            btnSendMessage.setText("Requested");
            requestMember.setUserid(currentUserId);
            requestMember.setProfession(professionreq);
            requestMember.setUrl(urlreq);
            requestMember.setName(namereq);
            databaseReference.child(currentUserId).setValue(requestMember);
            tvRequest.setText("Wait Until your request is accepted");
        }

    }

    private void unfollow() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(ShowUser.this);
        builder.setTitle("Unfollow")
                .setMessage("Are you sure to Unfollow?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference1.child(currentUserId).removeValue();
                        btnSendMessage.setText("Follow");
                        tvFollowers.setText("0");
                        Toast.makeText(ShowUser.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create();
        builder.show();
    }
}