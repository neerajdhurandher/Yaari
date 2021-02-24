package com.example.apptrail4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AboutActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase Fdatabase;
    private DatabaseReference databaseRefrence;
    private GoogleSignInClient mGoogleSignInClient;
    TextView nameD,emailD,dobD,instrestD,relationshipD,cityD;
    Button back_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        nameD = findViewById(R.id.nameBody);
        emailD= findViewById(R.id.emailBody);
        dobD= findViewById(R.id.dobBody);
        instrestD= findViewById(R.id.intrestBody);
        relationshipD= findViewById(R.id.relationstatusBody);
        cityD= findViewById(R.id.cityBody);
        back_btn= findViewById(R.id.backbtn);

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        Fdatabase = FirebaseDatabase.getInstance();
        databaseRefrence = Fdatabase.getReference("Users");



        Query query = databaseRefrence.orderByChild("email").equalTo(currentUser.getEmail());


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {


                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                      String nametxt = ""+currentUser.getDisplayName();
                      nameD.setText(nametxt);
                        String emailtxt = "" + dataSnapshot.child("email").getValue();
                        emailD.setText(emailtxt);


                        if (dataSnapshot.child("About").child("dob").child("dob").exists()) {
                            String dobtxt = "" + dataSnapshot.child("About").child("dob").child("dob").getValue();
                            if (!dobtxt.isEmpty()) {
                                dobD.setText(dobtxt);
                            }
                        } else {
                           dobD.setText("");
                        }
                        if (dataSnapshot.child("About").child("intrest").child("intrest").exists()) {
                            String intresttxt = "" + dataSnapshot.child("About").child("intrest").child("intrest").getValue();

                            if (!intresttxt.isEmpty()) {
                                instrestD.setText(intresttxt);
                            }
                        } else {
                            instrestD.setText("");
                        }
                        if (dataSnapshot.child("About").child("relationshipstatus").child("relationshipstatus").exists()) {
                            String relationshipstatustxt = "" + dataSnapshot.child("About").child("relationshipstatus").child("relationshipstatus").getValue();

                            if (!relationshipstatustxt.isEmpty()) {
                                relationshipD.setText(relationshipstatustxt);
                            }
                        } else {
                            relationshipD.setText("");
                        }
                        if (dataSnapshot.child("About").child("city").child("city").exists()) {
                            String citytxt = "" + dataSnapshot.child("About").child("city").child("city").getValue();

                            if (!citytxt.isEmpty()) {
                                cityD.setText(citytxt);
                            }
                        } else {
                            cityD.setText("");
                        }



                    } //for loop end
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AboutActivity.this, "Database Error Occurs", Toast.LENGTH_SHORT).show();

            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotohome = new Intent(AboutActivity.this,HomeActivity.class);
                startActivity(gotohome);
            }
        });


    }
}
