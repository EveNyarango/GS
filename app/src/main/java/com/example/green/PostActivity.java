package com.example.green;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    ImageView ivPost;
    VideoView vvPost;
    ProgressBar pbPost;
    EditText etDesc;
    Button btnChoose, btnUpload;
    private Uri selectedUri;
    private static final int PICK_FILE = 1;
    UploadTask uploadTask;
    String url, name;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db1, db2, db3;

    MediaController mediaController;
    String type;
    PostMember postMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postMember = new PostMember();

        mediaController = new MediaController(this);

        pbPost = findViewById(R.id.pbPost_post);
        ivPost = findViewById(R.id.ivPost_post);
        vvPost = findViewById(R.id.vvPost_post);
        etDesc = findViewById(R.id.etDesc_post);
        btnChoose = findViewById(R.id.btnChoose_post);
        btnUpload = findViewById(R.id.btnUploadFile_post);

        storageReference = FirebaseStorage.getInstance().getReference("User posts");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All posts");

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Dopost();
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });



    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE || resultCode == RESULT_OK || data !=null ||
                data.getData() !=null){
            selectedUri = data.getData();
            if (selectedUri.toString().contains("images")){
                Picasso.get().load(selectedUri).into(ivPost);
                ivPost.setVisibility(View.VISIBLE);
                vvPost.setVisibility(View.INVISIBLE);
                type = "iv";
            }else if(selectedUri.toString().contains("video")){
                vvPost.setMediaController(mediaController);
                vvPost.setVisibility(View.VISIBLE);
                ivPost.setVisibility(View.INVISIBLE);
                vvPost.setVideoURI(selectedUri);
                vvPost.start();
                type = "vv";
            }else{
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(currentuid);


        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            name = task.getResult().getString("name");
                            url = task.getResult().getString("url");



                        }else{
                            Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    private void Dopost() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        String desc = etDesc.getText().toString();

        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");

        final String savedate = currentdate.format(cdate.getTime());
        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm;ss");
        final String savetime = currenttime.format(ctime.getTime());

        String time = savedate +":"+ savetime;

        if(TextUtils.isEmpty(desc) || selectedUri !=null){

            pbPost.setVisibility(View.VISIBLE);

            final StorageReference reference = storageReference.child(System.currentTimeMillis()+ "."+getFileExt(selectedUri));
            uploadTask = reference.putFile(selectedUri);

            Task<Uri> uriTask =uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                       Uri downloadUri = task.getResult();

                       if(type.equals("iv")){
                           postMember.setDesc(desc);
                           postMember.setName(name);
                           postMember.setUid(currentuid);
                           postMember.setPostUri(downloadUri.toString());
                           postMember.setUrl(url);
                           postMember.setTime(time);
                           postMember.setType("iv");

//                           image
                           String id = db1.push().getKey();
                           db1.child(id).setValue(postMember);
//                           both
                           String id1 = db3.push().getKey();
                           db3.child(id1).setValue(postMember);
                           pbPost.setVisibility(View.INVISIBLE);
                           Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
                       }else if (type.equals("vv")){

                           postMember.setDesc(desc);
                           postMember.setName(name);
                           postMember.setUid(currentuid);
                           postMember.setPostUri(downloadUri.toString());
                           postMember.setUrl(url);
                           postMember.setTime(time);
                           postMember.setType("vv");

//                           video
                           String id3 = db2.push().getKey();
                           db2.child(id3).setValue(postMember);
//                           both
                           String id4 = db3.push().getKey();
                           db3.child(id4).setValue(postMember);

                           pbPost.setVisibility(View.INVISIBLE);

                           Toast.makeText(PostActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();

                       }else{
                           Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
                       }
                    }

                }
            });
        }else{
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }

        }


    }
