package com.example.apptrail4;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class Story_view_Activity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long pressTime = 0L;
    long limit = 50L;


    StoriesProgressView storiesProgressView;
    ImageView story_image,story_user_image;
    TextView story_username;
    LinearLayout seen_layout;
    TextView seen_count;
    ImageView story_delete;

    FirebaseUser currentUser;


    List<String> imageList;
    List<String> story_Ids;
    String user_Id;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()){

                case  MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;

                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now-pressTime;

            }

            return false;
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view_);



        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        storiesProgressView = findViewById(R.id.stories);
        story_user_image = findViewById(R.id.story_user_image);
        story_image = findViewById(R.id.story_image_view);
        story_username = findViewById(R.id.story_username);

        seen_layout = findViewById(R.id.seen_layout_id);
        seen_count = findViewById(R.id.seen_count_id);
        story_delete = findViewById(R.id.story_delete_id );

        seen_layout.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);


        user_Id = getIntent().getExtras().get("userid").toString();

        if (user_Id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

            seen_layout.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);

        }



        get_Stories(user_Id);
        userInfo(user_Id);

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        View skip = findViewById(R.id.skip);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);


        seen_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goto_story_view_users = new Intent(Story_view_Activity.this,Story_seen_users_Activity.class);

                goto_story_view_users.putExtra("user_id", user_Id);
                goto_story_view_users.putExtra("story_id", story_Ids.get(counter));

                startActivity(goto_story_view_users);




            }
        });


        story_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference story_db_ref_delete = FirebaseDatabase.getInstance().getReference("Story")
                        .child(user_Id).child(story_Ids.get(counter));

                story_db_ref_delete.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Story_view_Activity.this, "Delete Successfully !", Toast.LENGTH_SHORT).show();
                           finish();
                        }
                    }
                });


            }
        });




    }

    @Override
    public void onNext() {
        try {
            Glide.with(getApplicationContext()).load(imageList.get(++counter)).into(story_image);
        }
        catch (Exception e){
            Glide.with(getApplicationContext()).load(R.drawable.ic_image).into(story_image);
        }

        add_views(story_Ids.get(counter));
        seen_user_count(story_Ids.get(counter));

    }

    @Override
    public void onPrev() {
        if ((counter-1)<0 )
            return;
        try {
            Glide.with(getApplicationContext()).load(imageList.get(--counter)).into(story_image);
        }
        catch (Exception e){
            Glide.with(getApplicationContext()).load(R.drawable.ic_image).into(story_image);
        }

        seen_user_count(story_Ids.get(counter));


    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }




    private  void get_Stories(String user_Id){

        imageList = new ArrayList<>();
        story_Ids = new ArrayList<>();

        final DatabaseReference story_db_ref = FirebaseDatabase.getInstance().getReference("Story").child(user_Id);

        story_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageList.clear();
                story_Ids.clear();

                for(DataSnapshot ds: snapshot.getChildren()){

                    ModelStory modelStory_1 = ds.getValue(ModelStory.class);

                    long current_time = System.currentTimeMillis();

                    if (current_time > modelStory_1.getTime_start() && current_time < modelStory_1.getTime_end()){

                        imageList.add(modelStory_1.getImage_url());
                        story_Ids.add(modelStory_1.getStory_Id());


                    }else {
                        // delete story from db
                        story_db_ref.child(modelStory_1.getStory_Id()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }

                }
                storiesProgressView.setStoriesCount(imageList.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(Story_view_Activity.this);
                storiesProgressView.startStories(counter);

                try {
                    Glide.with(getApplicationContext()).load(imageList.get(counter)).into(story_image);
                }
                catch (Exception e){
                    Glide.with(getApplicationContext()).load(R.drawable.ic_image).into(story_image);
                }


                add_views(story_Ids.get(counter));
                seen_user_count(story_Ids.get(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void userInfo(String user_Id){

        DatabaseReference user_db_ref = FirebaseDatabase.getInstance().getReference("Users").child(user_Id);

        user_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser_2 = snapshot.getValue(ModelUser.class);

                try {
                    Glide.with(getApplicationContext()).load(modelUser_2.getImage()).circleCrop().into(story_user_image);
                }
                catch (Exception e){
                    Glide.with(getApplicationContext()).load(R.drawable.user_icon).circleCrop().into(story_user_image);

                }

                story_username.setText(modelUser_2.getDisplayname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    private void add_views (String story_Id){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(user_Id).child(story_Id);

       long current_time_l = System.currentTimeMillis();
       String current_time = ""+current_time_l;

        Calendar calendar =  Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(current_time));
        String dateTime = DateFormat.format("dd/MM/yy hh:mm a",calendar).toString();

        HashMap<String,String> hashMap_views = new HashMap<>();

        hashMap_views.put("displayname",currentUser.getDisplayName());
        hashMap_views.put("uid", currentUser.getUid());
        hashMap_views.put("image", currentUser.getPhotoUrl().toString());
        hashMap_views.put("email", "seen "+ dateTime);

        reference.child("views").child(currentUser.getUid()).setValue(hashMap_views);

    }

    private void seen_user_count (String story_Id){

        DatabaseReference story_db_ref_seen_user = FirebaseDatabase.getInstance().getReference("Story")
                .child(user_Id).child(story_Id).child("views");

        story_db_ref_seen_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                seen_count.setText(""+ snapshot.getChildrenCount()+" Views");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

}