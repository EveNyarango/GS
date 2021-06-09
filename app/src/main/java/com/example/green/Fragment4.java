package com.example.green;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.security.Permission;
import java.util.List;

public class Fragment4 extends Fragment implements View.OnClickListener{
    Button btnCreate;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference, likeref, db1, db2, db3;
    Boolean likeChecker = false;

//    DatabaseReference db1, db2, db3;


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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All posts");
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



                        final String url = getItem(position).getPostUri();
                        final String name = getItem(position).getName();
//                        final String url = getItem(position).getUrl();
                        final String time = getItem(position).getTime();
                        final String type = getItem(position).getType();
                        final String userid = getItem(position).getUid();



                        holder.likesChecker(postKey);
                        holder.btnMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDialog(name, url, time,userid, type);

                            }
                        });
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

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    void showDialog(String name, String url, String time, String userid, String type){

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.post_options, null);
        TextView download = view.findViewById(R.id.tvDownload_post);
        TextView share = view.findViewById(R.id.tvShare_post);
        TextView delete = view.findViewById(R.id.tvDelete_post);
        TextView copyurl = view.findViewById(R.id.tvCopyUrl_post);


        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        alertDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        if(userid.equals(currentUserid)){
            delete.setVisibility(view.VISIBLE);
        }else{
            delete.setVisibility(View.INVISIBLE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = db1.orderByChild("time").equalTo(time);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                            dataSnapshot1.getRef().removeValue();

                            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Query query2 = db2.orderByChild("time").equalTo(time);
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                            dataSnapshot1.getRef().removeValue();

                            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Query query3 = db3.orderByChild("time").equalTo(time);
                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                            dataSnapshot1.getRef().removeValue();

                            Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                reference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                alertDialog.dismiss();

            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if(type.equals("iv")){

                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                    DownloadManager.Request.NETWORK_MOBILE);
                            request.setTitle("Download");
                            request.setDescription("Downloading image...");
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name+System.currentTimeMillis() + ".jpg");
                            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);

                            Toast.makeText(getActivity(), "Dowloading", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();

                        }else{
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                    DownloadManager.Request.NETWORK_MOBILE);
                            request.setTitle("Download");
                            request.setDescription("Downloading video...");
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name+System.currentTimeMillis() + ".mp4");
                            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);

                            Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();

                        }

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(getActivity(), "No permission", Toast.LENGTH_SHORT).show();

                    }
                };

                TedPermission.with(getActivity())
                        .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();



            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharetext = name + "\n" + "\n" + url;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(Intent.EXTRA_TEXT, sharetext);
                intent.setType("text/plain");
                startActivity(intent.createChooser(intent, "share via"));

                alertDialog.dismiss();
            }
        });
copyurl.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        ClipboardManager cp = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("String", url);
        cp.setPrimaryClip(clip);
        clip.getDescription();
        Toast.makeText(getActivity(), "Copied", Toast.LENGTH_SHORT).show();
    }
});

    }
}
