package com.example.apptrail4.notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String tokenRefresh) {
        super.onNewToken(tokenRefresh);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser!= null){

            updateToken(tokenRefresh);
        }
    }



    private void updateToken(String tokenRefresh) {

      FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Tokens");
       Token token = new Token(tokenRefresh);
       databaseRef.child(currentUser.getUid()).setValue(token);

    }
}
