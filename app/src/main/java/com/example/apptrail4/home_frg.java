package com.example.apptrail4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class home_frg extends Fragment {

    FirebaseUser currentuser;
    String myUid;


    RecyclerView recyclerView;

    List<ModelShowPost> postList;
    Adapter_Show_Post adapter_show_post;




    public home_frg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_frg, container, false);

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        myUid = currentuser.getUid();

        recyclerView = view.findViewById(R.id.post_recycleerView);







        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        postList = new ArrayList<>();




        loadPosts();



        return view;

    }




    private void loadPosts() {

        final DatabaseReference posts_databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        posts_databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    postList.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {

                        ModelShowPost modelShowPost = ds.getValue(ModelShowPost.class);

                        postList.add(modelShowPost);
                        adapter_show_post = new Adapter_Show_Post(getActivity(), postList);
                        recyclerView.setAdapter(adapter_show_post);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }




    private void checkOnlineStatus(String status){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> hashMap_OnlineStatus = new HashMap<>();
        hashMap_OnlineStatus.put("onlineStatus",status);

        databaseReference.child(myUid).updateChildren(hashMap_OnlineStatus);


    }



    @Override
    public void onStart() {
        checkOnlineStatus("Online");
        super.onStart();
    }

    @Override
    public  void onPause() {
        super.onPause();
//        set timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);

    }

    @Override
    public void onResume() {
        checkOnlineStatus("Online");
        super.onResume();
    }
}
