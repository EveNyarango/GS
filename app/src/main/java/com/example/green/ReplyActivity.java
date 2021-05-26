package com.example.green;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    }
}