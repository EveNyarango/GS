package com.example.green;

import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewholder extends RecyclerView.ViewHolder {

    CircleImageView ivProfile;
    ImageView ivPost;
    TextView tvName,tvTime, tvCaption, tvLikes, tvComments;
    ImageButton btnLike, btnComments, btnMore;
    DatabaseReference likesRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    int likescount;

    public PostViewholder(@NonNull View itemView) {
        super(itemView);
    }
public  void SetPost(FragmentActivity activity, String name, String url, String postUri, String time, String uid, String type, String desc){
        ivProfile = itemView.findViewById(R.id.ivProfile_layout);
        ivPost = itemView.findViewById(R.id.ivPost_layout);
        tvName = itemView.findViewById(R.id.tvNamePost_layout);
        tvTime = itemView.findViewById(R.id.tvTime_layout);
        tvCaption = itemView.findViewById(R.id.tvCaption_layout);
        tvLikes = itemView.findViewById(R.id.tvLikes_post_layout);
        btnLike = itemView.findViewById(R.id.btnLike_post_layout);
        btnComments = itemView.findViewById(R.id.btnComment_post_layout);
        btnMore = itemView.findViewById(R.id.btnMore_post_layout);

    SimpleExoPlayer exoplayer;
    PlayerView playerView = itemView.findViewById(R.id.exoplayer_post_layout);

    if(type.equals("iv")) {
        Picasso.get().load(url).into(ivProfile);
        Picasso.get().load(postUri).into(ivPost);
        tvCaption.setText(desc);
        tvTime.setText(time);
        tvName.setText(name);
        playerView.setVisibility(View.INVISIBLE);
    }else if (type.equals("vv")){
        ivPost.setVisibility(View.INVISIBLE);
        tvCaption.setText(desc);
        tvTime.setText(time);
        tvName.setText(name);
        Picasso.get().load(url).into(ivProfile);

        try{

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(activity).build();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    exoplayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(activity);
                    Uri video = Uri.parse(postUri);
            DefaultHttpDataSourceFactory df = new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory ef =new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(video,df, ef,null, null);
            playerView.setPlayer(exoplayer);
            exoplayer.prepare(mediaSource);
            exoplayer.setPlayWhenReady(false);

        }catch (Exception e){
            Toast.makeText(activity,"Error", Toast.LENGTH_SHORT).show();

        }
    }
}
    public void likesChecker(String postKey) {
        btnLike = itemView.findViewById(R.id.btnLike_post_layout);

        likesRef = database.getReference("post likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(uid)){
                    btnLike.setImageResource(R.drawable.ic_baseline_like);
                    likescount = (int)snapshot.child(postKey).getChildrenCount();
                    tvLikes.setText(Integer.toString(likescount) +"likes");
                }else{
                    btnLike.setImageResource(R.drawable.ic_baseline_dislike);
                    likescount = (int)snapshot.child(postKey).getChildrenCount();
                    tvLikes.setText(Integer.toString(likescount) +"likes");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}
