package com.example.green;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowUser extends AppCompatActivity {

    TextView tvName, tvProf, tvBio, tvEmail, tvWebsite, tvRequest;
    CircleImageView ivProfile;
    TextView btnFollow, tvFollowers, tvPost;
    LinearLayout llFollowers, llPost;
    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference1, databaseReference2, postnoref, requestref;
    String url, name,age, email,privacy,p, website, bio, userid;
    RequestMember requestMember;
    String name_result;
    String uidreq, urlreq, professionreq;

    int postNo;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference, documentReference1;

    int followercount;



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
        tvRequest = findViewById(R.id.tvRequest_showUser);


        llFollowers = findViewById(R.id.llFollowers);
        llPost = findViewById(R.id.llPost);
    }
}