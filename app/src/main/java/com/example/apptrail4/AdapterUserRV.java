package com.example.apptrail4;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class AdapterUserRV extends  RecyclerView.Adapter<AdapterUserRV.userViewHolder>{

    Context context;
    List<ModelUser> userList;
    FirebaseUser currentUser;
    String currentUser_UID;

    public AdapterUserRV(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup,false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser_UID = currentUser.getUid();

        return new userViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final userViewHolder holder, int i) {


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
        catch (Exception e){
            Glide.with(context).load(R.drawable.user_icon).circleCrop().into(holder.userImageA);
        }


      // set following button state

       DatabaseReference currentUser_database = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser_UID);

        currentUser_database.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              if (snapshot.exists()) {

                  if (snapshot.child("Follower").hasChild(userUid)){

                      holder.following.setVisibility(View.VISIBLE);

                  }

                  }

              }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userUid.equals(currentUser_UID)){

                    Toast.makeText(context, "" + userdisplaynameF, Toast.LENGTH_SHORT).show();


                String Next_Person_Uid = userUid;
                Intent gotoNextPersonProfile = new Intent(context, Next_User_Profile_Activity.class);
                gotoNextPersonProfile.putExtra("Next_Person_Uid_Var", Next_Person_Uid);
                gotoNextPersonProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(gotoNextPersonProfile);

            }
                else {
                    Toast.makeText(context, "That's You !" , Toast.LENGTH_SHORT).show();
                }
        }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class userViewHolder extends RecyclerView.ViewHolder{
      ImageView userImageA;
      TextView usernameA,useremailA,following;

        public userViewHolder(@NonNull View itemView) {
            super(itemView);

          userImageA = itemView.findViewById(R.id.useriamgeS);
          usernameA = itemView.findViewById(R.id.usernameS);
          useremailA = itemView.findViewById(R.id.useremailS);
          following = itemView.findViewById(R.id.following_state);

          following.setVisibility(View.GONE);



        }
    }

}
