package com.example.apptrail4;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Post_Comments_Activity extends AppCompatActivity {


    String pId_fetchted;

    RecyclerView recyclerView_show_comments;

    List<Model_Show_Comment> show_commentList;
    Adapter_Show_Comment adapter_show_comment;

    ImageView userImage,postedIamge,postedIamgeOpen;
    TextView userName,userBio,postTime,postDescripton,likecounter,commentcounter;
    Button likeBtn,likedBtn,commentBtn;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    String currentUserUID;
    private int likeCount = 0, commentCount = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__comments_);

        pId_fetchted = getIntent().getExtras().get("pId").toString();

        show_commentList = new ArrayList<>();

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        currentUserUID = currentUser.getUid();


        userImage = findViewById(R.id.useriamgeS);
        userName = findViewById(R.id.usernameS);
        userBio = findViewById(R.id.bioS);
        postTime = findViewById(R.id.posttimeS);

        postedIamge = findViewById(R.id.postedImgaeId);
//        postedIamgeOpen = findViewById(R.id.postedImageOpenId);
        postDescripton = findViewById(R.id.photodeccriptionid);
        likecounter = findViewById(R.id.like_commentShowid);
        commentcounter = findViewById(R.id.comment_Count_Id);

        likeBtn = findViewById(R.id.likebtnId);
        likedBtn = findViewById(R.id.likedbtnId);
        commentBtn = findViewById(R.id.commentbtnId);

        recyclerView_show_comments = findViewById(R.id.post_recycleerView_show_comment);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView_show_comments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



        load_Post();
        likeStatus();
        load_Post_Comments();


        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotohome = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(gotohome);
            }
        });


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add like in database

                final DatabaseReference databaseReference_like = FirebaseDatabase.getInstance().getReference("Likes");


                databaseReference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Toast.makeText(getApplicationContext(), "Like", Toast.LENGTH_SHORT).show();

                        databaseReference_like.child(pId_fetchted).child(currentUserUID).child("Like").setValue("liked");
                        databaseReference_like.child(pId_fetchted).child(currentUserUID).child("userName").setValue(currentUser.getDisplayName());
                        databaseReference_like.child(pId_fetchted).child(currentUserUID).child("userImage").setValue(currentUser.getPhotoUrl().toString());
                        databaseReference_like.child(pId_fetchted).child(currentUserUID).child("userUID").setValue(currentUser.getUid());



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                likedBtn.setVisibility(View.VISIBLE);
                likedBtn.setEnabled(true);
                likeBtn.setVisibility(View.GONE);
                likeBtn.setEnabled(false);

            }
        });


       likedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // remove like from database

                final DatabaseReference databaseReference_like = FirebaseDatabase.getInstance().getReference("Likes");

                databaseReference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(pId_fetchted).child(currentUserUID).exists()) {

                            databaseReference_like.child(pId_fetchted).child(currentUserUID).removeValue();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                likedBtn.setVisibility(View.GONE);
                likedBtn.setEnabled(false);
             likeBtn.setVisibility(View.VISIBLE);
                likeBtn.setEnabled(true);


            }
        });







    }

    private void likeStatus() {

        // check from database and Set like and liked button visibility
        final DatabaseReference databaseReference_like = FirebaseDatabase.getInstance().getReference("Likes");

        databaseReference_like.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(pId_fetchted).child(currentUserUID).exists()) {


                    likedBtn.setVisibility(View.VISIBLE);
                   likedBtn.setEnabled(true);
                    likeBtn.setVisibility(View.GONE);
                   likeBtn.setEnabled(false);

                }
                else {
                    likedBtn.setVisibility(View.GONE);
                    likedBtn.setEnabled(false);
                    likeBtn.setVisibility(View.VISIBLE);
                    likeBtn.setEnabled(true);

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // like count and show

        databaseReference_like.child(pId_fetchted).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    likeCount = (int) snapshot.getChildrenCount();

                    if (snapshot.child(currentUserUID).exists()) {
                        likecounter.setText(" You and " + Integer.toString(likeCount-1) + " others ");
                    }
                    else {
                       likecounter.setText(Integer.toString(likeCount));

                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // count comments

        final DatabaseReference databaseReference_comment = FirebaseDatabase.getInstance().getReference("Comments");
        databaseReference_comment.child(pId_fetchted).child("Comment").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    commentCount = (int) snapshot.getChildrenCount();

                    if (commentCount == 0) {
                        commentcounter.setVisibility(View.GONE);
                    }
                    else {
                        commentcounter.setVisibility(View.VISIBLE);
                        commentcounter.setText(Integer.toString(commentCount)+"Comments");

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }

    private void load_Post() {
        final DatabaseReference post_DB = FirebaseDatabase.getInstance().getReference("Posts");
        post_DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    if (snapshot.child(pId_fetchted).exists()){


                        String uName = ""+snapshot.child(pId_fetchted).child("uName").getValue();
                        String uBio = ""+snapshot.child(pId_fetchted).child("uBio").getValue();
                        String uEmail = ""+snapshot.child(pId_fetchted).child("uEmail").getValue();
                        String caption = ""+snapshot.child(pId_fetchted).child("caption").getValue();
                        String pDate_Time = ""+snapshot.child(pId_fetchted).child("pDate_Time").getValue();
                        String uImage = ""+snapshot.child(pId_fetchted).child("uImage").getValue();
                        String uploadImage = ""+snapshot.child(pId_fetchted).child("uploadImage").getValue();


                        // covert timeStamp to normal time formate
                        Calendar calendar =  Calendar.getInstance(Locale.ENGLISH);
                        calendar.setTimeInMillis(Long.parseLong(pDate_Time));
                        final String dateTime = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();



                        userName.setText(uName);
                        userBio.setText(uBio);
                        postTime.setText(dateTime);
                        postDescripton.setText(caption);

                        try {
                            Glide.with(getApplicationContext()).load(uImage).circleCrop().into(userImage);

                        }
                        catch (Exception e) {
                            Glide.with(getApplicationContext()).load(R.drawable.user_icon).circleCrop().into(userImage);

                        }

                        try {
                            Glide.with(getApplicationContext()).load(uploadImage).fitCenter().into(postedIamge);

                        }
                        catch (Exception e) {

                        }



                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void load_Post_Comments() {


        final DatabaseReference post_commentsDB = FirebaseDatabase.getInstance().getReference("Comments");

        post_commentsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child(pId_fetchted).exists()) {

                        DatabaseReference readComments =   post_commentsDB.child(pId_fetchted).child("Comment");
                        readComments.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){

                                    show_commentList.clear();

                                for (DataSnapshot ds : snapshot.getChildren()) {

                                    Model_Show_Comment modelShowComment = ds.getValue(Model_Show_Comment.class);

                                    show_commentList.add(modelShowComment);
                                    adapter_show_comment = new Adapter_Show_Comment(getApplicationContext(), show_commentList);
                                    recyclerView_show_comments.setAdapter(adapter_show_comment);


                                }
                            }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
