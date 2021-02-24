package com.example.apptrail4;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Show_User_Posts_Activity extends AppCompatActivity {


    String nextPersonUid;
    ImageView next_user_image;
    TextView next_user_name,next_user_bio,following_state;
    FirebaseUser currenyUser;
    String currentUser_UID;

    RecyclerView recyclerView;
    List<ModelShowPost> postList;
    Adapter_Show_Post adapter_show_post;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__user__posts_);


        next_user_name = findViewById(R.id.usernameS);
        next_user_bio = findViewById(R.id.userbioS);
        next_user_image = findViewById(R.id.useriamgeS);
        following_state = findViewById(R.id.following_state);

        following_state.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.post_recyclerView);
        recyclerView.setHasFixedSize(true);

        postList = new ArrayList<>();

        // leaner layout adpater manager for post

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,true);

        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter_show_post = new Adapter_Show_Post(getApplicationContext(), postList);
        recyclerView.setAdapter(adapter_show_post);

        currenyUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser_UID = currenyUser.getUid();




        // get Intent
        nextPersonUid = getIntent().getExtras().get("Next_Person_Uid_Var").toString();

        // fetch next users details

        DatabaseReference next_person_datbase = FirebaseDatabase.getInstance().getReference().child("Users").child(nextPersonUid);
        DatabaseReference current_datbase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser_UID);

        next_person_datbase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String disname = "" + snapshot.child("displayname").getValue();
                next_user_name.setText(disname);

                String bio = "" + snapshot.child("About").child("bio").child("bio").getValue();
                next_user_bio.setText(bio);



                String nextPersonDpStr =  "" + snapshot.child("image").getValue();

                try {
                    Glide.with(getApplicationContext()).load(nextPersonDpStr).circleCrop().into(next_user_image);
                }
                catch (Exception e){
                    Glide.with(getApplicationContext()).load(R.drawable.user_icon).circleCrop().into(next_user_image);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        current_datbase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Follower").hasChild(nextPersonUid)){

                    following_state.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // loadnext user posts

        loadPosts();


    }


    private void loadPosts() {

        final DatabaseReference posts_databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(nextPersonUid).child("Posts");


        posts_databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    postList.clear();


                    for (DataSnapshot ds : snapshot.getChildren()) {

                        ModelShowPost modelShowPost = ds.getValue(ModelShowPost.class);

                        postList.add(modelShowPost);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

}