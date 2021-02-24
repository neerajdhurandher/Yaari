package com.example.apptrail4;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class udetailsf extends Fragment {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser currentUser;
    private Button btnLogout;
   private ViewPager viewpager_ud;
   private TextView textView, DisplayName, GivenName, EmailId, PersonId;
   private ImageView UserDp;
   private   Uri imageUri;

private FirebaseDatabase Fdatabase;
private DatabaseReference userRef;
private GoogleSignInClient mGoogleSignInClient;
private static final String USER = "users";
private   String emailstr;




public udetailsf() {
        // Required empty public constructor

        }


@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {


    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_udetailsf, container, false);

}


    }



//        DisplayName = DisplayName.findViewById(R.id.DisplayNametxt);
//        GivenName = GivenName.findViewById(R.id.GivenNametxt);
//        EmailId = EmailId.findViewById(R.id.EmailIdtxt);
//        UserDp = UserDp.findViewById(R.id.UserDpId);
//
//        Query query = userRef.orderByChild("email").equalTo(currentUser.getEmail());
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds: snapshot.getChildren()){
//
//                    String name = ""+ ds.child("name").getValue();
//                    String email = ""+ ds.child("email").getValue();
//                    String dob = ""+ ds.child("dob").getValue();
//                    String image = ""+ ds.child("image").getValue();
//
//                    DisplayName.setText(name);
//                    EmailId.setText(email);
////                    try {
////                        Picasso.get().load(image).into(UserDp);
////                    }
////                    catch (Exception e ){
////                        Picasso.get().load(R.drawable.user_icon).into(UserDp);
////                    }
//
//                    }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//    public void userInformation() {
//
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
//        if (acct != null) {
//            String personName = acct.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();
//            String personPhotoUri = personPhoto.toString();
//
//
//            DisplayName = DisplayName.findViewById(R.id.DisplayNametxt);
//            DisplayName.setText(personName);
//            GivenName = GivenName.findViewById(R.id.GivenNametxt);
//            GivenName.setText(personGivenName);
//            EmailId = EmailId.findViewById(R.id.EmailIdtxt);
//            EmailId.setText(personEmail);
//            PersonId = PersonId.findViewById(R.id.PersonIdtxt);
//            PersonId.setText(personId);
//            UserDp = UserDp.findViewById(R.id.UserDpId);
//            UserDp.setImageURI(personPhoto);
//        }
//    }





//}
