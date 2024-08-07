package com.example.apptrail4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Adapter_Pnd_Foll_Req extends RecyclerView.Adapter<Adapter_Pnd_Foll_Req.frpuserViewHolder> {


    Context context;
    List<ModelUser> userList;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserUID = currentUser.getUid();
    private DatabaseReference currentUser_database,next_person_datbase;




    public Adapter_Pnd_Foll_Req(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }



    @NonNull
    @Override
    public frpuserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_req_pending_users, viewGroup,false);
        return new frpuserViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull final frpuserViewHolder holder, int i) {


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




        final String Next_Person_Uid = userUid;
        currentUser_database = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserUID);
        next_person_datbase = FirebaseDatabase.getInstance().getReference().child("Users").child(Next_Person_Uid);



        holder.userdetails_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, ""+userdisplaynameF, Toast.LENGTH_SHORT).show();


                String Next_Person_Uid = userUid;
                Intent gotoNextPersonProfile = new Intent(context, Next_User_Profile_Activity.class);
                gotoNextPersonProfile.putExtra("Next_Person_Uid_Var",Next_Person_Uid);
                gotoNextPersonProfile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(gotoNextPersonProfile);

            }
        });

        holder.acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();

                Calendar calDate = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormate = new SimpleDateFormat("dd-MM-yyyy");
                String currentDate = dateFormate.format(calDate.getTime());

                Calendar calTime = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormate = new SimpleDateFormat("hh:mm a");
                String currentTime = timeFormate.format(calTime.getTime());

                String currentDateTime = currentDate+" "+currentTime;




                next_person_datbase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String disname = "" + snapshot.child("displayname").getValue();
                        String emailtxt = "" + snapshot.child("email").getValue();
                        String nextPersonDpStr =  "" + snapshot.child("image").getValue();



                        currentUser_database.child("Follower").child(Next_Person_Uid).child("displayname").setValue(disname);
                        currentUser_database.child("Follower").child(Next_Person_Uid).child("email").setValue(emailtxt);
                        currentUser_database.child("Follower").child(Next_Person_Uid).child("uid").setValue(Next_Person_Uid);
                        currentUser_database.child("Follower").child(Next_Person_Uid).child("image").setValue(nextPersonDpStr);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                currentUser_database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String disname = "" + snapshot.child("displayname").getValue();
                        String emailtxt = "" + snapshot.child("email").getValue();
                        String nextPersonDpStr =  "" + snapshot.child("image").getValue();


                        next_person_datbase.child("Follower").child(currentUserUID).child("displayname").setValue(disname);
                        next_person_datbase.child("Follower").child(currentUserUID).child("email").setValue(emailtxt);
                        next_person_datbase.child("Follower").child(currentUserUID).child("uid").setValue(currentUserUID);
                        next_person_datbase.child("Follower").child(currentUserUID).child("image").setValue(nextPersonDpStr);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                //      Add in Follower list of both user

                currentUser_database.child("Follower").child(Next_Person_Uid).child("AAState").
                        setValue("true");
                currentUser_database.child("Follower").child(Next_Person_Uid).child("Date & Time").
                        setValue(currentDateTime);



                next_person_datbase.child("Follower").child(currentUserUID).child("AAState").
                        setValue("true");
                next_person_datbase.child("Follower").child(currentUserUID).child("Date & Time").
                        setValue(currentDateTime);

                //delete node to Follow_Req_Pending node in Next person profile

                currentUser_database.child("Follow_Req_Pending").child(Next_Person_Uid).removeValue();

                //delete node to Follow_Req_Send node in Current User profile

                next_person_datbase.child("Follow_Req_Sends").child(currentUserUID).removeValue();



            }
        });

        holder.declinrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Next_Person_Uid = userUid;

                Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();

                //delete node to Follow_Req_Pending node in Next person profile

                currentUser_database.child("Follow_Req_Pending").child(Next_Person_Uid).removeValue();

                //delete node to Follow_Req_Send node in Current User profile

                next_person_datbase.child("Follow_Req_Sends").child(currentUserUID).removeValue();




            }
        });


    }


    @Override
    public int getItemCount() {
        return userList.size();
    }


    class frpuserViewHolder extends RecyclerView.ViewHolder{
        ImageView userImageA;
        TextView usernameA,useremailA;
        Button acceptbtn,declinrbtn;
        LinearLayout userdetails_layout;


        public frpuserViewHolder(@NonNull View itemView) {
            super(itemView);

            userImageA = itemView.findViewById(R.id.useriamgeS);
            usernameA = itemView.findViewById(R.id.usernameS);
            useremailA = itemView.findViewById(R.id.useremailS);
            acceptbtn = itemView.findViewById(R.id.acceptbtnid);
            declinrbtn= itemView.findViewById(R.id.declinebtnid);
            userdetails_layout= itemView.findViewById(R.id.userdetails_layout);

            acceptbtn.setEnabled(true);
            declinrbtn.setEnabled(true);



        }
    }




}
