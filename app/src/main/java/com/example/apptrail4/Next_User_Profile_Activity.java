package com.example.apptrail4;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Next_User_Profile_Activity extends AppCompatActivity {

    Uri imageUri;
    ImageView personDp;
    TextView usernameD, bioD,nameD,emailD,dobD,instrestD,relationshipD,cityD;
    TextView followerS , posts;
    Button back_btn,acceptFollowReqbtn,declineFollowReqbtn,followbtn,unfollowbtn;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase Fdatabase;
    private DatabaseReference currentUser_database,next_person_datbase;
    String currentUserUid, nextPersonUid,Current_State;
    String Already_Req_Sends,Already_Req_Received;
    private int followerCount = 0, PostCount = 0;




    public Next_User_Profile_Activity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next__user__profile_);


        //Text Views

        usernameD = findViewById(R.id.usernameshow);
        bioD = findViewById(R.id.bioshow);
        nameD = findViewById(R.id.nameshow);
        emailD = findViewById(R.id.emailBody);
        dobD = findViewById(R.id.dobBody);
        instrestD = findViewById(R.id.intrestBody);
        relationshipD = findViewById(R.id.relationstatusBody);
        cityD = findViewById(R.id.cityBody);
        personDp = findViewById(R.id.NextPersonimageId);

        followerS = findViewById(R.id.followerstextView);
        posts = findViewById(R.id.posttextView);

        //Button Views

        acceptFollowReqbtn = findViewById(R.id.AcceptFollowReqbtn);
        declineFollowReqbtn = findViewById(R.id.DeclineFollowReqbtn);
        followbtn = findViewById(R.id.sendFRbtn);
        unfollowbtn = findViewById(R.id.unfollowbtn);


        acceptFollowReqbtn.setVisibility(View.INVISIBLE);
        acceptFollowReqbtn.setEnabled(false);
        declineFollowReqbtn.setVisibility(View.INVISIBLE);
        declineFollowReqbtn.setEnabled(false);
        unfollowbtn.setVisibility(View.INVISIBLE);
        unfollowbtn.setEnabled(false);



        // get Intent
        nextPersonUid = getIntent().getExtras().get("Next_Person_Uid_Var").toString();


        // Firebase

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserUid = mFirebaseAuth.getCurrentUser().getUid();
        currentUser_database = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserUid);
        next_person_datbase = FirebaseDatabase.getInstance().getReference().child("Users").child(nextPersonUid);



        // fetch next person follower count
        next_person_datbase.child("Follower").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    followerCount = (int) snapshot.getChildrenCount();
                    followerS.setText("Followers    "+ " ( "+Integer.toString(followerCount)+ " ) ");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





