package com.example.green;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText etName,etBio, etProf, etEmail,etWeb;
    Button btn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private String currentuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        documentReference = db.collection("user").document(currentuid);

        etName = findViewById(R.id.etName_up);
        etBio = findViewById(R.id.etBio_up);
        etProf = findViewById(R.id.etProfession_up);
        etEmail = findViewById(R.id.etEmailProf_up);
        etWeb = findViewById(R.id.etWebsite_up);
        btn = findViewById(R.id.btn_Profile_up);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
                
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            String nameResult = task.getResult().getString("name");
                            String bioResult = task.getResult().getString("bio");
                            String emailResult = task.getResult().getString("email");
                            String webResult = task.getResult().getString("web");
                            String profResult = task.getResult().getString("prof");
                            String url = task.getResult().getString("url");

                            etName.setText(nameResult);
                            etBio.setText(bioResult);
                            etEmail.setText(emailResult);
                            etWeb.setText(webResult);
                            etProf.setText(profResult);




                        }else{
                            Toast.makeText(UpdateProfileActivity.this, "No Profile Created", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void updateProfile() {
        String name = etName.getText().toString();
        String bio = etBio.getText().toString();
        String email = etEmail.getText().toString();
        String web = etWeb.getText().toString();
        String prof = etProf.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        documentReference = db.collection("user").document(currentuid);

        final DocumentReference sDoc = db.collection("user").document(currentuid);

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
//                DocumentSnapshot snapshot = transaction.get(sfDocRef);


                transaction.update(sDoc, "name", name);
                transaction.update(sDoc, "bio", bio);
                transaction.update(sDoc, "email", email);
                transaction.update(sDoc, "web", web);
                transaction.update(sDoc, "prof", prof);


                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateProfileActivity.this, "updated", Toast.LENGTH_SHORT).show();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfileActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}