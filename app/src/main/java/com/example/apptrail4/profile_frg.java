package com.example.apptrail4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;


public class profile_frg extends Fragment  {

     private View F2F_fragment;
     private FirebaseAuth mFirebaseAuth;
     private FirebaseUser currentUser;
    private FirebaseDatabase Fdatabase;
    private DatabaseReference databaseRefrence,followerDatabase,Pnd_Foll_Req_Database;
    private GoogleSignInClient mGoogleSignInClient;


     private Button btnpost , btnfollower, btnfollowreq,btnabout,btnlogout,btnfeedback;
     ViewPager viewpager_ud;
     private ImageView userimage;
     Uri imageUri;
     private TextView usernametxt,nametxt, biotxt;

    private static final String USER = "Users";

    private String emailstr,username,fullname,bio,dob,interest,relationshipstatus,city;
     private ImageButton edit_btn;

     private int followerCount = 0, Pnd_Foll_Req_Count = 0;



    public profile_frg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        F2F_fragment = inflater.inflate(R.layout.fragment_profile_frg, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        Fdatabase = FirebaseDatabase.getInstance();
        databaseRefrence = Fdatabase.getReference("Users");
        final String  currentUseremail = currentUser.getEmail();


         usernametxt = F2F_fragment.findViewById(R.id.usernameshow);
         nametxt = F2F_fragment.findViewById(R.id.nameshow);
        biotxt = F2F_fragment.findViewById(R.id.bioshow);
        userimage = F2F_fragment.findViewById(R.id.userimageId);
        btnlogout = F2F_fragment.findViewById(R.id.logoutbtn);
        edit_btn = F2F_fragment.findViewById(R.id.editdetailId);


        btnabout = F2F_fragment.findViewById(R.id.aboutbtn);
        btnfollower = F2F_fragment.findViewById(R.id.followerbtn);
        btnfollowreq = F2F_fragment.findViewById(R.id.followreqbtn);
        btnpost = F2F_fragment.findViewById(R.id.postbtn);
        btnfeedback = F2F_fragment.findViewById(R.id.feedback_btn_Id);







        String uid = currentUser.getUid();


        followerDatabase = Fdatabase.getReference("Users").child(uid).child("Follower");

        followerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    followerCount = (int) snapshot.getChildrenCount();
                    btnfollower.setText("FOLLOWER   "+ " ( "+Integer.toString(followerCount)+ " ) ");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Pnd_Foll_Req_Database = Fdatabase.getReference("Users").child(uid).child("Follow_Req_Pending");

        Pnd_Foll_Req_Database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    Pnd_Foll_Req_Count = (int) snapshot.getChildrenCount();
                    btnfollowreq.setText("FOLLOW REQUESTS  "+" ( "+Integer.toString(Pnd_Foll_Req_Count)+ " ) ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        Toast.makeText(getActivity(), "hello "+currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();

        // Query for Fetch Current User Data from database

        Query query = databaseRefrence.orderByChild("email").equalTo(currentUser.getEmail());


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {


                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {


                        String disname = "" + dataSnapshot.child("displayname").getValue();
                        nametxt.setText(disname);


                        if (dataSnapshot.child("About").child("username").child("username").exists()) {
                            String username = "" + dataSnapshot.child("About").child("username").child("username").getValue();
                            if (!username.isEmpty()) {
                                usernametxt.setText(username);
                            }
                        } else {

                            String default_Username = disname.toLowerCase();
                            usernametxt.setText(default_Username);
                        }
                        if (dataSnapshot.child("About").child("bio").hasChild("bio")) {
                            String bio = "" + dataSnapshot.child("About").child("bio").child("bio").getValue();

                            if (!bio.isEmpty()) {
                                biotxt.setText(bio);
                            }
                        } else {
                            String default_Bio = "Hey! I am in Yaari";
                            biotxt.setText(default_Bio);
                        }


                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), "Database Error Occurs", Toast.LENGTH_SHORT).show();

            }
        });







        // Butttons for Go in diffeerent Activitiess or Pages

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                signOut();
//                Intent i = new Intent(getActivity(), LoginActivity.class);
//                startActivity(i);

            }

        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
             @Override
            public void onClick(View v) {
                 Intent gotoeditprofile = new Intent(getActivity(),Edit_Profile_Activity.class);
              startActivity(gotoeditprofile);

    }
});

        btnabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  gotoabout = new Intent(getActivity(),AboutActivity.class);
                startActivity(gotoabout);
            }
        });

        btnfollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotofollower = new Intent(getActivity(),Follower_Activity.class);
                startActivity(gotofollower);
            }
        });

        btnfollowreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoPndFollReq = new Intent(getActivity(),FrequestsActivity.class);
                startActivity(gotoPndFollReq);
            }
        });

        btnfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedback = new Intent(getActivity(), MainActivity.class);
                startActivity(feedback);
            }
        });





return F2F_fragment;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {


            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            String personPhotoStr = personPhoto.toString();

//            nametxt.setText(personName);
//            biotxt.setText(personEmail);

            try {
                Glide.with(getActivity()).load(personPhoto).circleCrop().into(userimage);
            }
            catch (Exception e){
                Glide.with(getActivity()).load(R.drawable.user_icon).circleCrop().into(userimage);
            }



        }


    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener((Executor) this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        startActivity(i);
                    }
                });
    }

}
