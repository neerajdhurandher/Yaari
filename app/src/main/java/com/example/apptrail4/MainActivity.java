package com.example.apptrail4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    // FeedBack Form



    FirebaseUser currentuser;
    String currentuserUID;
   EditText Experiencce_space,Like_aboutapp_space,Bug_space,Suggestion_space;
   String Experiencce_string,Like_aboutapp_string,Bug_string,Suggestion_string;
   Button Submmit_Btn;
   RelativeLayout thanks_view_layout;
   LinearLayout feedback_form_Layout;
   Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        currentuserUID = currentuser.getUid();

        Experiencce_space = findViewById(R.id.experience_Id);
        Like_aboutapp_space = findViewById(R.id.like_aboutapp_Id);
        Bug_space = findViewById(R.id.bug_Id);
        Suggestion_space = findViewById(R.id.suggestion_Id);

        Submmit_Btn =  findViewById(R.id.submmit_btn_Id);

        thanks_view_layout= findViewById(R.id.thanks_view_layout);
        feedback_form_Layout= findViewById(R.id.feedback_form_layout);
//        toolbar= findViewById(R.id.toolbarId);



        thanks_view_layout.setVisibility(View.GONE);


        final DatabaseReference feedbackDB = FirebaseDatabase.getInstance().getReference("Feedback");

        final String timestamp = String.valueOf(System.currentTimeMillis());




        Submmit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Experiencce_string = Experiencce_space.getText().toString()+" ";
                Like_aboutapp_string = Like_aboutapp_space.getText().toString()+" ";
                Bug_string =Bug_space.getText().toString()+" ";
                Suggestion_string = Suggestion_space.getText().toString()+" ";




                feedbackDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        feedbackDB.child(currentuserUID).child(timestamp).child("userName").setValue(currentuser.getDisplayName());
                        feedbackDB.child(currentuserUID).child(timestamp).child("userUID").setValue(currentuserUID);
                        feedbackDB.child(currentuserUID).child(timestamp).child("Time").setValue(timestamp);
                        feedbackDB.child(currentuserUID).child(timestamp).child("userImsge").setValue(Objects.requireNonNull(currentuser.getPhotoUrl()).toString());
                        feedbackDB.child(currentuserUID).child(timestamp).child("Experiencce_").setValue(Experiencce_string);
                        feedbackDB.child(currentuserUID).child(timestamp).child("Like_aboutapp").setValue(Like_aboutapp_string);
                        feedbackDB.child(currentuserUID).child(timestamp).child("Bug_Report").setValue(Bug_string);
                        feedbackDB.child(currentuserUID).child(timestamp).child("Suggestion").setValue(Suggestion_string);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Toast.makeText(MainActivity.this, "Thanks for your Feedback", Toast.LENGTH_LONG).show();
                feedback_form_Layout.setVisibility(View.GONE);
//                toolbar.setVisibility(View.GONE);

                thanks_view_layout.setVisibility(View.VISIBLE);

                Intent gotohome =  new Intent(MainActivity.this, HomeActivity.class);
                try {
                    new Intent(MainActivity.this, HomeActivity.class).wait(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(gotohome);
                finish();




            }
        });









    }



}
