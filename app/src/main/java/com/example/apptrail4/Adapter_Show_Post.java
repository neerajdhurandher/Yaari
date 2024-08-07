package com.example.apptrail4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class Adapter_Show_Post extends  RecyclerView.Adapter<Adapter_Show_Post.PostHolder> {

    Context context;
    List<ModelShowPost> postList;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    String currentUserUID,currentUser_Image;
    private int likeCount = 0,commentCount = 0;
    RecyclerView recyclerView_comment_show;


    Adapter_Show_Comment adapter_show_comment;
    List<Model_Show_Comment> showCommentList;






    public Adapter_Show_Post(Context context, List<ModelShowPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, viewGroup,false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        currentUserUID = currentUser.getUid().toString();
        currentUser_Image = currentUser.getPhotoUrl().toString();


        recyclerView_comment_show = view.findViewById(R.id.post_recycleerView_show_comment);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

//        recyclerView_comment_show.setLayoutManager(new LinearLayoutManager(context));

        showCommentList = new ArrayList<>();


        return new PostHolder(view) ;

    }

    @Override
    public void onBindViewHolder(@NonNull final PostHolder holder, int i) {

        final String uid = postList.get(i).getUid();
        String uName = postList.get(i).getuName();
        String uEmail = postList.get(i).getuEmail();
        String uBio = postList.get(i).getuBio();
        String  uImage = postList.get(i).getuImage();
        String pTime = postList.get(i).getpDate_Time();
        final String pId = postList.get(i).getpId();
        String caption = postList.get(i).getCaption();
        final String  uploadImage = postList.get(i).getUploadImage();



        // covert timeStamp to normal time formate
        Calendar calendar =  Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(pTime));
        final String dateTime = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();

        Calendar current_calendar = Calendar.getInstance(Locale.ENGLISH);
        final String currentdateTime =  String.valueOf(System.currentTimeMillis());



        holder.userName.setText(uName);
        holder.userBio.setText(uBio);
        holder.postTime.setText(dateTime);
        holder.postDescripton.setText(caption);


        try {
            Glide.with(context).load(uImage).circleCrop().into(holder.userImage);

        }
        catch (Exception e) {
            Glide.with(context).load(R.drawable.user_icon).circleCrop().into(holder.userImage);

        }

        try {
            Glide.with(context).load(uploadImage).fitCenter().into(holder.postedIamge);

        }
        catch (Exception e) {

        }


        // check from database and Set like and liked button visibility
        final DatabaseReference databaseReference_like = FirebaseDatabase.getInstance().getReference("Likes");

        databaseReference_like.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(pId).child(currentUserUID).exists()) {


                    holder.likedBtn.setVisibility(View.VISIBLE);
                    holder.likedBtn.setEnabled(true);
                    holder.likeBtn.setVisibility(View.GONE);
                    holder.likeBtn.setEnabled(false);

                } }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // like count and show

        databaseReference_like.child(pId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    likeCount = (int) snapshot.getChildrenCount();

                    if (snapshot.child(currentUserUID).exists()) {

                        if (Integer.toString(likeCount - 1).equals("0")) {
                            holder.likecounter.setText("You ");


                        } else {
                            holder.likecounter.setText(" You and " + Integer.toString(likeCount - 1) + " others ");
                        }
                    }
                    else {
                        holder.likecounter.setText(Integer.toString(likeCount));

                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // comment count and show

        final DatabaseReference databaseReference_comment = FirebaseDatabase.getInstance().getReference("Comments");
        databaseReference_comment.child(pId).child("Comment").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    commentCount = (int) snapshot.getChildrenCount();

                    if (commentCount == 0) {
                        holder.commentcounter.setVisibility(View.GONE);
                    }
                    else {
                        holder.commentcounter.setVisibility(View.VISIBLE);
                        holder.commentcounter.setText(Integer.toString(commentCount)+"Comments");

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // add like in database

                final DatabaseReference databaseReference_like = FirebaseDatabase.getInstance().getReference("Likes");


                databaseReference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Toast.makeText(context, " Like ", Toast.LENGTH_SHORT).show();

                        databaseReference_like.child(pId).child(currentUserUID).child("Like").setValue("liked");
                        databaseReference_like.child(pId).child(currentUserUID).child("userName").setValue(currentUser.getDisplayName());
                        databaseReference_like.child(pId).child(currentUserUID).child("userImage").setValue(currentUser.getPhotoUrl().toString());
                        databaseReference_like.child(pId).child(currentUserUID).child("userUID").setValue(currentUser.getUid());



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.likedBtn.setVisibility(View.VISIBLE);
                holder.likedBtn.setEnabled(true);
                holder.likeBtn.setVisibility(View.GONE);
                holder.likeBtn.setEnabled(false);

            }
        });


        holder.likedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // remove like from database

                databaseReference_like.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(pId).child(currentUserUID).exists()) {

                            databaseReference_like.child(pId).child(currentUserUID).removeValue();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.likedBtn.setVisibility(View.GONE);
                holder.likedBtn.setEnabled(false);
                holder.likeBtn.setVisibility(View.VISIBLE);
                holder.likeBtn.setEnabled(true);


            }
        });


        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.commentLayout.setVisibility(View.VISIBLE);
                holder.editComment.setVisibility(View.VISIBLE);
                holder.commentPostBtn.setVisibility(View.VISIBLE);
                holder.Read_comments_btn.setVisibility(View.VISIBLE);

                try {
                    Glide.with(context).load(currentUser_Image).circleCrop().into(holder.currentUser_Iamge);

                }
                catch (Exception e) {
                    Glide.with(context).load(R.drawable.user_icon).circleCrop().into(holder.currentUser_Iamge);

                }

                holder.postedIamge.getMinimumHeight();