//          check Following Status

        currentUser_database.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {



                    if (snapshot.child("Follow_Req_Sends").hasChild(nextPersonUid)) {

                            followbtn.setText("Requested");
                    }

                    if (snapshot.child("Follower").hasChild(nextPersonUid)){



                        acceptFollowReqbtn.setVisibility(View.VISIBLE);
                        acceptFollowReqbtn.setEnabled(true);
                        declineFollowReqbtn.setVisibility(View.VISIBLE);
                        declineFollowReqbtn.setEnabled(true);

                        acceptFollowReqbtn.setText("Chat");
                        declineFollowReqbtn.setText("Following");


                        followbtn.setVisibility(View.GONE);
                        followbtn.setEnabled(false);
                        unfollowbtn.setVisibility(View.VISIBLE);
                        unfollowbtn.setEnabled(true);


                        @SuppressLint("UseCompatLoadingForDrawables") Drawable myDrawable = getApplicationContext().getResources().getDrawable(R.drawable.chat_icon);

                        acceptFollowReqbtn.setCompoundDrawablesWithIntrinsicBounds(myDrawable,null,null,null);
                        declineFollowReqbtn.setCompoundDrawables(null,null,null,null);



                        acceptFollowReqbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                               //  accept follow button convert in chat button & on click that go to chat

                                Intent gotochat = new Intent(Next_User_Profile_Activity.this, Personal_Chat_Activity.class);
                                gotochat.putExtra("samnevaleuserkiUid",nextPersonUid);
                                startActivity(gotochat);
                            }
                        });

                        unfollowbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                unFollow();
                            }
                        });


                    }

                    if (snapshot.child("Follow_Req_Pending").hasChild(nextPersonUid)) {


                        followbtn.setVisibility(View.INVISIBLE);
                        followbtn.setEnabled(false);

                        acceptFollowReqbtn.setVisibility(View.VISIBLE);
                        acceptFollowReqbtn.setEnabled(true);
                        declineFollowReqbtn.setVisibility(View.VISIBLE);
                        declineFollowReqbtn.setEnabled(true);


                        acceptFollowReqbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                accept_Follow_Req();
                            }
                        });

                        declineFollowReqbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                decline_Follow_Req();
                            }
                        });


                    }


                    else {


                        followbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SendFollowRequest();
                            }
                        });

                    }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



      // fetch next person data
        next_person_datbase.addValueEventListener(new ValueEventListener() {
             @SuppressLint("SetTextI18n")
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {


                 String disname = "" + snapshot.child("displayname").getValue();
                 nameD.setText(disname);

                 String emailtxt = "" + snapshot.child("email").getValue();
                 emailD.setText(emailtxt);

                 String nextPersonDpStr =  "" + snapshot.child("image").getValue();

                 try {
                     Glide.with(getApplicationContext()).load(nextPersonDpStr).circleCrop().into(personDp);
                 }
                 catch (Exception e){
                     Glide.with(getApplicationContext()).load(R.drawable.user_icon).circleCrop().into(personDp);
                 }


                 if (snapshot.child("About").child("username").child("username").exists()) {
                     String username = "" + snapshot.child("About").child("username").child("username").getValue();
                     if (!username.isEmpty()) {
                         usernameD.setText(username);
                     }
                 } else {

                     String usernamelowercase = disname.toLowerCase();

                     usernameD.setText(usernamelowercase);          
                 }


                        if (snapshot.child("About").child("bio").hasChild("bio")) {
                            String bio = "" + snapshot.child("About").child("bio").child("bio").getValue();
                             if (!bio.isEmpty()) {
                                 bioD.setText(bio);
                             }
                         }
                         else {

                             bioD.setVisibility(View.GONE);

                         }




                 if (snapshot.child("About").child("dob").child("dob").exists()) {
                     String dobtxt = "" + snapshot.child("About").child("dob").child("dob").getValue();
                     if (!dobtxt.isEmpty()) {
                         dobD.setText(dobtxt);
                     }
                 } else {
                     dobD.setText("Details Not Available");
                 }


                 if (snapshot.child("About").child("intrest").child("intrest").exists()) {
                     String intresttxt = "" + snapshot.child("About").child("intrest").child("intrest").getValue();

                     if (!intresttxt.isEmpty()) {
                         instrestD.setText(intresttxt);
                     }
                 } else {
                     instrestD.setText("Details Not Available");
                 }


                 if (snapshot.child("About").child("relationshipstatus").child("relationshipstatus").exists()) {
                     String relationshipstatustxt = "" + snapshot.child("About").child("relationshipstatus").child("relationshipstatus").getValue();

                     if (!relationshipstatustxt.isEmpty()) {
                         relationshipD.setText(relationshipstatustxt);
                     }
                 } else {
                     relationshipD.setText("Details Not Available");
                 }


                 if (snapshot.child("About").child("city").child("city").exists()) {
                     String citytxt = "" + snapshot.child("About").child("city").child("city").getValue();

                     if (!citytxt.isEmpty()) {
                         cityD.setText(citytxt);
                     }
                 } else {
                     cityD.setText("Details Not Available");
                 }





             }


             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });





    }



    @SuppressLint("SetTextI18n")
    private void unFollow() {

        currentUser_database.child("Follower").child(nextPersonUid).removeValue();
        next_person_datbase.child("Follower").child(currentUserUid).removeValue();




        followbtn.setVisibility(View.VISIBLE);
        followbtn.setEnabled(true);

        unfollowbtn.setVisibility(View.GONE);
        unfollowbtn.setEnabled(false);

        acceptFollowReqbtn.setVisibility(View.INVISIBLE);
        declineFollowReqbtn.setVisibility(View.INVISIBLE);

//        followbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SendFollowRequest();
//            }
//        });
    }


    private void decline_Follow_Req() {

        // delete current user node from next user's req send node

        next_person_datbase.child("Follow_Req_Sends").child(currentUserUid).removeValue();

        //delete next user node from current user's req pending node

        currentUser_database.child("Follow_Req_Pending").child(nextPersonUid).removeValue();



        followbtn.setVisibility(View.VISIBLE);
        followbtn.setEnabled(true);
        acceptFollowReqbtn.setVisibility(View.INVISIBLE);
        declineFollowReqbtn.setVisibility(View.INVISIBLE);

        followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendFollowRequest();
            }
        });

    }


    private void accept_Follow_Req() {

        Toast.makeText(this, "Accepted", Toast.LENGTH_SHORT).show();


        Calendar calDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormate = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormate.format(calDate.getTime());

        Calendar calTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormate = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormate.format(calTime.getTime());

        String currentDateTime = currentDate+" "+currentTime;




        next_person_datbase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String disname = "" + snapshot.child("displayname").getValue();
                String emailtxt = "" + snapshot.child("email").getValue();
                String nextPersonDpStr =  "" + snapshot.child("image").getValue();



                currentUser_database.child("Follower").child(nextPersonUid).child("displayname").setValue(disname);
                currentUser_database.child("Follower").child(nextPersonUid).child("email").setValue(emailtxt);
                currentUser_database.child("Follower").child(nextPersonUid).child("uid").setValue(nextPersonUid);
                currentUser_database.child("Follower").child(nextPersonUid).child("image").setValue(nextPersonDpStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        currentUser_database.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String disname = "" + snapshot.child("displayname").getValue();
                                String emailtxt = "" + snapshot.child("email").getValue();
                                String nextPersonDpStr =  "" + snapshot.child("image").getValue();


                                next_person_datbase.child("Follower").child(currentUserUid).child("displayname").setValue(disname);
                                next_person_datbase.child("Follower").child(currentUserUid).child("email").setValue(emailtxt);
                                next_person_datbase.child("Follower").child(currentUserUid).child("uid").setValue(currentUserUid);
                                next_person_datbase.child("Follower").child(currentUserUid).child("image").setValue(nextPersonDpStr);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







        //      Add in Follower list of both user

        currentUser_database.child("Follower").child(nextPersonUid).child("AAState").
                setValue("true");
        currentUser_database.child("Follower").child(nextPersonUid).child("Date & Time").
                setValue(currentDateTime);



        next_person_datbase.child("Follower").child(currentUserUid).child("AAState").
                setValue("true");
        next_person_datbase.child("Follower").child(currentUserUid).child("Date & Time").
                setValue(currentDateTime);


        // delete current user node from next user's req send node

        next_person_datbase.child("Follow_Req_Sends").child(currentUserUid).removeValue();

        //delete next user node from current user's req pending node

        currentUser_database.child("Follow_Req_Pending").child(nextPersonUid).removeValue();







        followbtn.setVisibility(View.GONE);
        followbtn.setEnabled(false);

        unfollowbtn.setVisibility(View.VISIBLE);
        unfollowbtn.setEnabled(true);

        @SuppressLint("UseCompatLoadingForDrawables") Drawable myDrawable = getApplicationContext().getResources().getDrawable(R.drawable.chat_icon);

        acceptFollowReqbtn.setCompoundDrawablesWithIntrinsicBounds(myDrawable,null,null,null);
        declineFollowReqbtn.setCompoundDrawables(null,null,null,null);

        acceptFollowReqbtn.setText("Chat");
        declineFollowReqbtn.setText("Following");
        declineFollowReqbtn.setEnabled(false);

        unfollowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unFollow();
            }
        });

        acceptFollowReqbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotochat = new Intent(Next_User_Profile_Activity.this,Personal_Chat_Activity.class);
                gotochat.putExtra("samnevaleuserkiUid",nextPersonUid);
                startActivity(gotochat);
            }
        });




    }


    private void SendFollowRequest() {

        currentUser_database.child("Follow_Req_Sends").child(nextPersonUid).child("Request_type").
                setValue("Request Send").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    next_person_datbase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String disname = "" + snapshot.child("displayname").getValue();
                            String emailtxt = "" + snapshot.child("email").getValue();
                            String nextPersonDpStr =  "" + snapshot.child("image").getValue();



                            currentUser_database.child("Follow_Req_Sends").child(nextPersonUid).child("displayname").setValue(disname);
                            currentUser_database.child("Follow_Req_Sends").child(nextPersonUid).child("email").setValue(emailtxt);
                            currentUser_database.child("Follow_Req_Sends").child(nextPersonUid).child("uid").setValue(nextPersonUid);
                            currentUser_database.child("Follow_Req_Sends").child(nextPersonUid).child("image").setValue(nextPersonDpStr);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                    next_person_datbase.child("Follow_Req_Pending").child(currentUserUid).child("Request_type").
                            setValue("Request Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                         if (task.isSuccessful()){

                             currentUser_database.addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot snapshot) {

                                     String disname = "" + snapshot.child("displayname").getValue();
                                     String emailtxt = "" + snapshot.child("email").getValue();
                                     String nextPersonDpStr =  "" + snapshot.child("image").getValue();


                                     next_person_datbase.child("Follow_Req_Pending").child(currentUserUid).child("displayname").setValue(disname);
                                     next_person_datbase.child("Follow_Req_Pending").child(currentUserUid).child("email").setValue(emailtxt);
                                     next_person_datbase.child("Follow_Req_Pending").child(currentUserUid).child("uid").setValue(currentUserUid);
                                     next_person_datbase.child("Follow_Req_Pending").child(currentUserUid).child("image").setValue(nextPersonDpStr);

                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError error) {

                                 }
                             });


                             followbtn.setText("Requested");

                         }


                        }
                    });
                }

            }
        });



    }


}
