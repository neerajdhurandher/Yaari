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

import com.example.apptrail4.Adapter_Show_Post;
import com.example.apptrail4.Adapter_Story;
import com.example.apptrail4.ModelShowPost;
import com.example.apptrail4.ModelStory;
import com.example.apptrail4.ModelUser;
import com.example.apptrail4.R;
import com.example.apptrail4.notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

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
    RecyclerView recyclerView_story;

    List<ModelShowPost> postList;
    Adapter_Show_Post adapter_show_post;

    List<ModelStory> storyList;
    Adapter_Story adapter_story;

    List<String> allUserList;




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

        recyclerView_story = view.findViewById(R.id.post_recycleerView_story);

        recyclerView.setHasFixedSize(true);
        recyclerView_story.setHasFixedSize(true);

        postList = new ArrayList<>();
        storyList = new ArrayList<>();


        // leaner layout adpater manager for post

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,true);

         layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter_show_post = new Adapter_Show_Post(getActivity(), postList);
        recyclerView.setAdapter(adapter_show_post);



       // leaner layout adpater manager for story

        LinearLayoutManager layoutManager_story = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        recyclerView_story.setLayoutManager(layoutManager_story);

        adapter_story = new Adapter_Story(getActivity(),storyList);

        recyclerView_story.setAdapter(adapter_story);



        all_users_id();

        updateToken(FirebaseInstanceId.getInstance().getToken());


        readStory();

        loadPosts();




        return view;

    }

    private void all_users_id() {

        allUserList = new ArrayList<>();

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference("Users");
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUserList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    ModelUser userall = ds.getValue(ModelUser.class);

                    if (!userall.getUid().equals(currentUser.getUid())) {

                        allUserList.add(ds.getKey());
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void updateToken(String tokenRefresh) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefresh);
        databaseRef.child(currentUser.getUid()).setValue(token);

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

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void readStory(){

        DatabaseReference story_db = FirebaseDatabase.getInstance().getReference().child("Story");

        story_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currenttime = System.currentTimeMillis();
                storyList.clear();

                storyList.add(new ModelStory("", 0,0,"",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));

                for (String id : allUserList){

                    int count_Story = 0;

                    ModelStory modelStory = null;

                    for (DataSnapshot ds: snapshot.child(id).getChildren()){
                        modelStory = ds.getValue(ModelStory.class);

                        if (currenttime > modelStory.getTime_start() && currenttime < modelStory.getTime_end()){

                            count_Story++;
                        }

                    }
                    if (count_Story > 0){

                        storyList.add(modelStory);
                    }

                }
                adapter_story.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