//                loadcomment();







//                ViewGroup.LayoutParams layoutParams = holder.post_Layout.getLayoutParams();
//                layoutParams.height = 730;
//                holder.post_Layout.setLayoutParams(layoutParams);

//                Toast.makeText(context, "bhai abhi isme kaam chl raha hai", Toast.LENGTH_SHORT).show();

                final String typed_Comment =  holder.editComment.getText().toString();

            }
        });



        holder.commentPostBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final String typed_Comment =  holder.editComment.getText().toString();


                if (typed_Comment.isEmpty()) {
                    Toast.makeText(context, "Type Some Commnent", Toast.LENGTH_SHORT).show();

                }

                else{

                     // add comment in database

                    final DatabaseReference databaseReference_comment = FirebaseDatabase.getInstance().getReference("Comments");

                    databaseReference_comment.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                final String timestamp = String.valueOf(System.currentTimeMillis());

//                               final DatabaseReference refto_Comment_inside_Post_pId =  FirebaseDatabase.getInstance().getReference("Comments").child(pId).child("Comment");

                                        // Add Comment
                                        databaseReference_comment.child(pId).child("Comment").child(timestamp).child("commenttxt").setValue(typed_Comment);
                                        databaseReference_comment.child(pId).child("Comment").child(timestamp).child("time").setValue(currentdateTime);
                                        databaseReference_comment.child(pId).child("Comment").child(timestamp).child("cId").setValue(currentdateTime+"_"+currentUserUID);
                                        databaseReference_comment.child(pId).child("Comment").child(timestamp).child("uName").setValue(currentUser.getDisplayName());
                                        databaseReference_comment.child(pId).child("Comment").child(timestamp).child("uDp").setValue(currentUser.getPhotoUrl().toString());
                                        databaseReference_comment.child(pId).child("Comment").child(timestamp).child("uUID").setValue(currentUser.getUid());

                                        Toast.makeText(context, "Comment Posted", Toast.LENGTH_SHORT).show();
                                        holder.editComment.setText("");

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            holder.commentLayout.setVisibility(View.GONE);


                        }
                    }
                });




        holder.Read_comments_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gotoReadComments = new Intent(context,Post_Comments_Activity.class);
                gotoReadComments.putExtra("pId",pId);
                context.startActivity(gotoReadComments);



            }
        });


        // view posted image in full screen..

        holder.postedIamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent postDetailsActivity = new Intent(context,Post_Comments_Activity.class);
                postDetailsActivity.putExtra("pId",pId);
                context.startActivity(postDetailsActivity);

