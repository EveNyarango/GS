package com.example.green;

import android.app.Application;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView tvSender, tvReceiver;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setMessage(Application application, String message, String time, String date,String type, String senderuid, String receiveruid) {
        tvSender = itemView.findViewById(R.id.tvSender_messageLayout);
        tvReceiver = itemView.findViewById(R.id.tvReceiver_messageLayout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();

        if(currentUid.equals(senderuid)){
            tvReceiver.setVisibility(View.GONE);
            tvSender.setText(message);

        }else if (currentUid.equals(receiveruid)) {
            tvSender.setVisibility(View.GONE);
            tvReceiver.setText(message);

        }

    }
}
