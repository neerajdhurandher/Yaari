package com.example.apptrail4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    public static final String loginkeyLA = "com.example.apptrail4.loginwelcome";

    private SignInButton googleSigninbtn;
    private int RC_SIGN_IN = 1;
    private CheckBox checkBox;
     FirebaseAuth mFirebaseAuth;
     FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleSignInClient getSignInIntent;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();

        googleSigninbtn = findViewById(R.id.google_button);
        checkBox = findViewById(R.id.checkBox);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
                                                                // this help to sigin again if user already exits.....

                    Intent i = new Intent(LoginActivity.this, Welcome_Activity.class);
                    startActivity(i);
                    finish();

                }
                else{
                    Toast.makeText(LoginActivity.this,"Please Login",Toast.LENGTH_SHORT).show();
                }
            }
        };




        GoogleSignInOptions googleSignInOptions  = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = (GoogleSignInClient) GoogleSignIn.getClient(this,googleSignInOptions);

        googleSigninbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SigIn();
                if (checkBox.isChecked()){

                  SigIn();

                }
                else {
                    Toast.makeText(LoginActivity.this,"Accept Terms & conditions",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void SigIn(){
         Intent GsignIntent = googleSignInClient.getSignInIntent();
         startActivityForResult(GsignIntent, RC_SIGN_IN);
       }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                handleSignInResult(task);
            }
            catch (Exception e){
            }
           }

//

    }
    private void handleSignInResult(Task <GoogleSignInAccount> completedTask){
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText( LoginActivity.this,"Signed In Successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);

//

        }
        catch (ApiException e){
            Toast.makeText( LoginActivity.this,"Signed In Failed", Toast.LENGTH_SHORT).show();

        }
    }

    private void FirebaseGoogleAuth (GoogleSignInAccount acct){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {


            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                      String  emailS = user.getEmail();
                    String uid = user.getUid();
                    String image = user.getPhotoUrl().toString();
                    String displayS = user.getDisplayName();
                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {


                    HashMap<String, String> hashmapbasic = new HashMap<>();
                    hashmapbasic.put("displayname",displayS);
                    hashmapbasic.put("email", emailS);
                    hashmapbasic.put("uid", uid);
                    hashmapbasic.put("image", image);
                    hashmapbasic.put("onlineStatus", "Online");
                    hashmapbasic.put("Follower", "");
                    hashmapbasic.put("Follow_Req_Pending", "");
                    hashmapbasic.put("Follow_Req_Sends", "");

                    String default_Bio = "Hey! I am in Yaari";
                        assert displayS != null;
                        String default_Username = displayS.toLowerCase();


                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("Users");

                        reference.child(uid).setValue(hashmapbasic);

                        HashMap<String, String> hashmapabout = new HashMap<>();
                        hashmapabout.put("username",default_Username);
                        hashmapabout.put("bio",default_Bio);
                        hashmapabout.put("dob","");
                        hashmapabout.put("intrest","");
                        hashmapabout.put("relationshipstatus","");
                        hashmapabout.put("city","");

                        reference.child(uid).child("About").setValue(hashmapabout);




                    }

                    finish();

                } else {

                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }
}