//                holder.postedUserDetailsLayout.setVisibility(View.GONE);
//                holder.postedItemLayout.setVisibility(View.GONE);
//                holder.postedIamgeOpen.setVisibility(View.VISIBLE);
//
//                try {
//                    Glide.with(context).load(uploadImage).into(holder.postedIamgeOpen);
//
//                }
//                catch (Exception e) {
//
//                }



            }
        });



//        holder.postedIamgeOpen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                holder.postedUserDetailsLayout.setVisibility(View.VISIBLE);
//                holder.postedItemLayout.setVisibility(View.VISIBLE);
//                holder.postedIamgeOpen.setVisibility(View.GONE);
//
//            }
//        });

        holder.postedUserDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String Next_Person_Uid = uid;
                if (!uid.equals(currentUserUID)) {
                    Intent gotoNextPersonProfile = new Intent(context, Next_User_Profile_Activity.class);
                    gotoNextPersonProfile.putExtra("Next_Person_Uid_Var", Next_Person_Uid);
                    context.startActivity(gotoNextPersonProfile);
                }

            }
        });





    }





    @Override
    public int getItemCount() {
        return postList.size();
    }


    static class PostHolder extends RecyclerView.ViewHolder{

               ImageView userImage,postedIamge,postedIamgeOpen,currentUser_Iamge;
               TextView userName,userBio,postTime,postDescripton,likecounter,commentcounter;
               Button likeBtn,likedBtn,commentBtn,commentPostBtn,Read_comments_btn;
               EditText editComment;
               LinearLayout post_Layout,commentLayout,postedUserDetailsLayout,postedItemLayout;





        public PostHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.useriamgeS);
            userName = itemView.findViewById(R.id.usernameS);
            userBio = itemView.findViewById(R.id.bioS);
            postTime = itemView.findViewById(R.id.posttimeS);
            currentUser_Iamge = itemView.findViewById(R.id.currentUseriamgeS);

            postedIamge = itemView.findViewById(R.id.postedImgaeId);
//            postedIamgeOpen = itemView.findViewById(R.id.postedImageOpenId);
            postDescripton = itemView.findViewById(R.id.photodeccriptionid);
            likecounter = itemView.findViewById(R.id.like_commentShowid);
            commentcounter = itemView.findViewById(R.id.comment_Count_Id);

            editComment = itemView.findViewById(R.id.editcommentId);

            likeBtn = itemView.findViewById(R.id.likebtnId);
            likedBtn = itemView.findViewById(R.id.likedbtnId);
            commentBtn = itemView.findViewById(R.id.commentbtnId);
            commentPostBtn = itemView.findViewById(R.id.commentPostId);
            Read_comments_btn = itemView.findViewById(R.id.read_comments);

            post_Layout = itemView.findViewById(R.id.post_Layout);
            commentLayout = itemView.findViewById(R.id.commentLayoutId);
            postedUserDetailsLayout = itemView.findViewById(R.id.postedUserDetailsLayout);
            postedItemLayout = itemView.findViewById(R.id.postedItemsLayout);





//            postedIamgeOpen.setVisibility(View.GONE);
            commentLayout.setVisibility(View.GONE);
            editComment.setVisibility(View.GONE);
            commentPostBtn.setVisibility(View.GONE);
            likedBtn.setVisibility(View.GONE);
            likedBtn.setEnabled(false);
            Read_comments_btn.setVisibility(View.GONE);






        }


    }

}
