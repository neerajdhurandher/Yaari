package com.example.apptrail4;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdapterUserRV2 extends  RecyclerView.Adapter<AdapterUserRV2.userViewHolder> {

    Context context;
    List<ModelUser2> userList;

    public AdapterUserRV2(Context context, List<ModelUser2> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup, false);
        return new userViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userViewHolder holder, int i) {


        //fetch data
        final String userUid = userList.get(i).getUid();
        String userImageF = userList.get(i).getImage();
        final String userdisplaynameF = userList.get(i).getDisplayname();
        String useremailF = userList.get(i).getEmail();

        // assign data
        holder.usernameA.setText(userdisplaynameF);
        holder.useremailA.setText(useremailF);

        try {
            Glide.with(context).load(userImageF).circleCrop().into(holder.userImageA);
        }
        catch (Exception e) {
            Glide.with(context).load(R.drawable.user_icon).circleCrop().into(holder.userImageA);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(context, "" + userdisplaynameF, Toast.LENGTH_SHORT).show();


                Intent gotoNextPersonProfile = new Intent(context, Next_User_Profile_Activity.class);
                gotoNextPersonProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                gotoNextPersonProfile.putExtra("Next_Person_Uid_Var", userUid);
                context.startActivity(gotoNextPersonProfile);

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class userViewHolder extends RecyclerView.ViewHolder {
        ImageView userImageA;
        TextView usernameA, useremailA;

        public userViewHolder(@NonNull View itemView) {
            super(itemView);

            userImageA = itemView.findViewById(R.id.useriamgeS);
            usernameA = itemView.findViewById(R.id.usernameS);
            useremailA = itemView.findViewById(R.id.useremailS);


        }
    }
}

