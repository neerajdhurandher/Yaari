package com.example.apptrail4;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Story_seen_users_Activity extends AppCompatActivity {


    FirebaseUser currentUseer;
    String current_User_UID;
    String story_id,user_id;

    RecyclerView recyclerView;

    List<ModelUser> seen_user_list;
    AdapterUserRV adapterUserRV;

    TextView views_banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_seen_users_);


        currentUseer =  FirebaseAuth.getInstance().getCurrentUser();
        current_User_UID = currentUseer.getUid();

        views_banner = findViewById(R.id.views_banner_id);
        recyclerView = findViewById(R.id.user_recycleerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        story_id = getIntent().getStringExtra("story_id");
        user_id = getIntent().getStringExtra("user_id");


        seen_user_list = new ArrayList<>();

        getViews_count(user_id);

        seen_user_count(story_id);





    }

    private void getViews_count(String user_id) {

        DatabaseReference story_db_ref_views = FirebaseDatabase.getInstance().getReference("Story")
                .child(user_id).child(story_id).child("views");

        story_db_ref_views.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            seen_user_list.clear();
            for (DataSnapshot ds: snapshot.getChildren()){

                ModelUser modelUser = ds.getValue(ModelUser.class);

                if(modelUser.getUid().equals(current_User_UID)){


                    modelUser.setDisplayname("You");
                    modelUser.setImage(currentUseer.getPhotoUrl().toString());

                }


                seen_user_list.add(modelUser);
                adapterUserRV = new AdapterUserRV(getApplicationContext(),seen_user_list);
                recyclerView.setAdapter(adapterUserRV);

            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void seen_user_count (String story_Id){


        DatabaseReference story_db_ref_seen_user = FirebaseDatabase.getInstance().getReference("Story")
                .child(user_id).child(story_Id).child("views");

        story_db_ref_seen_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                views_banner.setText("Views On Your Story  ( " + snapshot.getChildrenCount() + " )");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}