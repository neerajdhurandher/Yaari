package com.example.apptrail4;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apptrail4.AdapterUserRV2;
import com.example.apptrail4.ModelUser2;
import com.example.apptrail4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Follower_Activity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterUserRV2 adapterUserRV2;
    List<ModelUser2> userList;
    FloatingActionButton fl_search_btn,searchbtnTop;
    EditText search_follower;
    FirebaseUser currentuser;
    TextView CurrentuserNameTop,followertxt,nofollowertxt;
    ImageView CurrentuserImage;
   RelativeLayout searchLayout;

    private int followerCount= 0;


    public Follower_Activity(){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_);



        recyclerView = findViewById(R.id.user_recycleerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fl_search_btn = findViewById(R.id.floatingsearchbtn);
        searchbtnTop= findViewById(R.id.floatingsearchbtnTop);
        CurrentuserNameTop = findViewById(R.id.usernameTop);
        CurrentuserImage = findViewById(R.id.useriamgeS);
        searchLayout = findViewById(R.id.searchLayout);
        followertxt= findViewById(R.id.followerId);
        nofollowertxt = findViewById(R.id.nofollowerId);

        nofollowertxt.setVisibility(View.GONE);


        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserName = currentuser.getDisplayName();
        Uri currentUseImage = currentuser.getPhotoUrl();

        CurrentuserNameTop.setText(currentUserName);

        DatabaseReference followerDatabase = FirebaseDatabase.getInstance().getReference("Users").child(currentuser.getUid()).child("Follower");

        followerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    followerCount = (int) snapshot.getChildrenCount();
                    followertxt.setText("FOLLOWER   "+ " ( "+Integer.toString(followerCount)+ " ) ");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            Glide.with(getApplicationContext()).load(currentUseImage).circleCrop().into(CurrentuserImage);
        }
        catch (Exception e){
            Glide.with(getApplicationContext()).load(R.drawable.user_icon).circleCrop().into(CurrentuserImage);

        }



        userList  = new ArrayList<>();
        getAllFollower();



        search_follower = findViewById(R.id.search_user_box_id);
        search_follower.setVisibility(View.GONE);
        fl_search_btn.setVisibility(View.GONE);
        searchLayout.setVisibility(View.GONE);

        searchbtnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.VISIBLE);
                search_follower.setVisibility(View.VISIBLE);
                fl_search_btn.setVisibility(View.VISIBLE);
                searchbtnTop.setVisibility(View.GONE);
            }
        });


        final String searchfollower = search_follower.getText().toString();


        search_follower.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                getAllFollower();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchfollower = search_follower.getText().toString();
                SearchFollower(searchfollower);

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchfollower = search_follower.getText().toString();
                SearchFollower(searchfollower);

            }
        });

        fl_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String searchfollower = search_follower.getText().toString();

                if (searchfollower.isEmpty()){
                    getAllFollower();
                }
                else {

                    SearchFollower(searchfollower);
                }

            }
        });



        return;



    }

    private void SearchFollower(final String searchfollower) {

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(currentuser.getUid()).child("Follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    ModelUser2 modelUser2 = ds.getValue(ModelUser2.class);



                        if (!searchfollower.isEmpty()){

                            assert modelUser2 != null;
                            if (modelUser2.getDisplayname().toLowerCase().contains(searchfollower.toLowerCase()) ||
                                    modelUser2.getEmail().toLowerCase().contains(searchfollower.toLowerCase())){

                                userList.add(modelUser2);

                            }
                        }
                        else {
                            getAllFollower();
                        }



                    adapterUserRV2 = new AdapterUserRV2(getApplicationContext(),userList);
                    adapterUserRV2.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterUserRV2);

                }

    }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAllFollower() {


        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(currentuser.getUid()).child("Follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    ModelUser2 modelUser2 = ds.getValue(ModelUser2.class);


                        userList.add(modelUser2);



                    adapterUserRV2= new AdapterUserRV2(getApplicationContext(),userList);
                    recyclerView.setAdapter(adapterUserRV2);

                }

                if (userList.isEmpty()){

                    followertxt.setVisibility(View.GONE);
                    nofollowertxt.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);

                    getAllUsers();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAllUsers() {

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    ModelUser2 modelUser2 = ds.getValue(ModelUser2.class);

                    if(!modelUser2.getUid().equals(currentuser.getUid())){

                        userList.add(modelUser2);
                    }


                    adapterUserRV2 = new AdapterUserRV2(getApplicationContext(),userList);
                    recyclerView.setAdapter(adapterUserRV2);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
