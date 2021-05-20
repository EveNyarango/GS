package com.example.green;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Viewholder_Question extends RecyclerView.ViewHolder {

    CircleImageView imageQ;
    TextView time_result, name_result, question_result;


    public Viewholder_Question(View itemView) {
        super(itemView);
    }

    public void setitem(FragmentActivity activity, String name, String url, String userid, String question, String key, String privacy, String time){
        imageQ = itemView.findViewById(R.id.ivProfile_item);
        time_result = itemView.findViewById(R.id.tvTime_item);
        name_result = itemView.findViewById(R.id.tvName_item);
        question_result = itemView.findViewById(R.id.tvQuestion_item);

        Picasso.get().load(url).into(imageQ);
        time_result.setText(time);
        name_result.setText(name);
        question_result.setText(question);

    }
}
