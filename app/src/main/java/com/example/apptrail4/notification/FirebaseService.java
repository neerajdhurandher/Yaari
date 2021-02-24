package com.example.apptrail4.notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();

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
