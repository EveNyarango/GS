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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CircleImageView ivProfile;
    TextView tvName;
    EditText etMessage;
    ImageButton btnSend, btnCamera, btnMic;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootref1, rootref2;
    MessageMember messageMember;
    String receiver_name, receiver_uid, sender_uid, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            url = bundle.getString("u");
            receiver_name = bundle.getString("n");
            receiver_uid = bundle.getString("uid");
        }else {
            Toast.makeText(this, "user missing", Toast.LENGTH_SHORT).show();
        }

        messageMember = new MessageMember();
        recyclerView = findViewById(R.id.rvMessage);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        ivProfile = findViewById(R.id.ivProfile_message);
        tvName = findViewById(R.id.tvName_Message);
        etMessage = findViewById(R.id.etEnterMessage);
        btnSend = findViewById(R.id.btnSend_message);
        btnCamera = findViewById(R.id.btnCamera_message);
        btnMic = findViewById(R.id.btnMic_message);

        Picasso.get().load(url).into(ivProfile);
        tvName.setText(receiver_name);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        sender_uid= user.getUid();

        rootref1 = database.getReference("message").child(sender_uid).child(receiver_uid);
        rootref2 = database.getReference("message").child(receiver_uid).child(sender_uid);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<MessageMember> options1 =
                new FirebaseRecyclerOptions.Builder<MessageMember>()
                        .setQuery(rootref1,     MessageMember.class)
                        .build();
        FirebaseRecyclerAdapter<MessageMember, MessageViewHolder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<MessageMember, MessageViewHolder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull MessageMember model) {
                        holder.setMessage(getApplication(), model.getMessage(), model.getTime(), model.getDate(),
                                model.getType(), model.getSenderuid(), model.getReceiveruid());

                    }

                    @NonNull
                    @Override
                    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.message_layout, parent, false);
                        return new MessageViewHolder(view);

                    }
                };
        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);

    }

    private void sendMessage() {

        String message = etMessage.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-mmmm-yyyy");

        final String savedate = currentdate.format(cdate.getTime());
        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm;ss");
        final String savetime = currenttime.format(ctime.getTime());

        String time = savedate +":"+ savetime;

        if (message.isEmpty()){
            Toast.makeText(this, "Cannot send empty message", Toast.LENGTH_SHORT).show();

        }else {
            messageMember.setDate(savedate);
            messageMember.setTime(savetime);
            messageMember.setMessage(message);
            messageMember.setReceiveruid(receiver_uid);
            messageMember.setSenderuid(sender_uid);
            messageMember.setType("text");

            String id = rootref1.push().getKey();
            rootref1.child(id).setValue(messageMember);

            String id1 = rootref2.push().getKey();
            rootref2.child(id).setValue(messageMember);

            etMessage.setText("");


        }
    }
}