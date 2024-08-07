package com.example.apptrail4;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class FrequestsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Adapter_Pnd_Foll_Req adapterPndFollReq;
    List<ModelUser> PFR_user;

    AdapterUserRV2 adapterUserRV2;
    List<ModelUser2> userList;

    FirebaseUser currentuser;
    TextView nofollowReqPend,findNewYaar_View,Pending_Foll_Req;

    Button find_new_yarr_btn;
    ImageView new_yaar_Iamge;


    public FrequestsActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequests);


        recyclerView = findViewById(R.id.user_recycleerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



        nofollowReqPend= findViewById(R.id.noPendingFollReq);
        findNewYaar_View = findViewById(R.id.findnewYaar);
        Pending_Foll_Req = findViewById(R.id.pendingFollReq);
        find_new_yarr_btn = findViewById(R.id.btn_new_yaar);
        new_yaar_Iamge = findViewById(R.id.new_yaar_Image_id);




        findNewYaar_View.setVisibility(View.GONE);
        nofollowReqPend.setVisibility(View.GONE);
        new_yaar_Iamge.setVisibility(View.GONE);
        find_new_yarr_btn.setVisibility(View.GONE);


        currentuser = FirebaseAuth.getInstance().getCurrentUser();


        PFR_user  = new ArrayList<>();
        userList = new ArrayList<>();

        getAllPendingFollReq();



        return;


    }

    private void getAllPendingFollReq() {

        recyclerView.setVisibility(View.VISIBLE);

            currentuser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(currentuser.getUid()).child("Follow_Req_Pending").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    PFR_user.clear();
                    for (DataSnapshot ds: snapshot.getChildren()){

                        ModelUser modelUser = ds.getValue(ModelUser.class);


                        PFR_user.add(modelUser);

                        adapterPndFollReq = new Adapter_Pnd_Foll_Req(getApplicationContext(),PFR_user);
                        recyclerView.setAdapter(adapterPndFollReq);

                    }

                    if (PFR_user.isEmpty()){

                        No_Pending_Request();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        
        
    }

    private void No_Pending_Request() {

        Pending_Foll_Req.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        new_yaar_Iamge.setVisibility(View.VISIBLE);
        find_new_yarr_btn.setVisibility(View.VISIBLE);
        nofollowReqPend.setVisibility(View.VISIBLE);


        find_new_yarr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find_New_Yaar_Function();
            }
        });


        }

    private void find_New_Yaar_Function() {


        new_yaar_Iamge.setVisibility(View.GONE);
        find_new_yarr_btn.setVisibility(View.GONE);
        nofollowReqPend.setVisibility(View.GONE);


        findNewYaar_View.setVisibility(View.VISIBLE);

        recyclerView.setVisibility(View.VISIBLE);



        Toast.makeText(this, "All Users Show ", Toast.LENGTH_SHORT).show();

        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReferenceGetAllUser = FirebaseDatabase.getInstance().getReference("Users");
        final DatabaseReference databaseReference_check_follower = FirebaseDatabase.getInstance().getReference("Users").
                child(currentuser.getUid()).child("Follower");

        databaseReferenceGetAllUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){

                userList.clear();

                    for (DataSnapshot ds: snapshot.getChildren()){

                        final ModelUser2 modelUser2 = ds.getValue(ModelUser2.class); // get all user


                        if(!modelUser2.getUid().equals(currentuser.getUid())){

                            // check if user exists in follower list



//                            databaseReference_check_follower.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    if (snapshot.exists()){
//
//
//                                        for (DataSnapshot ds2 : snapshot.getChildren()){
//
//                                            ModelUser modelUser21 = ds2.getValue(ModelUser.class);
//
//                                            if (!modelUser21.getUid().equals(modelUser2.getUid())){
//
//                                                userList.add(modelUser2);
//                                            }
//
//
//
//                                        }
//
//                                    }
//                                    else {
//                                        userList.add(modelUser2);
//                                    }
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });



                            userList.add(modelUser2);


                        }


                    adapterUserRV2 = new AdapterUserRV2(getApplicationContext(), userList);
                    adapterUserRV2.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterUserRV2);

                }
//            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }



}
