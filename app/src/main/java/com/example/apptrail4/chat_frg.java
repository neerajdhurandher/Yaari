package com.example.apptrail4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
public class chat_frg extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelUser> userList;
    List<ModelChatList> chatListList;
    AdapteChatList adapteChatList;
    DatabaseReference databaseReference;
    FirebaseUser currentuser;
    String myUid;
    RelativeLayout no_chat_list;
    Button goto_follower_btn;

    public chat_frg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat_frg, container, false);



        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.chat_recycleview);
        currentuser= FirebaseAuth.getInstance().getCurrentUser();
        myUid = currentuser.getUid();
        no_chat_list = view.findViewById(R.id.no_chat_view_layout);
        goto_follower_btn = view.findViewById(R.id.goto_follower_Id);

        Checkuser();

        chatListList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chat_List").child(currentuser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatListList.clear();

                for (DataSnapshot ds: snapshot.getChildren()){

                    ModelChatList chatList = ds.getValue(ModelChatList.class);
                    chatListList.add(chatList);

                }
                if (chatListList.isEmpty()){
                    no_chat_list.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }
                else {

                    no_chat_list.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    loadChat();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());


        goto_follower_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goto_follower_intent = new Intent(getActivity(), Follower_Activity.class);
                startActivity(goto_follower_intent);
            }
        });




        return view;
    }

    private void updateToken(String token){


        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        databaseRef.child(myUid).setValue(token);

    }



    private void loadChat() {

        userList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    for (ModelChatList modelChatList : chatListList){

                        if (modelUser.getUid() != null && modelUser.getUid().equals(modelChatList.getId())){

                            userList.add(modelUser);
                            break;

                        }
                    }

                        adapteChatList = new AdapteChatList(getActivity(),userList);
                        recyclerView.setAdapter(adapteChatList);

                        for (int i = 0; i< userList.size();i++){
                            lastMessage(userList.get(i).getUid());
                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void lastMessage(final String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String theLastMessage = "default";
                String theLastMessage_type = "default";
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);

                    if (chat== null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String reciver  = chat.getReciver();

                    if (sender==null || reciver == null){
                        continue;
                    }
                    if (chat.getReciver().equals(currentuser.getUid()) && chat.getSender().equals(userId) ||
                            chat.getReciver().equals(userId) && chat.getSender().equals(currentuser.getUid())   ){

                        if (chat.message_type.equals("image")){

                            theLastMessage_type = "image";
                            theLastMessage = "Image_hai_hgashjflyhoifheoyfhoiahfiof7q9084hrue67ryef697t02y7tv7ty28063yt8y8yf";

                        }
                        if (chat.message_type.equals("text")){


                            theLastMessage = chat.getMessage();
                            theLastMessage_type = "text";

                        }

                    }
                }

                adapteChatList.setLastMessageMap(userId,theLastMessage);
                adapteChatList.setLastMessagetypeMap(userId,theLastMessage_type);
                adapteChatList.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void Checkuser(){

        currentuser = firebaseAuth.getCurrentUser();
        if (currentuser!= null){



        }
        else {
            startActivity(new Intent(getActivity(), FeedbackActivity.class));
            getActivity().finish();


        }
    }

    private void checkOnlineStatus(String status){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> hashMap_OnlineStatus = new HashMap<>();
        hashMap_OnlineStatus.put("onlineStatus",status);

        databaseReference.child(myUid).updateChildren(hashMap_OnlineStatus);


    }



    @Override
    public void onStart() {
        Checkuser();
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
