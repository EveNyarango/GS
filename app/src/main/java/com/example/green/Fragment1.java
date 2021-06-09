package com.example.green;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment1 extends Fragment implements View.OnClickListener{
    @Nullable
    CircleImageView IvImage;
    TextView nameTv, profTv, bioTv, emailTv, webTv, tvPost;
    ImageButton IvEdit, btnMenu;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IvImage = getActivity().findViewById(R.id.ivProfile_f1);
        nameTv = getActivity().findViewById(R.id.tvName_f1);
        profTv = getActivity().findViewById(R.id.tvProf_f1);
        bioTv = getActivity().findViewById(R.id.tvBio_f1);
        emailTv = getActivity().findViewById(R.id.tvEmail_f1);
        webTv = getActivity().findViewById(R.id.tvWebsite_f1);
        IvEdit = getActivity().findViewById(R.id.btn_edit);
        btnMenu = getActivity().findViewById(R.id.btn_menu);
        tvPost = getActivity().findViewById(R.id.tvPost_f1);

        IvEdit.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        IvImage.setOnClickListener(this);
        webTv.setOnClickListener(this);
        tvPost.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_edit:
                Intent intent = new Intent (getActivity(), UpdateProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_menu:
                BottomNav bottomNav = new BottomNav();
                bottomNav.show(getFragmentManager(), "bottomnav");

                break;

            case R.id.ivProfile_f1:
                Intent myIntent = new Intent(getActivity(),ImageActivity.class);
                startActivity(myIntent);
                break;

            case R.id.tvPost_f1:
                Intent myIntent3 = new Intent(getActivity(), PersonalPost.class);
                startActivity(myIntent3);
                break;

            case R.id.tvWebsite_f1:
                try {
                    String url = webTv.getText().toString();
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    intent2.setData(Uri.parse(url));
                    startActivity(intent2);

                }catch (Exception e){
                    Toast.makeText(getActivity(),"Please enter a valid url", Toast.LENGTH_SHORT);

                }
                break;


        }

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentid = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("user").document(currentid);

        reference.get()
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

                            Picasso.get().load(url).into(IvImage);
                            nameTv.setText(nameResult);
                            bioTv.setText(bioResult);
                            emailTv.setText(emailResult);
                            webTv.setText(webResult);
                            profTv.setText(profResult);




                        }else{
//                            Intent intent = new Intent(getActivity(),ProfileActivity.class);
//                            startActivity(intent);
                            try
                            {
                                Intent mIntent = new Intent(getActivity(),ProfileActivity.class);
                                startActivity(mIntent);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }
                });

    }
}
